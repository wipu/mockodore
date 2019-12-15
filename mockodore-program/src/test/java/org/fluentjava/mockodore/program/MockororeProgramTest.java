package org.fluentjava.mockodore.program;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$01;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;
import org.junit.Before;
import org.junit.Test;

public class MockororeProgramTest {

	private static final RawAddress address$1000 = RawAddress.named(0x1000);
	private static final RawAddress address$2000 = RawAddress.named(0x2000);
	private ProxyForTestingProxying p;

	private class ProxyForTestingProxying extends
			C64AssyLangProxy<ProxyForTestingProxying, MockodoreProgram> {
		public ProxyForTestingProxying(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}
	}

	@Before
	public void before() {
		p = new ProxyForTestingProxying(MockodoreProgram.with());
	}

	private void assertBytes(String bytesAsHex) {
		assertEquals(bytesAsHex, ByteArrayPrettyPrinter
				.spaceSeparatedHex(p.end().asBytes().justPrgBytes()));
	}

	private void assertAssy(String assy) {
		assertEquals(assy, p.end().asAssy());
	}

	private void assertJava(String java) {
		assertEquals(java, p.end().asJava());
	}

	private void assertPrgBytes(String prgBytesAsHex) {
		assertEquals(prgBytesAsHex, ByteArrayPrettyPrinter
				.spaceSeparatedHex(p.end().asPrgBytes().allBytes()));
	}

	private void assertError(String errorMessage) {
		try {
			p.end().asBytes();
			fail("Should have thrown '" + errorMessage + "'");
		} catch (MockodoreException e) {
			assertEquals(errorMessage, e.getMessage());
		}
	}

	@Test
	public void emptyProgram() {
		// no instructions
		assertBytes("");
		assertAssy("");
		assertJava("");
	}

	@Test
	public void nop() {
		p.nop();
		assertBytes("EA");
		assertAssy("nop\n");
		assertJava("nop();\n");
	}

	@Test
	public void nopRawPlusX() {
		p.nop(RawAddress.named(0x4000).plusX());
		assertBytes("FC 00 40");
		assertAssy("nop $4000,X\n");
		assertJava("nop(RawAddress.named(0x4000).plusX());\n");
	}

	@Test
	public void rts() {
		p.rts();
		assertBytes("60");
		assertAssy("rts\n");
		assertJava("rts();\n");
	}

	@Test
	public void brk() {
		p.brk();
		assertBytes("00");
		assertAssy("brk\n");
		assertJava("brk();\n");
	}

	@Test
	public void ldaZeropageFB() {
		p.lda(ZeroPage.$FB);
		assertBytes("A5 FB");
		assertAssy("lda $FB\n");
		assertJava("lda(ZeroPage.$FB);\n");
	}

	@Test
	public void lsrZeropage() {
		p.lsr(ZeroPage.$FB);
		assertBytes("46 FB");
		assertAssy("lsr $FB\n");
		assertJava("lsr(ZeroPage.$FB);\n");
	}

	@Test
	public void rorZeropage() {
		p.ror(ZeroPage.$FB);
		assertBytes("66 FB");
		assertAssy("ror $FB\n");
		assertJava("ror(ZeroPage.$FB);\n");
	}

	@Test
	public void ror() {
		p.ror();
		assertBytes("6A");
		assertAssy("ror\n");
	}

	@Test
	public void rol() {
		p.rol();
		assertBytes("2A");
		assertAssy("rol\n");
	}

	@Test
	public void rorAbsX() {
		p.ror(RawAddress.named(0x1234).plusX());
		assertBytes("7E 34 12");
		assertAssy("ror $1234,X\n");
		assertJava("ror(RawAddress.named(0x1234).plusX());\n");
	}

	@Test
	public void rorAbs() {
		p.ror(RawAddress.named(0x1234));
		assertBytes("6E 34 12");
		assertAssy("ror $1234\n");
	}

	@Test
	public void ldxZeropageFB() {
		p.ldx(ZeroPage.$FB);
		assertBytes("A6 FB");
		assertAssy("ldx $FB\n");
		assertJava("ldx(ZeroPage.$FB);\n");
	}

	@Test
	public void ldyZeropageFB() {
		p.ldy(ZeroPage.$FB);
		assertBytes("A4 FB");
		assertAssy("ldy $FB\n");
		assertJava("ldy(ZeroPage.$FB);\n");
	}

	@Test
	public void ldaFailsWithUnknownLabel() {
		Label unknown = Label.named("unknown");
		p.startAddress(address$1000);
		p.lda(unknown);
		assertError("Undefined label: unknown");
	}

	@Test
	public void ldaFailsWithLabelWithoutProgramStartAddress() {
		Label label = Label.named("label");
		p.label(label);
		p.lda(label);
		assertError("Cannot reference label 'label'"
				+ " without program start address");
	}

	@Test
	public void ldaLabel4096() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.lda(label);
		assertBytes("AD 00 10");
		assertAssy("label:\nlda label\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "lda(label);\n");
	}

	@Test
	public void ldxLabel4096() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.ldx(label);
		assertBytes("AE 00 10");
		assertAssy("label:\n" + "ldx label\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "ldx(label);\n");
	}

