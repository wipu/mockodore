package org.fluentjava.mockodore.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.Debug;
import org.fluentjava.mockodore.api.assylang.DebugContext;
import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class C64SimulatorTest {

	private static final RawAddress address$1000 = RawAddress.named(0x1000);
	private static final RawAddress address$2000 = RawAddress.named(0x2000);
	private C64AssyLangForProgram p;
	private C64Simulator sim;

	@BeforeEach
	public void before() {
		p = MockodoreProgram.with();
		sim = new C64Simulator();
	}

	@Test
	public void loadWritesPrgToCorrectPlace() {
		p.startAddress(address$1000);
		p.nop();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());

		assertEquals("EA 60", sim.hexDump(address$1000, 2));
	}

	@Test
	public void simpleSysSetsRunningFlag() {
		p.startAddress(address$1000);
		p.nop();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		assertFalse(sim.isSimpleSysRunning());
		sim.simpleSys(address$1000);
		assertTrue(sim.isSimpleSysRunning());
	}

	@Test
	public void simpleSysAndTwoTicksEvaluateNop() {
		p.startAddress(address$2000);
		p.nop();

		sim.loadPrg(p.end().asPrgBytes());
		assertEquals("(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $2000 1)",
				sim.recentEventLog());
		sim.simpleSys(address$2000);
		assertEquals("(0 simpleSys $2000)(0 $2000 nop)", sim.recentEventLog());
		sim.tick();
		assertEquals("", sim.recentEventLog());
		sim.tick();
		assertEquals("(2 $2001 brk)", sim.recentEventLog());
	}

	@Test
	public void evaluationOfNopRts() {
		p.startAddress(address$2000);
		p.nop();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$2000);
		// evaluate NOP
		sim.tick().tick();
		// almost evaluate RTS
		sim.tick().tick().tick().tick().tick();
		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $2000 2)(0 simpleSys $2000)(0 $2000 nop)(2 $2001 rts)",
				sim.recentEventLog());
		assertTrue(sim.isSimpleSysRunning());
		// finish RTS and the whole program
		sim.tick();
		assertEquals("(8 rtsSys)", sim.recentEventLog());
		assertFalse(sim.isSimpleSysRunning());
	}

	@Test
	public void whileRunningLoopTickingNopNopRts() {
		p.startAddress(address$2000);
		p.nop();
		p.nop();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$2000);
		while (sim.isSimpleSysRunning()) {
			sim.tick();
		}
		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $2000 3)(0 simpleSys $2000)(0 $2000 nop)(2 $2001 nop)(4 $2002 rts)(10 rtsSys)",
				sim.recentEventLog());
	}

	@Test
	public void tickThrowsAtUnsupportedOperation() {
		p.startAddress(address$2000);
		p.nop();
		p.data(0x8B); // XAA: illegal OP
		p.nop();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$2000);
		sim.tick();
		assertTrue(sim.isSimpleSysRunning());
		sim.tick();
		assertTrue(sim.isSimpleSysRunning());
		try {
			sim.tick();
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("Who called ?:8b", e.getMessage());
		}
	}

	// JSR, RTS and stack

	@Test
	public void jsrPushesPcPlus2ToStackAndJumps() {
		Label sub = Label.named("sub");
		p.startAddress(address$2000);
		p.jsr(sub);
		p.nop();
		p.label(sub);
		p.nop();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$2000);

		assertEquals(0x2000, sim.pc().value());
		assertEquals(0x20, sim.sr().signedByte());
		assertEquals(0xFF, sim.sp().uInt());

		sim.tick(Op.JSR.cycles());

		assertEquals(0x2004, sim.pc().value());
		assertEquals(0xFD, sim.sp().uInt());
		assertEquals("00 02 20", sim.hexDump(RawAddress.named(0x01FD), 3));
	}

	@Test
	public void jsrAndRtsWorkAndLeaveStackUnaffected() {
		Label sub = Label.named("sub");
		p.startAddress(address$1000);
		p.jsr(sub);
		p.nop();
		p.rts();
		p.label(sub);
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$1000);
		sim.tick(Op.JSR, Op.RTS, Op.NOP, Op.RTS);

		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $1000 6)(0 simpleSys $1000)(0 $1000 jsr $1005)(6 $1FF:=#$10)(6 SP=$FE)(6 $1FE:=#$02)(6 SP=$FD)(6 $1005 rts)(12 SP=$FE)(12 SP=$FF)(12 $1003 nop)(14 $1004 rts)(20 rtsSys)",
				sim.recentEventLog());
		assertEquals(0xFF, sim.sp().uInt());
	}

	@Test
	public void phaPushesAcc() {
		p.startAddress(address$2000);
		p.lda(0x01);
		p.pha();

		sim.load(p.end());
		sim.simpleSys(address$2000);
		// we cannot run until rts since we have polluted the stack:
		sim.tick(Op.LDA_IMMEDIATE);
		sim.tick(Op.PHA);

		assertEquals(0xFE, sim.sp().uInt());
		assertEquals("00 01", sim.hexDump(RawAddress.named(0x01FE), 2));
	}

	@Test
	public void plaPullsAccAndWritesZ() {
		p.startAddress(address$2000);
		p.lda(0x80);
		p.pha();
		p.lda(0x02); // modify a, unset Z
		p.pla();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.sp().uInt());
		assertEquals("00 80", sim.hexDump(RawAddress.named(0x01FE), 2));
		assertEquals(0x80, sim.a().uInt());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void secAndClcAffectTheCarryFlag() {
		p.startAddress(address$1000);
		p.sec();
		p.clc();
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$1000);

		byte originalSr = sim.sr().signedByte();
		assertFalse(sim.sr().carry());
		assertEquals(0, sim.sr().carryAsZeroOrOne());

		sim.tick(Op.SEC);
		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $1000 3)(0 simpleSys $1000)(0 $1000 sec)(2 SR:=$21 [nv-bdizC])(2 $1001 clc)",
				sim.recentEventLog());
		assertTrue(sim.sr().carry());
		assertEquals(1, sim.sr().carryAsZeroOrOne());

		sim.tick(Op.CLC);
		assertEquals("(4 SR:=$20 [nv-bdizc])(4 $1002 rts)",
				sim.recentEventLog());
		assertFalse(sim.sr().carry());
		assertEquals(originalSr, sim.sr().signedByte());
	}

	@Test
	public void ldaFromLabelWritesToAcc() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(data);
		p.rts();
		p.label(data);
		p.data(0x02);

		assertEquals(0x00, sim.a().uInt());
		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSys(address$1000);
		sim.tick(Op.LDA_ABS);

		assertEquals(0x02, sim.a().uInt());
	}

	@Test
	public void ldxFromLabelWritesToX() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(data);
		p.rts();
		p.label(data);
		p.data(0x03);

		assertEquals(0x00, sim.x().uInt());
		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x03, sim.x().uInt());
	}

	@Test
	public void staToRawAddressWritesFromAcc() {
		Label from = Label.named("from");
		RawAddress to = RawAddress.named(0x2000);
		p.startAddress(address$1000);
		p.lda(from);
		p.sta(to);
		p.rts();
		p.label(from).data(0x03);

		assertEquals("00", sim.hexDump(to, 1));
		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("03", sim.hexDump(to, 1));
	}

	@Test
	public void stxToLabelWritesFromX() {
		Label to = Label.named("to");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.stx(to);
		p.rts();
		p.label(to).data(0x00);

		sim.load(p.end());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("01", sim.hexDump(to, 1));
	}

	@Test
	public void styToLabelWritesFromY() {
		Label to = Label.named("to");
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.sty(to);
		p.rts();
		p.label(to).data(0x00);

		sim.load(p.end());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("01", sim.hexDump(to, 1));
	}

	@Test
	public void styZp() {
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.sty(ZeroPage.xFB);
		p.rts();

		sim.load(p.end());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("01", sim.hexDump(ZeroPage.xFB, 1));
	}

	@Test
	public void staAbsPlusX() {
		RawAddress to = RawAddress.named(0x2000);
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ldx(0x02);
		p.sta(to.plusX());
		p.rts();

		assertEquals("00 00 00 00", sim.hexDump(to, 4));
		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("00 00 01 00", sim.hexDump(to, 4));
	}

	@Test
	public void staAbsPlusY() {
		RawAddress to = RawAddress.named(0x2000);
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ldy(0x02);
		p.sta(to.plusY());
		p.rts();

		assertEquals("00 00 00 00", sim.hexDump(to, 4));
		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("00 00 01 00", sim.hexDump(to, 4));
	}

	@Test
	public void incAbsPlusXWritesZ() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01); // also unsets Z
		p.inc(data.plusX());
		p.rts();
		p.label(data).data(0x00, 0xFF, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00 00 00", sim.hexDump(data, 3));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void incAbsPlusXWritesN() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01); // also unsets N
		p.inc(data.plusX());
		p.rts();
		p.label(data).data(0x00, 0x7F, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00 80 00", sim.hexDump(data, 3));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void adcAbsPlusX() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.lda(0x7F);
		p.adc(data.plusX());
		p.rts();
		p.label(data).data(0x00, 0x02, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x81, sim.a().uInt());
		assertEquals("NV-bdizc", sim.sr().toString());
	}

	@Test
	public void sbcAbsPlusX() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.lda(0x7F);
		p.sbc(data.plusX());
		p.rts();
		p.label(data).data(0x00, 0x02, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x7C, sim.a().uInt());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void sbcAbsPlusY() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.lda(0x7F);
		p.sbc(data.plusY());
		p.rts();
		p.label(data).data(0x00, 0x02, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x7C, sim.a().uInt());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void oraAbsPlusX() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.lda(0x80);
		p.ora(data.plusX());
		p.rts();
		p.label(data).data(0x00, 0x01, 0x00);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x81, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void oraAbs() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(0x80);
		p.ora(data);
		p.rts();
		p.label(data).data(0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x81, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void oraZp() {
		p.startAddress(address$1000);
		p.lda(0x01).sta(ZeroPage.xFB);
		p.lda(0x80);
		p.ora(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x81, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void adcWithNoReadNorWriteOfCarry() {
		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		p.startAddress(address$1000);
		p.lda(term1);
		p.adc(term2);
		p.rts();
		p.label(term1).data(1);
		p.label(term2).data(2);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals(3, sim.a().uInt());
		assertFalse(sim.sr().carry());
	}

	@Test
	public void adcWithReadCarryButNoWriteCarry() {
		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		p.startAddress(address$1000);
		p.sec();
		p.lda(term1);
		p.adc(term2);
		p.rts();
		p.label(term1).data(1);
		p.label(term2).data(2);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals(4, sim.a().uInt());
		assertFalse(sim.sr().carry());
	}

	@Test
	public void adcWithWriteCarryButNoReadCarry() {
		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		p.startAddress(address$1000);
		p.lda(term1);
		p.adc(term2);
		p.rts();
		p.label(term1).data(254);
		p.label(term2).data(2);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals(0, sim.a().uInt());
		assertTrue(sim.sr().carry());
	}

	@Test
	public void adcWithReadAndWriteOfCarry() {
		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		p.startAddress(address$1000);
		p.sec();
		p.lda(term1);
		p.adc(term2);
		p.rts();
		p.label(term1).data(254);
		p.label(term2).data(1);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals(0, sim.a().uInt());
		assertTrue(sim.sr().carry());
	}

	@Test
	public void adcOf16BitsWithCarryFromLsbToMsbButNotAfterThat() {
		assertEquals(0x0401, 0x01F0 + 0x0211);

		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		RawAddress sum = RawAddress.named(0x2000);
		p.startAddress(address$1000);

		p.lda(term1);
		p.clc();
		p.adc(term2);
		p.sta(sum);

		p.lda(term1.plus(1));
		p.adc(term2.plus(1));
		p.sta(sum.plus(1));

		p.rts();
		p.label(term1).data(0xF0).data(0x01);
		p.label(term2).data(0x11).data(0x02);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("01 04", sim.hexDump(sum, 2));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void adcOf16BitsWithNoCarryFromLsbToMsbButCarryAfterMsb() {
		assertEquals(0xFE23, 0x7F01 + 0x7F22);

		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		RawAddress sum = RawAddress.named(0x2000);
		p.startAddress(address$1000);

		p.lda(term1);
		p.clc();
		p.adc(term2);
		p.sta(sum);

		p.lda(term1.plus(1));
		p.adc(term2.plus(1));
		p.sta(sum.plus(1));

		p.rts();
		p.label(term1).data(0x01).data(0x7F);
		p.label(term2).data(0x22).data(0x7F);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("23 FE", sim.hexDump(sum, 2));
		assertEquals("NV-bdizc", sim.sr().toString());
	}

	@Test
	public void adcSetsOverflowWhenNeeded() {
		p.startAddress(address$1000);
		p.lda(0x7F);
		p.clc();
		p.adc(0x01);
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertTrue(sim.sr().overflow());
	}

	@Test
	public void adcUnsetsOverflowWhenNeeded() {
		p.startAddress(address$1000);
		p.lda(0x7F);
		p.clc();
		p.adc(0x01);
		p.clc();
		p.adc(0x01);
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertFalse(sim.sr().overflow());
	}

	@Test
	public void sbcOf16BitsWithCarryFromLsbToMsbButNotAfterThat() {
		assertEquals(0x0401, 0x01F0 + 0x0211);
		assertEquals(0x01F0, 0x0401 - 0x0211);

		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		RawAddress difference = RawAddress.named(0x2000);
		p.startAddress(address$1000);

		p.lda(term1);
		p.sec();
		p.sbc(term2);
		p.sta(difference);

		p.lda(term1.plus(1));
		p.sbc(term2.plus(1));
		p.sta(difference.plus(1));

		p.rts();
		p.label(term1).data(0x01).data(0x04);
		p.label(term2).data(0x11).data(0x02);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("F0 01", sim.hexDump(difference, 2));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void sbcOf16BitsWithNoCarryFromLsbToMsbButCarryAfterMsb() {
		assertEquals(0xFE23, 0x7F01 + 0x7F22);
		assertEquals(0x7F01, 0xFE23 - 0x7F22);

		Label term1 = Label.named("term1");
		Label term2 = Label.named("term2");
		RawAddress sum = RawAddress.named(0x2000);
		p.startAddress(address$1000);

		p.lda(term1);
		p.sec();
		p.sbc(term2);
		p.sta(sum);

		p.lda(term1.plus(1));
		p.sbc(term2.plus(1));
		p.sta(sum.plus(1));

		p.rts();
		p.label(term1).data(0x23).data(0xFE);
		p.label(term2).data(0x22).data(0x7F);

		sim.loadPrg(p.end().asPrgBytes());
		sim.simpleSysAndAutoTick(address$1000);

		assertEquals("01 7F", sim.hexDump(sum, 2));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void readMemByLabelFromProgramLoadedAsMoreThanBytes() {
		Label start = Label.named("start");
		Label end = Label.named("end");
		p.startAddress(address$1000);
		p.label(start);
		p.nop();
		p.label(end);
		p.rts();

		sim.load(p.end());
		assertEquals("EA 60", sim.hexDump(start, 2));
		assertEquals("60", sim.hexDump(end, 1));
	}

	@Test
	public void readMemByLabelFailsIfProgramLoadedAsMereBytes() {
		Label start = Label.named("start");
		Label end = Label.named("end");
		p.startAddress(address$1000);
		p.label(start);
		p.nop();
		p.label(end);
		p.rts();

		sim.loadPrg(p.end().asPrgBytes());
		try {
			sim.hexDump(start, 2);
			fail();
		} catch (MockodoreException e) {
			assertEquals("Cannot resolve label without program.",
					e.getMessage());
		}
	}

	@Test
	public void ldaImmediateWritesToAcc() {
		p.startAddress(address$1000);
		p.lda(0x02);
		p.rts();

		assertEquals(0x00, sim.a().uInt());
		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x02, sim.a().uInt());
	}

	@Test
	public void ldaImmediateSetsN() {
		p.startAddress(address$1000);
		p.lda(0x80);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void ldyImmediate() {
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void ldyAbsWritesToY() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldy(label);
		p.rts();
		p.label(label).data(0x01);

		assertEquals(0x00, sim.y().uInt());
		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void ldyZpWritesToY() {
		p.startAddress(address$1000);
		p.lda(0x02).sta(ZeroPage.xFB);
		p.ldy(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void ldaAbsX() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldx(0x02);
		p.lda(array.plusX());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12);

		assertEquals(0x00, sim.a().uInt());
		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x12, sim.a().uInt());
	}

	@Test
	public void nopAbsXReturns() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldx(0x02);
		p.nop(array.plusX());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12);

		sim.loadAndSimpleSysAndAutoTick(p.end());
	}

	@Test
	public void ldaAbsXInterpretesXAsUnsigned() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldx(0x81);
		p.lda(array.plusX());
		p.rts();
		p.label(array);
		for (int i = 0; i < 256; i++) {
			p.data(i);
		}

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x81, sim.a().uInt());
	}

	@Test
	public void ldyAbsX() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldx(0x02);
		p.ldy(array.plusX());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x12, sim.y().uInt());
	}

	@Test
	public void cmpAbsSetsN() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x04);
		p.cmp(array);
		p.rts();
		p.label(array).data(0x05);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpZpSetsN() {
		p.startAddress(address$1000);
		p.lda(0x05).sta(ZeroPage.xFB);
		p.lda(0x04);
		p.cmp(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpZpIndirectPlusYSetsZC() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(array.lsb()).sta(ZeroPage.xFB);
		p.lda(array.msb()).sta(ZeroPage.xFB.plus(1));

		p.ldy(0x01);
		p.lda(0x03);
		p.cmp(ZeroPage.xFB.indirectPlusY());
		p.rts();
		p.label(array).data(0x01, 0x03, 0x01);

		sim.load(p.end());

		sim.simpleSysAndAutoTick();
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void cmpZpXSetsN() {
		p.startAddress(address$1000);
		p.lda(0x05).sta(ZeroPage.xFB.plus(1));
		p.ldx(1);
		p.lda(0x04);
		p.cmp(ZeroPage.xFB.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpAbsXSetsN() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x04);
		p.ldx(0x01);
		p.cmp(array.plusX());
		p.rts();
		p.label(array).data(0x01, 0x05, 0x02);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpAbsYSetsN() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x04);
		p.ldy(0x01);
		p.cmp(array.plusY());
		p.rts();
		p.label(array).data(0x01, 0x05, 0x02);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpAbsXUnsetsN() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x04);
		p.ldx(0x01);
		p.cmp(array.plusX());
		p.rts();
		p.label(array).data(0x05, 0x03, 0x06);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void cmpAbsXSetsZ() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x02);
		p.ldx(0x01);
		p.cmp(array.plusX());
		p.rts();
		p.label(array).data(0x01, 0x02, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void cmpAbsXUnsetsZ() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ldx(0x01);
		p.cmp(array.plusX());
		p.rts();
		p.label(array).data(0x01, 0x02, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cmpAbsXDoesNotSetVUnlikeSbc() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x80);
		p.ldx(0x01);
		p.cmp(array.plusX());
		p.rts();
		p.label(array).data(0x01, 0x02, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void cmpImmediateSetsZ() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.cmp(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void cpxAbsSetsZ() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldx(0x04);
		p.cpx(array);
		p.rts();
		p.label(array).data(0x05);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cpxImmediateSetsZ() {
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.cpx(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void cpyImmediateSetsZ() {
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.cpy(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void ldaAbsY() {
		Label y = Label.named("y");
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldy(y);
		p.lda(array.plusY());
		p.rts();
		p.label(y).data(0x01);
		p.label(array).data(0x10, 0x11, 0x12);

		assertEquals(0x00, sim.a().uInt());
		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x11, sim.a().uInt());
	}

	@Test
	public void ldxAbsY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.ldx(array.plusY());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x11, sim.x().uInt());
	}

	@Test
	public void staZeroPage() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZeroPage.xFB);
		p.rts();

		sim.load(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		sim.simpleSysAndAutoTick();
		assertEquals("01", sim.hexDump(ZeroPage.xFB, 1));
	}

	@Test
	public void stxZeroPage() {
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.stx(ZeroPage.xFB);
		p.rts();

		sim.load(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		sim.simpleSysAndAutoTick();
		assertEquals("01", sim.hexDump(ZeroPage.xFB, 1));
	}

	@Test
	public void rolZeroPageNoReadCarryAndSetCarry() {
		p.startAddress(address$1000);
		p.lda(0xA1);
		p.sta(ZeroPage.xFB);
		p.lda(0x00); // unset N
		p.rol(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("42", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void rorZPNoReadCarry() {
		p.startAddress(address$1000);
		p.lda(0x42);
		p.sta(ZeroPage.xFB);
		p.clc();
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("21", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rorZPReadsCarry() {
		p.startAddress(address$1000);
		p.lda(0x42);
		p.sta(ZeroPage.xFB);
		p.sec();
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("A1", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rorZPSetsCarry() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZeroPage.xFB);
		p.clc();
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void rorZPUnsetsCarry() {
		p.startAddress(address$1000);
		p.lda(0x02);
		p.sta(ZeroPage.xFB);
		p.sec();
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("81", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rorZPSetsZero() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZeroPage.xFB);
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void rorSetsZeroAndCarry() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ror();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().value().uInt());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void rorAbsXSetsZero() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(1);
		p.ror(data.plusX());
		p.rts();
		p.label(data).data(0xFF, 1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("FF 00", sim.hexDump(data, 2));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void rorAbsSetsZero() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ror(data);
		p.rts();
		p.label(data).data(1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(data, 1));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void rorZPSetsNegative() {
		p.startAddress(address$1000);
		p.lda(0x00);
		p.sta(ZeroPage.xFB);
		p.sec();
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("80", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rorZPUnsetsNegative() {
		p.startAddress(address$1000);
		p.lda(0x80);
		p.sta(ZeroPage.xFB);
		p.lda(0xFF); // set N
		p.ror(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("40", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void lsrZeroPageThatSetsCarry() {
		p.startAddress(address$1000);
		p.lda(0xA1);
		p.sta(ZeroPage.xFB);
		p.clc();
		p.lsr(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("50", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void lsrZeroPageThatUnsetsCarry() {
		p.startAddress(address$1000);
		p.lda(0xA0);
		p.sta(ZeroPage.xFB);
		p.sec();
		p.lsr(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("50", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void lsrZeroPageThatSetsZero() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZeroPage.xFB);
		p.lda(0x01); // unset Z
		p.lsr(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void lsrZeroPageThatUnsetsNegative() {
		p.startAddress(address$1000);
		p.lda(0xFF);
		p.sta(ZeroPage.xFB);
		p.lda(0xFF); // set N
		p.lsr(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("7F", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void rolZeroPageNoReadCarryAndSetNegative() {
		p.startAddress(address$1000);
		p.lda(0x41);
		p.sta(ZeroPage.xFB);
		p.lda(0x00); // unset N
		p.rol(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("82", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rolZeroPageReadCarryAndUnsetCarryAndNegative() {
		p.startAddress(address$1000);
		p.lda(0x31);
		p.sta(ZeroPage.xFB);
		p.sec(); // set C and
		p.lda(0xFF); // set N
		p.rol(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("63", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void rolZeroPageSetsZero() {
		p.startAddress(address$1000);
		p.lda(0x00);
		p.sta(ZeroPage.xFB);
		p.lda(0x01); // unset Z
		p.rol(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void rolSetsZeroAndCarry() {
		p.startAddress(address$1000);
		p.lda(0x80);
		p.rol();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().value().uInt());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void staZeroPageIndirectPlusY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(array.lsb()).sta(ZeroPage.xFB);
		p.lda(array.msb()).sta(ZeroPage.xFB.plus(1));
		p.ldy(0x01);
		p.lda(0xFF);
		p.sta(ZeroPage.xFB.indirectPlusY());
		p.rts();
		p.label(array).data(0x01, 0x02, 0x03);

		sim.load(p.end());

		sim.simpleSysAndAutoTick();
		assertEquals("01 FF 03", sim.hexDump(array, 3));
	}

	@Test
	public void sloZeroPageIndirectPlusY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(array.lsb()).sta(ZeroPage.xFB);
		p.lda(array.msb()).sta(ZeroPage.xFB.plus(1));
		p.ldy(0x01);
		p.lda(0x80);
		p.slo(ZeroPage.xFB.indirectPlusY());
		p.rts();
		p.label(array).data(0x01, 0x02, 0x03);

		sim.load(p.end());

		sim.simpleSysAndAutoTick();
		assertEquals("01 04 03", sim.hexDump(array, 3));
		assertEquals(0x84, sim.a().uInt());
	}

	@Test
	public void ldaZeroPage() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZeroPage.xFB);
		p.lda(0x00);
		p.lda(ZeroPage.xFB);
		p.rts();

		sim.load(p.end());

		assertEquals("00", sim.hexDump(ZeroPage.xFB, 1));
		sim.simpleSys(address$1000);
		sim.tick(Op.LDA_IMMEDIATE, Op.STA_ZEROPAGE, Op.LDA_IMMEDIATE);
		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $1000 9)(0 simpleSys $1000)(0 $1000 lda #$01)(2 A=$01)(2 SR:=$20 [nv-bdizc])(2 SR:=$20 [nv-bdizc])(2 $1002 sta $FB)(5 $FB:=#$01)(5 $1004 lda #$00)(7 A=$00)(7 SR:=$22 [nv-bdiZc])(7 SR:=$22 [nv-bdiZc])(7 $1006 lda $FB)",
				sim.recentEventLog());
		assertEquals("01", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals(0x00, sim.a().uInt());

		sim.tick(Op.LDA_ZEROPAGE, Op.RTS);
		assertEquals(0x01, sim.a().uInt());
		assertEquals(
				"(10 A=$01)(10 SR:=$20 [nv-bdizc])(10 SR:=$20 [nv-bdizc])(10 $1008 rts)(16 rtsSys)",
				sim.recentEventLog());
	}

	@Test
	public void ldaZeroPageIndirectPlusY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(array.lsb()).sta(ZeroPage.xFB);
		p.lda(array.msb()).sta(ZeroPage.xFB.plus(1));
		p.lda(0x00);
		p.ldy(0x02);
		p.lda(ZeroPage.xFB.indirectPlusY());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12, 0x13);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x12, sim.a().signedByte());
	}

	@Test
	public void adcImmediate() {
		p.startAddress(address$1000);
		p.lda(0x02);
		p.adc(0x03);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x05, sim.a().uInt());
	}

	@Test
	public void sbcImmediate() {
		p.startAddress(address$1000);
		p.sec();
		p.lda(0x02);
		p.sbc(0x03);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void eorImmNonZeroNonNegative() {
		p.startAddress(address$1000);
		p.lda(0x06);
		p.eor(0x03);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x05, sim.a().uInt());
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void eorImmZero() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.eor(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void eorImmNegative() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.eor(0x80);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x81, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void adcZp() {
		p.startAddress(address$1000);
		p.lda(0x02).sta(ZeroPage.xFB);
		p.lda(0x03);
		p.adc(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x05, sim.a().uInt());
		assertEquals("02", sim.hexDump(ZeroPage.xFB, 1));
	}

	@Test
	public void adcZpIndirectPlusY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(array.lsb()).sta(ZeroPage.xFB);
		p.lda(array.msb()).sta(ZeroPage.xFB.plus(1));
		p.lda(0x11);
		p.ldy(0x02);
		p.adc(ZeroPage.xFB.indirectPlusY());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12, 0x13);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x23, sim.a().signedByte());
	}

	@Test
	public void adcZpX() {
		p.startAddress(address$1000);
		p.lda(0x02).sta(ZeroPage.xFB.plus(1));
		p.ldx(1);
		p.lda(0x03);
		p.adc(ZeroPage.xFB.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x05, sim.a().uInt());
		assertEquals("00 02", sim.hexDump(ZeroPage.xFB, 2));
	}

	@Test
	public void sbcZp() {
		p.startAddress(address$1000);
		p.lda(0x03).sta(ZeroPage.xFB);
		p.lda(0x02);
		p.sec();
		p.sbc(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void sbcZpX() {
		p.startAddress(address$1000);
		p.lda(0x03).sta(ZeroPage.xFB.plus(1));
		p.lda(0x02);
		p.ldx(1);
		p.sec();
		p.sbc(ZeroPage.xFB.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void adcAbsY() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.ldy(0x01);
		p.lda(0x02);
		p.adc(array.plusY());
		p.rts();
		p.label(array).data(0x10, 0x11, 0x12);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x13, sim.a().uInt());
	}

	@Test
	public void ldxImmediate() {
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.x().uInt());
	}

	@Test
	public void oraImmediateWritesA() {
		p.startAddress(address$1000);
		p.lda(0xE0);
		p.ora(0x13);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xF3, sim.a().uInt());
	}

	@Test
	public void oraImmediateSetsZ() {
		p.startAddress(address$1000);
		p.lda(0x00);
		p.ora(0x00);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().uInt());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void oraImmediateUnsetsZ() {
		p.startAddress(address$1000);
		p.lda(0x00);
		p.ora(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.a().uInt());
		assertFalse(sim.sr().zero());
	}

	@Test
	public void oraImmediateSetsN() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ora(0xF0);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xF1, sim.a().uInt());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void oraImmediateUnsetsN() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ora(0x70);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x71, sim.a().uInt());
		assertFalse(sim.sr().negative());
	}

	@Test
	public void staZeroPagePlusXWithXValue0() {
		ZeroPage zp0 = ZeroPage.xFB;

		p.startAddress(address$1000);
		p.ldx(0x00);
		p.lda(0xFF);
		p.sta(zp0.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("FF 00 00 00", sim.hexDump(zp0, 4));
	}

	@Test
	public void staZeroPagePlusXWithXValue2() {
		ZeroPage zp0 = ZeroPage.xFB;

		p.startAddress(address$1000);
		p.ldx(0x02);
		p.lda(0xFF);
		p.sta(zp0.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00 00 FF 00", sim.hexDump(zp0, 4));
	}

	@Test
	public void ldaZeroPagePlusXWithXValue2() {
		ZeroPage zp0 = ZeroPage.xFB;

		p.startAddress(address$1000);
		// init data:
		p.lda(0xFF);
		p.sta(zp0.plus(2));
		// lda it
		p.ldx(0x02);
		p.lda(zp0.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void ldyZeroPagePlusXWithXValue2() {
		ZeroPage zp0 = ZeroPage.xFB;

		p.startAddress(address$1000);
		// init data:
		p.lda(0xFF);
		p.sta(zp0.plus(2));
		// lda it
		p.ldx(0x02);
		p.ldy(zp0.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.y().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void decAbsPlusXWithXValue2() {
		Label data = Label.named("data");

		p.startAddress(address$1000);
		p.ldx(0x02);
		p.dec(data.plusX());
		p.rts();
		p.label(data).data(2, 3, 4, 5);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("02 03 03 05", sim.hexDump(data, 4));
	}

	@Test
	public void decAbsPlusXSetsZero() {
		Label data = Label.named("data");

		p.startAddress(address$1000);
		p.ldx(0x01); // also unsets Z
		p.dec(data.plusX());
		p.rts();
		p.label(data).data(0, 1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void decAbsSetsZero() {
		Label data = Label.named("data");

		p.startAddress(address$1000);
		p.lda(0x01); // unset Z
		p.dec(data);
		p.rts();
		p.label(data).data(0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(data, 1));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void incAbsSetsZero() {
		Label data = Label.named("data");

		p.startAddress(address$1000);
		p.lda(0x01); // unset Z
		p.inc(data);
		p.rts();
		p.label(data).data(0xFF);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(data, 1));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void incZpSetsNegative() {
		ZeroPage ZP1 = ZeroPage.x01;

		p.startAddress(address$1000);
		p.lda(0x7F);
		p.sta(ZP1);
		p.inc(ZP1);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("80", sim.hexDump(ZP1, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void decZpSetsZero() {
		ZeroPage ZP1 = ZeroPage.x01;

		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(ZP1);
		p.dec(ZP1);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("00", sim.hexDump(ZP1, 1));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void decZpXSetsZero() {
		ZeroPage ZP1 = ZeroPage.x01;

		p.startAddress(address$1000);
		// init zp data
		p.lda(0x01);
		p.sta(ZP1);
		p.sta(ZP1.plus(1));
		// dec it
		p.ldx(1);
		p.dec(ZP1.plusX());
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("01 00", sim.hexDump(ZP1, 2));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void decAbsPlusXSetsNegative() {
		Label data = Label.named("data");

		p.startAddress(address$1000);
		p.ldx(0x00); // also unsets N and sets Z
		p.dec(data.plusX());
		p.rts();
		p.label(data).data(0, 0);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void aslImpliedWithoutCarry() {
		p.startAddress(address$1000);
		p.lda(0x03);
		p.sec();
		p.asl();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x06, sim.a().uInt());
		assertFalse(sim.sr().carry());
	}

	@Test
	public void aslImpliedWithCarry() {
		p.startAddress(address$1000);
		p.lda(0x81);
		p.clc();
		p.asl();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.a().uInt());
		assertTrue(sim.sr().carry());
	}

	@Test
	public void aslAbsXThatSetsCarry() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.asl(data.plusX());
		p.rts();
		p.label(data).data(0x01, 0x81, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("01 02 01", sim.hexDump(data, 3));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void aslAbsThatSetsCarry() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.asl(data);
		p.rts();
		p.label(data).data(0x81);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("02", sim.hexDump(data, 1));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void aslZpThatSetsCarry() {
		p.startAddress(address$1000);
		p.lda(0x81).sta(ZeroPage.xFB);
		p.asl(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("02", sim.hexDump(ZeroPage.xFB, 1));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void aslAbsXThatSetsNegative() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.asl(data.plusX());
		p.rts();
		p.label(data).data(0x01, 0x7F, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("01 FE 01", sim.hexDump(data, 3));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void aslAbsXThatSetsZeroAsWellAsCarry() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.asl(data.plusX());
		p.rts();
		p.label(data).data(0x01, 0x80, 0x01);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("01 00 01", sim.hexDump(data, 3));
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void tay() {
		p.startAddress(address$1000);
		p.lda(0x12);
		p.tay();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x12, sim.y().uInt());
	}

	@Test
	public void tax() {
		p.startAddress(address$1000);
		p.lda(0x12);
		p.tax();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x12, sim.x().uInt());
	}

	@Test
	public void tyaWhenZero() {
		p.startAddress(address$1000);
		p.lda(0x12);
		p.tya();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void tyaWhenNegative() {
		p.startAddress(address$1000);
		p.ldy(-1);
		p.lda(0x00); // set Z, unset N
		p.tya();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void tyaWhenNonZeroPositive() {
		p.startAddress(address$1000);
		p.ldy(1);
		p.lda(0xFF); // set N
		p.tya();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.a().uInt());
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void txaCopiesXToA() {
		p.startAddress(address$1000);
		p.ldx(0x12);
		p.txa();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x12, sim.a().uInt());
	}

	@Test
	public void txaSetsN() {
		p.startAddress(address$1000);
		p.ldx(0xFF);
		p.txa();
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(true, sim.sr().negative());
	}

	@Test
	public void jmpLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.jmp(label);
		p.ldy(0x01);
		p.rts();
		p.label(label);
		p.lda(0x02);
		p.rts();
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.a().uInt());
		assertEquals(0x00, sim.y().uInt());
		assertEquals(
				"(0 SR:=$20 [nv-bdizc])(0 SP=$FF)(0 loadPrg $1000 9)(0 simpleSys $1000)(0 $1000 jmp $1006)(3 $1006 lda #$02)(5 A=$02)(5 SR:=$20 [nv-bdizc])(5 SR:=$20 [nv-bdizc])(5 $1008 rts)(11 rtsSys)",
				sim.recentEventLog());
	}

	@Test
	public void jmpLabelIndirect() {
		Label pointer = Label.named("pointer");
		Label sub = Label.named("sub");
		p.startAddress(address$1000);

		p.lda(sub.lsb());
		p.sta(pointer);
		p.lda(sub.msb());
		p.sta(pointer.plus(1));
		p.jmp(pointer.indirect());
		p.ldy(0x01);
		p.rts();
		p.label(sub);
		p.ldy(0x02);
		p.rts();
		p.label(pointer);
		p.data(0, 0);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void ldaSetsZero() {
		p.startAddress(address$1000);
		p.lda(0);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().uInt());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void ldxSetsZero() {
		p.startAddress(address$1000);
		p.ldx(0);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.x().uInt());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void ldySetsZero() {
		p.startAddress(address$1000);
		p.ldy(0);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.y().uInt());
		assertTrue(sim.sr().zero());
	}

	@Test
	public void ldaSetsNegative() {
		p.startAddress(address$1000);
		p.lda(-1);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void ldxSetsNegative() {
		p.startAddress(address$1000);
		p.ldx(-1);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.x().uInt());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void ldxZpSetsNegative() {
		p.startAddress(address$1000);
		p.lda(-1).sta(ZeroPage.xFB);
		p.ldx(ZeroPage.xFB);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.x().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void ldySetsNegative() {
		p.startAddress(address$1000);
		p.ldy(-1);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.y().uInt());
		assertTrue(sim.sr().negative());
	}

	@Test
	public void bmiWhenNegative() {
		Label neg = Label.named("neg");
		p.startAddress(address$1000);
		p.lda(-1);
		p.bmi(neg);
		p.ldy(0x01);
		p.rts();
		p.label(neg);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bplWhenPositive() {
		Label pos = Label.named("pos");
		p.startAddress(address$1000);
		p.lda(1);
		p.bpl(pos);
		p.ldy(0x01);
		p.rts();
		p.label(pos);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bplWhenZero() {
		Label pos = Label.named("pos");
		p.startAddress(address$1000);
		p.lda(0);
		p.bpl(pos);
		p.ldy(0x01);
		p.rts();
		p.label(pos);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void beqWhenZeroSet() {
		Label eq = Label.named("eq");
		p.startAddress(address$1000);
		p.lda(0);
		p.beq(eq);
		p.ldy(0x01);
		p.rts();
		p.label(eq);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void beqWhenZeroUnset() {
		Label eq = Label.named("eq");
		p.startAddress(address$1000);
		p.lda(1);
		p.beq(eq);
		p.ldy(0x01);
		p.rts();
		p.label(eq);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bneWhenZeroSet() {
		Label neq = Label.named("neq");
		p.startAddress(address$1000);
		p.lda(0);
		p.bne(neq);
		p.ldy(0x01);
		p.rts();
		p.label(neq);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bneWhenZeroUnset() {
		Label neq = Label.named("neq");
		p.startAddress(address$1000);
		p.lda(0x01);
		p.bne(neq);
		p.ldy(0x01);
		p.rts();
		p.label(neq);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bccWhenCarrySet() {
		Label carry = Label.named("carry");
		p.startAddress(address$1000);
		p.sec();
		p.bcc(carry);
		p.ldy(0x01);
		p.rts();
		p.label(carry);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bccWhenCarryUnset() {
		Label carry = Label.named("carry");
		p.startAddress(address$1000);
		p.clc();
		p.bcc(carry);
		p.ldy(0x01);
		p.rts();
		p.label(carry);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bcsWhenCarryUnset() {
		Label carry = Label.named("carry");
		p.startAddress(address$1000);
		p.clc();
		p.bcs(carry);
		p.ldy(0x01);
		p.rts();
		p.label(carry);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bcsWhenCarrySet() {
		Label carry = Label.named("carry");
		p.startAddress(address$1000);
		p.sec();
		p.bcs(carry);
		p.ldy(0x01);
		p.rts();
		p.label(carry);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bmiBackwardsWhenNegative() {
		Label check = Label.named("check");
		Label neg = Label.named("neg");
		p.startAddress(address$1000);
		p.jmp(check);
		p.label(neg);
		p.ldy(0x02);
		p.rts();
		p.label(check);
		p.lda(-1);
		p.bmi(neg);
		p.ldy(0x01);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bmiWhenPositive() {
		Label neg = Label.named("neg");
		p.startAddress(address$1000);
		p.lda(1);
		p.bmi(neg);
		p.ldy(0x01);
		p.rts();
		p.label(neg);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bmiWhenZero() {
		Label neg = Label.named("neg");
		p.startAddress(address$1000);
		p.lda(0);
		p.bmi(neg);
		p.ldy(0x01);
		p.rts();
		p.label(neg);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bplWhenNotPositive() {
		Label pos = Label.named("pos");
		p.startAddress(address$1000);
		p.lda(-1);
		p.bpl(pos);
		p.ldy(0x01);
		p.rts();
		p.label(pos);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void dexAffectsX() {
		p.startAddress(address$1000);
		p.dex();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.x().uInt());
	}

	@Test
	public void deyAffectsY() {
		p.startAddress(address$1000);
		p.dey();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.y().uInt());
	}

	@Test
	public void dexSetsStatusN() {
		p.startAddress(address$1000);
		p.ldx(0x00);
		p.dex();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertTrue(sim.sr().negative());
	}

	@Test
	public void dexUnsetsStatusN() {
		p.startAddress(address$1000);
		p.ldx(0x01);
		p.dex();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertFalse(sim.sr().negative());
	}

	@Test
	public void andAbsX() {
		Label array = Label.named("array");
		p.startAddress(address$1000);
		p.lda(0x33);
		p.ldx(0x01);
		p.and(array.plusX());
		p.rts();
		p.label(array).data(0xF1, 0x1F);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x13, sim.a().uInt());
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void andAbs() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(0x33);
		p.and(data);
		p.rts();
		p.label(data).data(0x1F);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x13, sim.a().uInt());
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void andAbsSetsZero() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(0x0F);
		p.and(data);
		p.rts();
		p.label(data).data(0xF0);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.a().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void andAbsSetsNegative() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(0x8F);
		p.and(data);
		p.rts();
		p.label(data).data(0xF0);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x80, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void andImm() {
		p.startAddress(address$1000);
		p.lda(0x33);
		p.and(0x1F);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x13, sim.a().uInt());
	}

	@Test
	public void lsrZeroIsZeroWithStatusZero() {
		p.startAddress(address$1000);
		p.lda(0x00);
		p.ldy(0x01); // unset Z
		p.lsr();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.a().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void lsrOneIsZeroWithStatusZeroAndCarry() {
		p.startAddress(address$1000);
		p.lda(0x01);
		p.ldy(0x01); // unset Z
		p.lsr();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.a().uInt());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void lsrThreeIsOneWithStatusCarry() {
		p.startAddress(address$1000);
		p.lda(0x03);
		p.lsr();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x01, sim.a().uInt());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void lsrAbsX() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.ldx(1);
		p.lsr(data.plusX());
		p.rts();
		p.label(data).data(5, 3, 5);

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals("05 01 05", sim.hexDump(data, 3));
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void inxSetsNeg() {
		p.startAddress(address$1000);
		p.ldx(-2);
		p.inx();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0xFF, sim.x().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void inySetsNeg() {
		p.startAddress(address$1000);
		p.ldy(-2);
		p.iny();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0xFF, sim.y().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void inxSetsZero() {
		p.startAddress(address$1000);
		p.ldx(-1);
		p.inx();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.x().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void inySetsZero() {
		p.startAddress(address$1000);
		p.ldy(-1);
		p.iny();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.y().uInt());
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void sbc2MinusAbs1AfterSec() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(2);
		p.sec();
		p.sbc(term);
		p.rts();
		p.label(term).data(1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(1, sim.a().uInt());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void sbc3MinusAbs1AfterSec() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(3);
		p.sec();
		p.sbc(term);
		p.rts();
		p.label(term).data(1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(2, sim.a().uInt());
		assertEquals("nv-bdizC", sim.sr().toString());
	}

	@Test
	public void sbc2MinusAbs1AfterClc() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(2);
		p.clc();
		p.sbc(term);
		p.rts();
		p.label(term).data(1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0, sim.a().uInt());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void sbc2MinusAbs2AfterSecSetsZero() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(2);
		p.sec();
		p.sbc(term);
		p.rts();
		p.label(term).data(2);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0, sim.a().uInt());
		assertEquals("nv-bdiZC", sim.sr().toString());
	}

	@Test
	public void sbc1MinusAbs2AfterSecSetsNegative() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(1);
		p.sec();
		p.sbc(term);
		p.rts();
		p.label(term).data(2);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void sbc128MinusAbs1AfterSec() {
		Label term = Label.named("term");
		p.startAddress(address$1000);
		p.lda(0x80);
		p.sec();
		p.sbc(term);
		p.rts();
		p.label(term).data(1);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x7F, sim.a().uInt());
		assertEquals("nV-bdizC", sim.sr().toString());
	}

	/**
	 * From c64-wiki
	 */
	@Test
	public void cmpAndBccForUnsignedComparisonWhenAccSmaller() {
		Label bccTrue = Label.named("bccTrue");
		p.startAddress(address$1000);
		p.lda(0x01);
		p.cmp(0x81);
		p.bcc(bccTrue);
		p.ldy(0x01);
		p.rts();
		p.label(bccTrue);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void cmpAndBccForUnsignedComparisonWhenAccLarger() {
		Label bccTrue = Label.named("bccTrue");
		p.startAddress(address$1000);
		p.lda(0x81);
		p.cmp(0x01);
		p.bcc(bccTrue);
		p.ldy(0x01);
		p.rts();
		p.label(bccTrue);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void debugReadLabelContent() {
		final Label label = Label.named("label");

		final AtomicInteger debuggedData = new AtomicInteger();

		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(label);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				UnsignedByte v = ctx.readMem(label);
				debuggedData.set(v.uInt());
			}
		});
		p.rts();
		p.label(label).data(0);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(1, debuggedData.get());
	}

	@Test
	public void debugWriteLabelContent() {
		final Label label = Label.named("label");

		p.startAddress(address$1000);
		p.lda(0x01);
		p.sta(label);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				ctx.writeMem(label, UnsignedByte.x02);
			}
		});
		p.rts();
		p.label(label).data(0);

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals("02", sim.hexDump(label, 1));
	}

	@Test
	public void debugReadAcc() {
		final AtomicInteger debuggedData1 = new AtomicInteger();
		final AtomicInteger debuggedData2 = new AtomicInteger();

		p.startAddress(address$1000);
		p.lda(0x01);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				UnsignedByte v = ctx.a().value();
				debuggedData1.set(v.uInt());
			}
		});
		p.lda(0x02);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				UnsignedByte v = ctx.a().value();
				debuggedData2.set(v.uInt());
			}
		});
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(1, debuggedData1.get());
		assertEquals(2, debuggedData2.get());
	}

	@Test
	public void debugReadY() {
		final AtomicInteger y = new AtomicInteger();

		p.startAddress(address$1000);
		p.ldy(0x01);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				UnsignedByte v = ctx.y().value();
				y.set(v.uInt());
			}
		});
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(1, y.get());
	}

	@Test
	public void debugReadSr() {
		final AtomicBoolean debuggedZeroFlag1 = new AtomicBoolean();
		final AtomicBoolean debuggedZeroFlag2 = new AtomicBoolean();

		p.startAddress(address$1000);
		p.lda(0x01);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				debuggedZeroFlag1.set(ctx.sr().zero());
			}
		});
		p.lda(0x00);
		p.debug(new Debug() {
			@Override
			public void execute(DebugContext ctx) {
				debuggedZeroFlag2.set(ctx.sr().zero());
			}
		});
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertFalse(debuggedZeroFlag1.get());
		assertTrue(debuggedZeroFlag2.get());
	}

	@Test
	public void bvcWhenOverflowSet() {
		Label overflow = Label.named("overflow");
		p.startAddress(address$1000);
		p.lda(0x7F).clc().adc(0x01);
		p.bvc(overflow);
		p.ldy(0x01);
		p.rts();
		p.label(overflow);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void bvcWhenOverflowNotSet() {
		Label overflow = Label.named("overflow");
		p.startAddress(address$1000);
		p.bvc(overflow);
		p.ldy(0x01);
		p.rts();
		p.label(overflow);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bvsWhenOverflowSet() {
		Label overflow = Label.named("overflow");
		p.startAddress(address$1000);
		p.lda(0x7F).clc().adc(0x01);
		p.bvs(overflow);
		p.ldy(0x01);
		p.rts();
		p.label(overflow);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void bvsWhenOverflowNotSet() {
		Label overflow = Label.named("overflow");
		p.startAddress(address$1000);
		p.bvs(overflow);
		p.ldy(0x01);
		p.rts();
		p.label(overflow);
		p.ldy(0x02);
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x01, sim.y().uInt());
	}

	@Test
	public void simpleSysAndAutoTickToLabel() {
		Label ldy1 = Label.named("ldy1");
		Label ldy2 = Label.named("ldy2");

		p.startAddress(address$1000);
		p.rts();
		p.label(ldy1);
		p.ldy(1).rts();
		p.label(ldy2);
		p.ldy(2).rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		assertEquals(0x00, sim.y().uInt());

		sim.simpleSysAndAutoTick(ldy1);
		assertEquals(0x01, sim.y().uInt());

		sim.simpleSysAndAutoTick(ldy2);
		assertEquals(0x02, sim.y().uInt());
	}

	@Test
	public void hexDumpLabeled() {
		Labeled labeled = new Labeled() {
			@Override
			public Label label() {
				return Label.named("labeled");
			}
		};
		p.startAddress(address$1000);
		p.lda(1).sta(labeled);
		p.rts();
		p.label(labeled).data(0);

		sim.load(p.end());

		assertEquals("00", sim.hexDump(labeled, 1));
		sim.simpleSysAndAutoTick();
		assertEquals("01", sim.hexDump(labeled, 1));
	}

	@Test
	public void bitAbsWhenMemoryIsNegative() {
		p.startAddress(address$1000);
		Label data = Label.named("data");
		p.lda(0xFF);
		p.bit(data);
		p.rts();
		p.label(data).data(0x80);
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("80", sim.hexDump(data, 1));
		assertEquals("Nv-bdizc", sim.sr().toString());
	}

	@Test
	public void bitAbsWhenMemoryIsPositive() {
		p.startAddress(address$1000);
		Label data = Label.named("data");
		p.lda(0xFF);
		p.bit(data);
		p.rts();
		p.label(data).data(0x3F);
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("3F", sim.hexDump(data, 1));
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void bitAbsWhenMemoryHasBit6() {
		p.startAddress(address$1000);
		Label data = Label.named("data");
		p.lda(0xFF);
		p.bit(data);
		p.rts();
		p.label(data).data(0x40);
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xFF, sim.a().uInt());
		assertEquals("40", sim.hexDump(data, 1));
		assertEquals("nV-bdizc", sim.sr().toString());
	}

	@Test
	public void bitAbsWhenAccAndMemoryIsZero() {
		p.startAddress(address$1000);
		Label data = Label.named("data");
		p.lda(0xF0);
		p.bit(data);
		p.rts();
		p.label(data).data(0x0F);
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0xF0, sim.a().uInt());
		assertEquals("0F", sim.hexDump(data, 1));
		assertEquals("nv-bdiZc", sim.sr().toString());
	}

	@Test
	public void cldDoesNothing() {
		p.startAddress(address$1000);
		p.cld();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		// that's it, no exception, no status change:
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void cliDoesNothing() {
		p.startAddress(address$1000);
		p.cli();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		// that's it, no exception, no status change:
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	@Test
	public void seiDoesNothing() {
		p.startAddress(address$1000);
		p.sei();
		p.rts();

		sim.loadAndSimpleSysAndAutoTick(p.end());
		// that's it, no exception, no status change:
		assertEquals("nv-bdizc", sim.sr().toString());
	}

	/**
	 * Getting some coverage for the empty listener
	 */
	@Test
	public void simulatorWorksWithEmptyListener() {
		sim = new C64Simulator(new EmptyC64SimulatorEventListener());

		p.startAddress(address$1000);
		Label data = Label.named("data");
		p.lda(0xFF); // write A
		p.tax(); // write X
		p.tay(); // write Y
		p.sta(data); // write mem
		p.adc(1); // write V
		p.sec(); // write C
		p.rts();
		p.label(data).data(0x00);
		sim.loadAndSimpleSysAndAutoTick(p.end());

		assertEquals(0x00, sim.a().uInt());
	}

	/**
	 * In reality there is the familiar "...BASIC V2...38911 BYTES FREE..." but
	 * this is simpler for simulation purposes
	 */
	@Test
	public void initiallyScreenMemoryContainsSpaces() {
		assertEquals(
				"20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 "
						+ "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 "
						+ "20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20",
				sim.hexDump(RawAddress.named(0x0400), 60));
	}

}
