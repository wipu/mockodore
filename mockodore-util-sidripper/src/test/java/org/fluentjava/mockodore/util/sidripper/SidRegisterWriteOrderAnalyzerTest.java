package org.fluentjava.mockodore.util.sidripper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.fluentjava.mockodore.util.sidripper.SidRegisterWriteOrderAnalyzer.HappensBefore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidRegisterWriteOrderAnalyzerTest {

	private SidRegisterWriteOrderAnalyzer o;

	@BeforeEach
	public void before() {
		o = new SidRegisterWriteOrderAnalyzer(0);
	}

	private void initInRegOrder() {
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			write(reg);
		}
	}

	private void write(SidRegisterAddress reg) {
		// the value does not matter:
		o.reg(reg).write(UnsignedByte.x01);
	}

	private static SidRegisterAddress reg(int offset) {
		return SidRegisterAddress.all().get(offset);
	}

	@SuppressWarnings("unchecked")
	private static <T> void assertOrder(Comparable<T> a, Comparable<T> b) {
		assertTrue(a.compareTo((T) b) < 0);
		assertTrue(b.compareTo((T) a) > 0);
	}

	// the tests
	// --------------------------------------------------------------

	@Test
	public void noWrites() {
		// nothing

		assertEquals("[]", o.playFrames().toString());
		assertEquals("[]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
	}

	@Test
	public void onlyInit() {
		initInRegOrder();

		assertEquals("[]", o.playFrames().toString());
		assertEquals("[]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
	}

	@Test
	public void initIteratesInRegOrderAndTwoFramesWriteNothing() {
		initInRegOrder();
		o.playCallStarting();
		o.playCallStarting();

		assertEquals("[0:[], 1:[]]", o.playFrames().toString());
		assertEquals("[]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
	}

	@Test
	public void initIteratesInRegOrderAndTwoFramesWriteExactSameOneReg() {
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);

		assertEquals("[0:[AD1], 1:[AD1]]", o.playFrames().toString());
		assertEquals("[]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
	}

	@Test
	public void initIteratesInRegOrderAndTwoFramesWriteExactSameTwoRegs() {
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);

		assertEquals("[0:[AD1, SR1], 1:[AD1, SR1]]", o.playFrames().toString());
		assertEquals("[AD1 < SR1]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
		assertEquals("[AD1, SR1]", o.commonOrderOfRegisters().toString());
	}

	@Test
	public void initAndTwoFramesBuSkippingFirstFrame() {
		o = new SidRegisterWriteOrderAnalyzer(1);
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);

		assertEquals("[1:[AD1, SR1]]", o.playFrames().toString());
		assertEquals("[AD1 < SR1]", o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
		assertEquals("[AD1, SR1]", o.commonOrderOfRegisters().toString());
	}

	@Test
	public void initIteratesInRegOrderAndTwoFramesWriteSameTwoRegsInReverseOrder() {
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		o.playCallStarting();
		write(SidRegisterAddress.SR_1);
		write(SidRegisterAddress.AD_1);

		assertEquals("[0:[AD1, SR1], 1:[SR1, AD1]]", o.playFrames().toString());
		assertEquals("[AD1 < SR1, SR1 < AD1]",
				o.allPlayFrameHappensBefores().toString());
		assertEquals("[SR1 < AD1 contradicts AD1 < SR1]",
				o.contradictions().toString());
		try {
			o.commonOrderOfRegisters();
			fail();
		} catch (IllegalStateException e) {
			assertEquals("No common order of register writes", e.getMessage());
		}
	}

	@Test
	public void happensBeforeComparisons() {
		HappensBefore hb0_1 = new HappensBefore(reg(0), reg(1));
		HappensBefore hb0_2 = new HappensBefore(reg(0), reg(2));
		HappensBefore hb0_3 = new HappensBefore(reg(0), reg(3));
		HappensBefore hb1_2 = new HappensBefore(reg(1), reg(2));
		HappensBefore hb1_3 = new HappensBefore(reg(1), reg(3));
		HappensBefore hb2_3 = new HappensBefore(reg(2), reg(3));

		assertOrder(hb0_1, hb0_2);
		assertOrder(hb0_1, hb0_3);
		assertOrder(hb0_1, hb1_2);
		assertOrder(hb0_1, hb1_3);
		assertOrder(hb0_1, hb2_3);

		assertOrder(hb0_2, hb0_3);
		assertOrder(hb0_2, hb1_2);
		assertOrder(hb0_2, hb1_3);
		assertOrder(hb0_2, hb2_3);

		assertOrder(hb0_3, hb1_2);
		assertOrder(hb0_3, hb1_3);
		assertOrder(hb0_3, hb2_3);

		assertOrder(hb1_3, hb2_3);
	}

	@Test
	public void initIteratesInRegOrderAndThreeFramesWriteOnlyPartlySameRegisters() {
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		write(SidRegisterAddress.CR_1);
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.CR_1);
		o.playCallStarting();
		write(SidRegisterAddress.SR_1);
		write(SidRegisterAddress.CR_1);

		assertEquals("[0:[AD1, SR1, CR1], 1:[AD1, CR1], 2:[SR1, CR1]]",
				o.playFrames().toString());
		assertEquals("[AD1 < CR1, AD1 < SR1, SR1 < CR1]",
				o.allPlayFrameHappensBefores().toString());
		assertEquals("[]", o.contradictions().toString());
		assertEquals("[AD1, SR1, CR1]", o.commonOrderOfRegisters().toString());
	}

	@Test
	public void initIteratesInRegOrderAndOtherwiseGoodButOneRegIsWrittenTwiceDuringFrame() {
		initInRegOrder();
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		o.playCallStarting();
		write(SidRegisterAddress.AD_1);
		write(SidRegisterAddress.SR_1);
		write(SidRegisterAddress.AD_1);

		assertEquals("[0:[AD1, SR1], 1:[AD1, SR1, AD1]]",
				o.playFrames().toString());
		assertEquals("[AD1 < AD1, AD1 < SR1, SR1 < AD1]",
				o.allPlayFrameHappensBefores().toString());
		assertEquals(
				"[AD1 written many times, SR1 < AD1 contradicts AD1 < SR1]",
				o.contradictions().toString());
		try {
			o.commonOrderOfRegisters();
			fail();
		} catch (IllegalStateException e) {
			assertEquals("No common order of register writes", e.getMessage());
		}
	}

}