	@Test
	public void ldyLabel4096() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.ldy(label);
		assertBytes("AC 00 10");
		assertAssy("label:\nldy label\n");
	}

	@Test
	public void ldyLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.ldy(label.plusX());
		assertBytes("BC 00 10");
		assertAssy("label:\nldy label,X\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "ldy(label.plusX());\n");
	}

	@Test
	public void cmpLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.cmp(label);
		assertBytes("CD 00 10");
		assertAssy("label:\ncmp label\n");
	}

	@Test
	public void cmpZpX() {
		p.startAddress(address$1000);
		p.cmp(ZeroPage.$FB.plusX());
		assertBytes("D5 FB");
		assertAssy("cmp $FB,X\n");
	}

	@Test
	public void cmpLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.cmp(label.plusX());
		assertBytes("DD 00 10");
		assertAssy("label:\ncmp label,X\n");
	}

	@Test
	public void incLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.inc(label.plusX());
		assertBytes("FE 00 10");
		assertAssy("label:\ninc label,X\n");
	}

	@Test
	public void adcLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.adc(label.plusX());
		assertBytes("7D 00 10");
		assertAssy("label:\nadc label,X\n");
	}

	@Test
	public void sbcLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.sbc(label.plusX());
		assertBytes("FD 00 10");
		assertAssy("label:\nsbc label,X\n");
	}

	@Test
	public void oraLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.ora(label.plusX());
		assertBytes("1D 00 10");
		assertAssy("label:\nora label,X\n");
	}

	@Test
	public void oraLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.ora(label);
		assertBytes("0D 00 10");
		assertAssy("label:\nora label\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "ora(label);\n" + "");
	}

	@Test
	public void oraZp() {
		p.startAddress(address$1000);
		p.ora(ZeroPage.$FB);
		assertBytes("05 FB");
		assertAssy("ora $FB\n");
	}

	@Test
	public void cmpLabelPlusY() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.cmp(label.plusY());
		assertBytes("D9 00 10");
		assertAssy("label:\ncmp label,Y\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "cmp(label.plusY());\n");
	}

	@Test
	public void cmpImmediate() {
		p.startAddress(address$1000);
		p.cmp(0x01);
		assertBytes("C9 01");
		assertAssy("cmp #$01\n");
		assertJava("cmp(0x01);\n");
	}

	@Test
	public void cpxImmediate() {
		p.startAddress(address$1000);
		p.cpx($01);
		assertBytes("E0 01");
		assertAssy("cpx #$01\n");
	}

	@Test
	public void cpxImmediateInt() {
		p.startAddress(address$1000);
		p.cpx(0x01);
		assertBytes("E0 01");
		assertAssy("cpx #$01\n");
	}

	@Test
	public void cpyImmediate() {
		p.startAddress(address$1000);
		p.cpy(0x01);
		assertBytes("C0 01");
		assertAssy("cpy #$01\n");
	}

	@Test
	public void ldaLabel8192PlusNop() {
		Label label = Label.named("label");
		p.startAddress(address$2000);
		p.nop();
		p.label(label);
		p.nop();
		p.lda(label);
		assertBytes("EA EA AD 01 20");
	}

	@Test
	public void doubleLabelIsError() {
		Label label = Label.named("label");
		p.startAddress(address$2000);
		p.data(0);
		p.label(label);
		p.data(0);
		try {
			p.label(label);
			fail();
		} catch (MockodoreException e) {
			assertEquals(
					"Label 'label' was already defined" + " at relative byte 1",
					e.getMessage());
		}
	}

	@Test
	public void bneToPreviousNop() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.label(dest);
		p.nop();
		p.bne(dest);
		assertBytes("EA D0 FD");
		assertAssy("dest:\nnop\nbne dest\n");
		assertJava("Label dest = Label.named(\"dest\");\n" + "label(dest);\n"
				+ "nop();\n" + "bne(dest);\n");
	}

	@Test
	public void bneToRawAddressWithinRange() {
		p.startAddress(address$1000);
		p.bne(RawAddress.named(0x100A));
		assertBytes("D0 08");
		assertAssy("bne $100A\n");
	}

	@Test
	public void bneToAfterNextNop() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.bne(dest);
		p.nop();
		p.label(dest);
		p.nop();
		assertBytes("D0 01 EA EA");
	}

	@Test
	public void beq() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.beq(dest);
		p.nop();
		p.label(dest);
		p.nop();
		assertBytes("F0 01 EA EA");
		assertAssy("beq dest\nnop\ndest:\nnop\n");
	}

	private static final int MAX_FORWARD_RELATIVE = 127;
	private static final int MAX_BACKWARD_RELATIVE = 126;

	@Test
	public void beqMaxWorward() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.beq(dest);
		for (int i = 0; i < MAX_FORWARD_RELATIVE; i++) {
			p.nop();
		}
		p.label(dest);

		String actualBytes = ByteArrayPrettyPrinter
				.spaceSeparatedHex(p.end().asBytes().justPrgBytes());
		assertEquals("F0 7F EA", actualBytes.substring(0, 8));
	}

	@Test
	public void beqMaxBackward() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.label(dest);
		for (int i = 0; i < MAX_BACKWARD_RELATIVE; i++) {
			p.nop();
		}
		p.beq(dest);

		String actualBytes = ByteArrayPrettyPrinter
				.spaceSeparatedHex(p.end().asBytes().justPrgBytes());
		assertEquals("EA F0 80",
				actualBytes.substring(actualBytes.length() - 8));
	}

	@Test
	public void beqFailsIfDestTooFarAhead() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.beq(dest);
		for (int i = 0; i < MAX_FORWARD_RELATIVE + 1; i++) {
			p.nop();
		}
		p.label(dest);
		p.nop();

		try {
			p.end().asPrgBytes();
			fail();
		} catch (MockodoreException e) {
			assertEquals(
					"Address 'dest' is too far from $1000"
							+ " for a relative reference: +128",
					e.getMessage());
		}
	}

	@Test
	public void beqFailsIfDestTooFarBack() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.label(dest);
		for (int i = 0; i < MAX_BACKWARD_RELATIVE + 1; i++) {
			p.nop();
		}
		p.beq(dest);

		try {
			p.end().asPrgBytes();
			fail();
		} catch (MockodoreException e) {
			assertEquals(
					"Address 'dest' is too far from $107F"
							+ " for a relative reference: -129",
					e.getMessage());
		}
	}

	@Test
	public void bcc() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.bcc(dest);
		p.rts();
		p.label(dest);
		p.rts();
		assertBytes("90 01 60 60");
		assertAssy("bcc dest\nrts\ndest:\nrts\n");
	}

	@Test
	public void bcs() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.bcs(dest);
		p.rts();
		p.label(dest);
		p.rts();
		assertBytes("B0 01 60 60");
		assertAssy("bcs dest\nrts\ndest:\nrts\n");
	}

	@Test
	public void bvc() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.bvc(dest);
		p.rts();
		p.label(dest);
		p.rts();
		assertBytes("50 01 60 60");
		assertAssy("bvc dest\nrts\ndest:\nrts\n");
	}

	@Test
	public void bvs() {
		Label dest = Label.named("dest");
		p.startAddress(address$1000);
		p.bvs(dest);
		p.rts();
		p.label(dest);
		p.rts();
		assertBytes("70 01 60 60");
		assertAssy("bvs dest\nrts\ndest:\nrts\n");
	}

	@Test
	public void bmiToRawAddressWithinRange() {
		p.startAddress(address$1000);
		p.bmi(RawAddress.named(0x100A));
		assertBytes("30 08");
		assertAssy("bmi $100A\n");
		assertJava("bmi(RawAddress.named(0x100A));\n");
	}

	@Test
	public void bplToRawAddressWithinRangeButNegative() {
		p.startAddress(address$1000);
		p.bpl(RawAddress.named(0x0FF0));
		assertBytes("10 EE");
		assertAssy("bpl $FF0\n");
	}

	// TODO bne etc overflow

	@Test
	public void staLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.sta(label);
		assertBytes("8D 00 10");
	}

	@Test
	public void stxLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.stx(label);

		assertBytes("8E 00 10");
		assertAssy("label:\nstx label\n");
	}

	@Test
	public void stxZp() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.stx(ZeroPage.$01);

		assertBytes("86 01");
		assertAssy("label:\nstx $01\n");
	}

	@Test
	public void styLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.sty(label);

		assertBytes("8C 00 10");
		assertAssy("label:\nsty label\n");
	}

	@Test
	public void styZp() {
		p.startAddress(address$1000);
		p.sty(ZeroPage.$FB);

		assertBytes("84 FB");
		assertAssy("sty $FB\n");
	}

	@Test
	public void bitAbs() {
		p.startAddress(address$1000);
		p.bit(RawAddress.named(0x1234));

		assertBytes("2C 34 12");
		assertAssy("bit $1234\n");
	}

	@Test
	public void staLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.sta(label.plusX());

		assertBytes("9D 00 10");
		assertAssy("label:\nsta label,X\n");
	}

	@Test
	public void staLabelPlusY() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.sta(label.plusY());

		assertBytes("99 00 10");
		assertAssy("label:\nsta label,Y\n");
	}

	@Test
	public void staZeroPage() {
		p.sta(ZeroPage.$FB);
		assertBytes("85 FB");
		assertAssy("sta $FB\n");
	}

	@Test
	public void rolZeroPage() {
		p.rol(ZeroPage.$FB);
		assertBytes("26 FB");
		assertAssy("rol $FB\n");
	}

	@Test
	public void staZeroPagePlusX() {
		p.sta(ZeroPage.$FB.plusX());
		assertBytes("95 FB");
		assertAssy("sta $FB,X\n");
		assertJava("sta(ZeroPage.$FB.plusX());\n");
	}

	@Test
	public void staZeroPagePlusXIndirect() {
		p.sta(ZeroPage.$FB.plusXIndirect());
		assertBytes("81 FB");
		assertAssy("sta ($FB,X)\n");
		assertJava("sta(ZeroPage.$FB.plusXIndirect());\n");
	}

	@Test
	public void staZeroPageIndirectPlusY() {
		p.sta(ZeroPage.$FB.indirectPlusY());
		assertBytes("91 FB");
		assertAssy("sta ($FB),Y\n");
		assertJava("sta(ZeroPage.$FB.indirectPlusY());\n");
	}

	@Test
	public void sloZeroPageIndirectPlusY() {
		p.slo(ZeroPage.$FB.indirectPlusY());
		assertBytes("13 FB");
		assertAssy("slo ($FB),Y\n");
	}

	// TODO after all it's better to bind labels to the instruction (or data
	// block) that comes after it. That way even the relative location can
	// change later, if code gets moved around to make some calls local etc

	// 2-instruction program

	@Test
	public void nopRts() {
		p.nop().rts();
		assertBytes("EA 60");
	}

	// data

	@Test
	public void byteData() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(data);
		p.rts();
		p.label(data);
		byte[] bytes = { 0x01, 0x02 };
		p.data(bytes);
		p.nop();
		assertBytes("AD 04 10 60 01 02 EA");
		assertAssy("lda data\nrts\ndata:\n.b $01, $02\nnop\n");
		assertJava("Label data = Label.named(\"data\");\n" + "lda(data);\n"
				+ "rts();\n" + "label(data);\n" + "data(0x01, 0x02);\n"
				+ "nop();\n");
	}

	@Test
	public void byteDataAsInt() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(data);
		p.rts();
		p.label(data);
		p.data(0x01, 0x02);
		p.nop();
		assertBytes("AD 04 10 60 01 02 EA");
		assertAssy("lda data\nrts\ndata:\n.b $01, $02\nnop\n");
		assertJava("Label data = Label.named(\"data\");\n" + "lda(data);\n"
				+ "rts();\n" + "label(data);\n" + "data(0x01, 0x02);\n"
				+ "nop();\n");
	}

	@Test
	public void byteDataAsUnsignedByte() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(data);
		p.rts();
		p.label(data);
		p.data(UnsignedByte.$01, UnsignedByte.$02);
		p.nop();
		assertBytes("AD 04 10 60 01 02 EA");
		assertAssy("lda data\nrts\ndata:\n.b $01, $02\nnop\n");
		assertJava("Label data = Label.named(\"data\");\n" + "lda(data);\n"
				+ "rts();\n" + "label(data);\n" + "data(0x01, 0x02);\n"
				+ "nop();\n");
	}

	@Test
	public void compileTimeIndexingOfAddress() {
		Label data = Label.named("data");
		p.startAddress(address$1000);
		p.lda(data);
		p.sta(data.plus(1));
		p.sta(data.plus(2));
		p.rts();
		p.label(data);
		p.data(0x01, 0x02, 0x03, 0x04);
		assertBytes("AD 0A 10 8D 0B 10 8D 0C 10 60 01 02 03 04");
		assertJava("Label data = Label.named(\"data\");\n" + "lda(data);\n"
				+ "sta(data.plus(1));\n" + "sta(data.plus(2));\n" + "rts();\n"
				+ "label(data);\n" + "data(0x01, 0x02, 0x03, 0x04);\n");
	}

	// "lsb, msb" meaning address arithmetic

	@Test
	public void staLdaLsbAndMsbBetweenTwoLabels() {
		Label from = Label.named("from");
		Label to = Label.named("to");
		p.startAddress(address$1000);
		p.lda(from);
		p.sta(to);
		p.lda(from.plus(1));
		p.sta(to.plus(1));
		p.label(from);
		p.data(1, 1);
		p.label(to);
		p.data(2, 2);
		assertBytes("AD 0C 10 8D 0E 10 AD 0D 10 8D 0F 10 01 01 02 02");
		assertAssy("lda from\nsta to\nlda from+(1)\nsta to+(1)\n"
				+ "from:\n.b $01, $01\nto:\n.b $02, $02\n");
	}

	// immediate

	@Test
	public void ldaImmediate() {
		p.lda(0x01);
		p.lda(0x02);
		assertBytes("A9 01 A9 02");
		assertAssy("lda #$01\nlda #$02\n");
	}

	@Test
	public void ldaImmediateOpValue() {
		p.lda(Op.LDY_ABS);
		assertBytes("A9 AC");
		assertAssy("lda #$AC ; LDY abs\n");
	}

	@Test
	public void ldxImmediateInt() {
		p.ldx(0x01);
		assertBytes("A2 01");
		assertAssy("ldx #$01\n");
	}

	@Test
	public void ldxImmediateUnsignedByte() {
		p.ldx(UnsignedByte.$02);
		assertBytes("A2 02");
		assertAssy("ldx #$02\n");
	}

	@Test
	public void ldyImmediate() {
		p.ldy(0x01);
		assertBytes("A0 01");
		assertAssy("ldy #$01\n");
	}

	@Test
	public void eorImmediate() {
		p.eor(0x02);
		assertBytes("49 02");
		assertAssy("eor #$02\n");
	}

	@Test
	public void oraImmediate() {
		p.ora(0x01);
		assertBytes("09 01");
		assertAssy("ora #$01\n");
	}

	// raw address

	@Test
	public void ldaStaBetweenRawAddresses() {
		p.lda(RawAddress.named(0x1234));
		p.sta(RawAddress.named(0x1235));
		p.lda(RawAddress.named(0x2345));
		p.sta(RawAddress.named(0x2346));
		assertBytes("AD 34 12 8D 35 12 AD 45 23 8D 46 23");
		assertAssy("lda $1234\nsta $1235\nlda $2345\nsta $2346\n");
	}

	// indirect

	@Test
	public void indirectJmpToLabel() {
		Label to = Label.named("to");
		p.startAddress(address$1000);
		p.label(to);
		p.nop();
		p.jmp(to.indirect());
		assertBytes("EA 6C 00 10");
		assertAssy("to:\nnop\njmp (to)\n");
		assertJava("Label to = Label.named(\"to\");\n" + "label(to);\n"
				+ "nop();\n" + "jmp(to.indirect());\n");
	}

	@Test
	public void indirectJmpToRawAddress() {
		p.jmp(RawAddress.named(0x1234).indirect());
		assertBytes("6C 34 12");
		assertAssy("jmp ($1234)\n");
		assertJava("jmp(RawAddress.named(0x1234).indirect());\n");
	}

	// prg

	@Test
	public void prgFailsWithoutStartAddress() {
		p.nop();
		p.rts();
		try {
			p.end().asPrgBytes();
			fail();
		} catch (MockodoreException e) {
			assertEquals("Cannot create a prg without start address",
					e.getMessage());
		}
	}

	@Test
	public void prgAt4096() {
		p.startAddress(address$1000);
		p.nop();
		p.rts();
		assertPrgBytes("00 10 EA 60");
	}

	@Test
	public void differentPrgAt8192() {
		p.startAddress(address$2000);
		p.sei();
		p.rts();
		assertPrgBytes("00 20 78 60");
	}

	// Label resolving

	@Test
	public void labelResolvingFromProgram() {
		Label start = Label.named("start");
		Label end = Label.named("end");
		p.startAddress(address$2000);
		p.label(start);
		p.nop();
		p.label(end);
		p.rts();

		MockodoreProgram program = p.end();
		assertEquals(0x2000, program.addressOf(start).value());
		assertEquals(0x2001, program.addressOf(end).value());
		try {
			program.addressOf(Label.named("unknown"));
			fail();
		} catch (MockodoreException e) {
			assertEquals("Undefined label: unknown", e.getMessage());
		}
	}

	// Absolute + X

	@Test
	public void ldaRawAddressPlusX() {
		p.lda(RawAddress.named(0xD020).plusX());
		assertBytes("BD 20 D0");
		assertAssy("lda $D020,X\n");
		assertJava("lda(RawAddress.named(0xD020).plusX());\n");
	}

	@Test
	public void ldaLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.lda(label.plusX());
		p.label(label).data(0x00);
		assertBytes("BD 03 10 00");
		assertAssy("lda label,X\nlabel:\n.b $00\n");
	}

	@Test
	public void ldaLabelPlusY() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.lda(label.plusY());
		p.label(label).data(0x00);
		assertBytes("B9 03 10 00");
		assertAssy("lda label,Y\nlabel:\n.b $00\n");
	}

	@Test
	public void ldxLabelPlusY() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldx(label.plusY());
		p.label(label).data(0x00);
		assertBytes("BE 03 10 00");
		assertAssy("ldx label,Y\nlabel:\n.b $00\n");
	}

	@Test
	public void ldaZeroPagePlusX() {
		ZeroPage zp = ZeroPage.$FB;
		p.startAddress(address$1000);
		p.lda(zp.plusX());
		assertBytes("B5 FB");
		assertAssy("lda $FB,X\n");
	}

	@Test
	public void ldyZeroPagePlusX() {
		ZeroPage zp = ZeroPage.$FB;
		p.startAddress(address$1000);
		p.ldy(zp.plusX());
		assertBytes("B4 FB");
		assertAssy("ldy $FB,X\n");
	}

	@Test
	public void ldaZeroPageIndirectPlusY() {
		ZeroPage zp = ZeroPage.$FB;
		p.startAddress(address$1000);
		p.lda(zp.indirectPlusY());
		assertBytes("B1 FB");
		assertAssy("lda ($FB),Y\n");
	}

	@Test
	public void ldaRawAddressPlusY() {
		RawAddress addr = RawAddress.named(0x1234);
		p.startAddress(address$1000);
		p.lda(addr.plusY());
		assertBytes("B9 34 12");
		assertAssy("lda $1234,Y\n");
		assertJava("lda(RawAddress.named(0x1234).plusY());\n");
	}

	// further instructions

	@Test
	public void jsrLabel() {
		Label sub1 = Label.named("sub1");
		Label sub2 = Label.named("sub2");
		p.startAddress(address$1000);
		p.jsr(sub1);
		p.jsr(sub2);
		p.label(sub1);
		p.nop();
		p.label(sub2);
		p.nop();
		assertBytes("20 06 10 20 07 10 EA EA");
	}

	@Test
	public void sec() {
		p.sec();
		assertBytes("38");
		assertAssy("sec\n");
	}

	@Test
	public void clc() {
		p.clc();
		assertBytes("18");
		assertAssy("clc\n");
	}

	@Test
	public void cld() {
		p.cld();
		assertBytes("D8");
		assertAssy("cld\n");
	}

	@Test
	public void tay() {
		p.tay();
		assertBytes("A8");
		assertAssy("tay\n");
	}

	@Test
	public void tya() {
		p.tya();
		assertBytes("98");
		assertAssy("tya\n");
	}

	@Test
	public void tax() {
		p.tax();
		assertBytes("AA");
		assertAssy("tax\n");
	}

	@Test
	public void txa() {
		p.txa();
		assertBytes("8A");
		assertAssy("txa\n");
	}

	@Test
	public void inx() {
		p.inx();
		assertBytes("E8");
		assertAssy("inx\n");
	}

	@Test
	public void iny() {
		p.iny();
		assertBytes("C8");
		assertAssy("iny\n");
	}

	@Test
	public void lsr() {
		p.lsr();
		assertBytes("4A");
		assertAssy("lsr\n");
	}

	@Test
	public void lsrAbsX() {
		p.lsr(RawAddress.named(0x1234).plusX());
		assertBytes("5E 34 12");
		assertAssy("lsr $1234,X\n");
	}

	@Test
	public void adcLabel() {
		Label num = Label.named("num");
		p.startAddress(address$1000);
		p.adc(num);
		p.label(num);
		p.data(0x01);

		assertBytes("6D 03 10 01");
		assertAssy("adc num\nnum:\n.b $01\n");
	}

	@Test
	public void adcZp() {
		p.adc(ZeroPage.$FB);

		assertBytes("65 FB");
		assertAssy("adc $FB\n");
	}

	@Test
	public void adcZpX() {
		p.adc(ZeroPage.$FB.plusX());

		assertBytes("75 FB");
		assertAssy("adc $FB,X\n");
	}

	@Test
	public void sbcZp() {
		p.sbc(ZeroPage.$FB);

		assertBytes("E5 FB");
		assertAssy("sbc $FB\n");
	}

	@Test
	public void sbcZpX() {
		p.sbc(ZeroPage.$FB.plusX());

		assertBytes("F5 FB");
		assertAssy("sbc $FB,X\n");
	}

	@Test
	public void sbcLabel() {
		Label num = Label.named("num");
		p.startAddress(address$1000);
		p.sbc(num);
		p.label(num);
		p.data(0x01);

		assertBytes("ED 03 10 01");
		assertAssy("sbc num\nnum:\n.b $01\n");
	}

	@Test
	public void adcLabelPlusY() {
		Label num = Label.named("num");
		p.startAddress(address$1000);
		p.adc(num.plusY());
		p.label(num);
		p.data(0x01);

		assertBytes("79 03 10 01");
		assertAssy("adc num,Y\nnum:\n.b $01\n");
	}

	@Test
	public void sbcLabelPlusY() {
		Label num = Label.named("num");
		p.startAddress(address$1000);
		p.sbc(num.plusY());
		p.label(num);
		p.data(0x01);

		assertBytes("F9 03 10 01");
		assertAssy("sbc num,Y\nnum:\n.b $01\n");
	}

	@Test
	public void adcImmediate() {
		p.adc(0x02);

		assertBytes("69 02");
		assertAssy("adc #$02\n");
	}

	@Test
	public void sbcImmediate() {
		p.sbc(0x02);

		assertBytes("E9 02");
		assertAssy("sbc #$02\n");
	}

	@Test
	public void sei() {
		p.sei();
		assertBytes("78");
		assertAssy("sei\n");
	}

	@Test
	public void cli() {
		p.cli();
		assertBytes("58");
		assertAssy("cli\n");
	}

	@Test
	public void incRawAddress() {
		p.inc(RawAddress.named(0xD021));
		assertBytes("EE 21 D0");
		assertAssy("inc $D021\n");
	}

	@Test
	public void incLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.inc(label);
		p.rts();
		p.label(label).data(0x00);
		assertBytes("EE 04 10 60 00");
		assertAssy("inc label\nrts\nlabel:\n.b $00\n");
	}

	@Test
	public void incZp() {
		p.startAddress(address$1000);
		p.inc(ZeroPage.$01);
		assertBytes("E6 01");
		assertAssy("inc $01\n");
	}

	@Test
	public void decZp() {
		p.startAddress(address$1000);
		p.dec(ZeroPage.$01);
		assertBytes("C6 01");
		assertAssy("dec $01\n");
	}

	@Test
	public void decZpX() {
		p.startAddress(address$1000);
		p.dec(ZeroPage.$01.plusX());
		assertBytes("D6 01");
		assertAssy("dec $01,X\n");
	}

	@Test
	public void decRawAddress() {
		p.dec(RawAddress.named(0xD021));
		assertBytes("CE 21 D0");
		assertAssy("dec $D021\n");
	}

	@Test
	public void decLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.dec(label);
		p.rts();
		p.label(label).data(0x00);
		assertBytes("CE 04 10 60 00");
		assertAssy("dec label\nrts\nlabel:\n.b $00\n");
	}

	@Test
	public void dex() {
		p.dex();
		assertBytes("CA");
		assertAssy("dex\n");
	}

	@Test
	public void dey() {
		p.dey();
		assertBytes("88");
		assertAssy("dey\n");
	}

	@Test
	public void pha() {
		p.pha();
		assertBytes("48");
		assertAssy("pha\n");
	}

	@Test
	public void pla() {
		p.pla();
		assertBytes("68");
		assertAssy("pla\n");
	}

	// lsb, msb meaning bytes of 16bit addresses

	/**
	 * TODO type for RawAddressLsb for better java here:
	 */
	@Test
	public void ldaLsbOfRawAddress() {
		RawAddress addr = RawAddress.named(0x1234);
		p.lda(addr.lsb());

		assertBytes("A9 34");
		assertAssy("lda #$34\n");
		assertJava("lda(0x34);\n");
	}

	@Test
	public void ldaMsbOfRawAddress() {
		RawAddress addr = RawAddress.named(0x1234);
		p.lda(addr.msb());

		assertBytes("A9 12");
		assertAssy("lda #$12\n");
		assertJava("lda(0x12);\n");
	}

	@Test
	public void ldaLsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.lda(label.lsb());
		p.label(label);
		p.nop();

		assertBytes("A9 02 EA");
		assertAssy("lda #<label\nlabel:\nnop\n");
		assertJava("Label label = Label.named(\"label\");\n"
				+ "lda(label.lsb());\n" + "label(label);\n" + "nop();\n");
	}

	@Test
	public void ldxLsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldx(label.lsb());
		p.label(label);
		p.nop();

		assertBytes("A2 02 EA");
		assertAssy("ldx #<label\nlabel:\nnop\n");
	}

	@Test
	public void ldyLsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldy(label.lsb());
		p.label(label);
		p.nop();

		assertBytes("A0 02 EA");
		assertAssy("ldy #<label\nlabel:\nnop\n");
	}

	@Test
	public void ldaMsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.lda(label.msb());
		p.label(label);
		p.nop();

		assertBytes("A9 10 EA");
		assertAssy("lda #>label\nlabel:\nnop\n");
		assertJava("Label label = Label.named(\"label\");\n"
				+ "lda(label.msb());\n" + "label(label);\n" + "nop();\n");
	}

	@Test
	public void ldxMsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldx(label.msb());
		p.label(label);
		p.nop();

		assertBytes("A2 10 EA");
		assertAssy("ldx #>label\nlabel:\nnop\n");
	}

	@Test
	public void ldyMsbOfLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.ldy(label.msb());
		p.label(label);
		p.nop();

		assertBytes("A0 10 EA");
		assertAssy("ldy #>label\nlabel:\nnop\n");
	}

	@Test
	public void aslImplied() {
		p.asl();
		assertBytes("0A");
		assertAssy("asl\n");
	}

	@Test
	public void aslLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.asl(label);
		p.rts();
		p.label(label);
		p.data(0x01);

		assertBytes("0E 04 10 60 01");
		assertAssy("asl label\nrts\nlabel:\n.b $01\n");
	}

	@Test
	public void aslZp() {
		p.startAddress(address$1000);
		p.asl(ZeroPage.$FB);

		assertBytes("06 FB");
		assertAssy("asl $FB\n");
	}

	@Test
	public void aslAbsX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.asl(label.plusX());
		p.rts();
		p.label(label);
		p.data(0x01);

		assertBytes("1E 04 10 60 01");
		assertAssy("asl label,X\nrts\nlabel:\n.b $01\n");
	}

	@Test
	public void jmpLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.jmp(label);
		p.rts();
		p.label(label);
		p.nop();

		assertBytes("4C 04 10 60 EA");
		assertAssy("jmp label\nrts\nlabel:\nnop\n");
	}

	@Test
	public void jmpRaw() {
		p.startAddress(address$1000);
		p.jmp(RawAddress.named(0x1234));

		assertBytes("4C 34 12");
		assertAssy("jmp $1234\n");
	}

	@Test
	public void andRawPlusX() {
		p.startAddress(address$1000);
		p.and(RawAddress.named(0x1234).plusX());
		assertBytes("3D 34 12");
		assertAssy("and $1234,X\n");
	}

	@Test
	public void andLabelPlusX() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.and(label.plusX());
		assertBytes("3D 00 10");
		assertAssy("label:\nand label,X\n");
	}

	@Test
	public void andImm() {
		p.startAddress(address$1000);
		p.and(0x12);
		assertBytes("29 12");
		assertAssy("and #$12\n");
	}

	@Test
	public void andLabel() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.and(label);
		assertBytes("2D 00 10");
		assertAssy("label:\nand label\n");
	}

	@Test
	public void decRawPlusX() {
		p.startAddress(address$1000);
		p.dec(RawAddress.named(0x1234).plusX());
		assertBytes("DE 34 12");
		assertAssy("dec $1234,X\n");
	}

	@Test
	public void rawAddressAsData() {
		p.startAddress(address$1000);
		p.data(RawAddress.named(0x1234));
		assertBytes("34 12");
		assertAssy(".b #<$1234\n.b #>$1234\n");
		assertJava("data(RawAddress.named(0x1234));\n");
	}

	@Test
	public void labelAsData() {
		Label label = Label.named("label");
		p.startAddress(address$1000);
		p.label(label);
		p.data(label);
		assertBytes("00 10");
		assertAssy("label:\n.b #<label\n.b #>label\n");
		assertJava("Label label = Label.named(\"label\");\n" + "label(label);\n"
				+ "data(label);\n");
	}

	@Test
	public void multipleLabelsPerAddress() {
		Label label0 = Label.named("label0");
		Label label1 = Label.named("label1");
		p.startAddress(address$1000);
		p.jsr(label0);
		p.jsr(label1);
		p.label(label0);
		p.label(label1);
		p.rts();
		assertBytes("20 06 10 20 06 10 60");
		assertAssy("jsr label0\njsr label1\nlabel0:\nlabel1:\nrts\n");
		assertJava("Label label0 = Label.named(\"label0\");\n"
				+ "Label label1 = Label.named(\"label1\");\n" + "jsr(label0);\n"
				+ "jsr(label1);\n" + "label(label0);\n" + "label(label1);\n"
				+ "rts();\n");
	}

	@Test
	public void assyWithIndentationAndAddressesAsCommentsAfterLabels() {
		Label label1 = Label.named("label1");
		Label label2 = Label.named("label2");
		Label label3 = Label.named("label3");
		p.startAddress(address$1000);
		p.lda(label1);
		p.rts();
		p.label(label2);
		p.rts();
		p.nop();
		p.label(label3);
		p.iny();
		assertEquals(
				"  lda label1\n" + "  rts\n" + "label2:  ;$1004\n" + "  rts\n"
						+ "  nop\n" + "label3:  ;$1006\n" + "  iny\n",
				p.end().asAssy(2, true));
	}

	@Test
	public void labelLsbAndMsbAsDataBytesSeparately() {
		Label label1 = Label.named("label1");
		Label label2 = Label.named("label2");
		p.startAddress(address$1000);
		p.data(label1.lsb());
		p.data(label2.lsb());
		p.data(label1.msb());
		p.data(label2.msb());
		p.label(label1).data(1);
		p.label(label2).data(1);

		assertBytes("04 05 10 10 01 01");
		assertAssy(".b #<label1\n.b #<label2\n" + ".b #>label1\n.b #>label2\n"
				+ "label1:\n.b $01\n" + "label2:\n.b $01\n");
		assertJava("Label label1 = Label.named(\"label1\");\n"
				+ "Label label2 = Label.named(\"label2\");\n"
				+ "data(label1.lsb());\n" + "data(label2.lsb());\n"
				+ "data(label1.msb());\n" + "data(label2.msb());\n"
				+ "label(label1);\n" + "data(0x01);\n" + "label(label2);\n"
				+ "data(0x01);\n");
	}

	@Test
	public void labelMapForDebugging() {
		Label subRoutineLabel1 = Label.named("subRoutineLabel1");
		p.startAddress(address$1000);
		p.label(Label.named("start"));
		p.jsr(subRoutineLabel1);
		p.rts();
		p.label(subRoutineLabel1);
		p.label(Label.named("subRoutineLabel2"));
		p.rts();

		assertEquals(
				"{4096=[start], "
						+ "4100=[subRoutineLabel1, subRoutineLabel2]}",
				p.end().labelMap().toString());
	}

	@Test
	public void emptyLine() {
		p.nop();
		p.emptyLine();
		p.lda(1);
		assertBytes("EA A9 01");
		assertAssy("nop\n\nlda #$01\n");
		assertJava("nop();\n" + "emptyLine();\n" + "lda(0x01);\n" + "");
	}

	@Test
	public void commentLine() {
		p.nop();
		p.commentLine("Load one to acc:");
		p.lda(1);
		assertBytes("EA A9 01");
		assertAssy("nop\n" + "; Load one to acc:\n" + "lda #$01\n" + "");
		assertJava("nop();\n" + "commentLine(\"Load one to acc:\");\n"
				+ "lda(0x01);\n" + "");
	}

	@Test
	public void labelIsPrintedOnceAndOnlyOnceIfEmptyOrCommentLineIsLabeled() {
		Label label1 = Label.named("label1");
		Label label2 = Label.named("label2");
		p.startAddress(address$1000);

		p.jsr(label1);
		p.jsr(label2);
		p.label(label1);
		p.emptyLine();
		p.rts();
		p.label(label2);
		p.commentLine("a comment");
		p.rts();

		// labels also point to correct places:
		assertBytes("20 06 10 20 07 10 60 60");
		assertAssy("jsr label1\n" + "jsr label2\n" + "label1:\n" + "\n"
				+ "rts\n" + "label2:\n" + "; a comment\n" + "rts\n" + "");
	}

	@Test
	public void commentAfterInstr() {
		p.lda(1).commentAfterInstr("Load one to acc");
		assertBytes("A9 01");
		assertAssy("lda #$01 ; Load one to acc\n" + "");
		assertJava("lda(0x01).commentAfterInstr(\"Load one to acc\");\n" + "");
	}

	@Test
	public void twoCommentsAfterInstr() {
		p.lda(1).commentAfterInstr("Load one to acc")
				.commentAfterInstr("to be used as parameter");
		assertBytes("A9 01");
		assertAssy("lda #$01 ; Load one to acc\n"
				+ "         ; to be used as parameter\n");
		assertJava("lda(0x01).commentAfterInstr(\"Load one to acc\")"
				+ ".commentAfterInstr(\"to be used as parameter\");\n" + "");
	}

	@Test
	public void commentAfterInstrFailsIfNoInstr() {
		try {
			p.commentAfterInstr("Orphan comment");
			fail();
		} catch (MockodoreException e) {
			assertEquals(
					"No instruction to put comment after: " + "Orphan comment",
					e.getMessage());
		}
	}

	@Test
	public void opcodeAsData() {
		p.data(Op.JSR).data(address$1000);
		p.data(Op.NOP);
		assertBytes("20 00 10 EA");
		assertAssy(".b $20 ; JSR abs\n" + ".b #<$1000\n" + ".b #>$1000\n"
				+ ".b $EA ; NOP\n" + "");
		// TODO if needed, produce exactly same java:
		assertJava("data(0x20).commentAfterInstr(\"JSR abs\");\n"
				+ "data(RawAddress.named(0x1000));\n"
				+ "data(0xEA).commentAfterInstr(\"NOP\");\n" + "");
	}

	@Test
	public void labeled() {
		Labeled labeled = new Labeled() {
			@Override
			public Label label() {
				return Label.named("labeled");
			}
		};

		p.startAddress(address$1000);
		p.lda(labeled);
		p.ldy(labeled);
		p.sta(labeled);
		p.label(labeled).data(0xFF);
		p.data(labeled);

		assertAssy("lda labeled\n" + "ldy labeled\n" + "sta labeled\n"
				+ "labeled:\n" + ".b $FF\n" + ".b #<labeled\n"
				+ ".b #>labeled\n" + "");
		assertPrgBytes("00 10 AD 09 10 AC 09 10 8D 09 10 FF 09 10");
	}

	// TODO test assy of prg ending with label

}
