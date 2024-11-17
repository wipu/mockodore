package org.fluentjava.mockodore.model.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatusRegisterTest {

	private C64SimulatorLineLogger listener;
	private StatusRegister sr;

	@BeforeEach
	public void before() {
		listener = new C64SimulatorLineLogger();
		sr = new StatusRegister(listener);
	}

	@Test
	public void initialByteValueAndToString() {
		// the unused bit 5 is always 1
		assertEquals(0x20, sr.signedByte());
		assertEquals("nv-bdizc", sr.toString());
	}

	@Test
	public void carryAsZeroOrOneAndToStringAndBoolean() {
		assertEquals(0, sr.carryAsZeroOrOne());
		assertEquals("nv-bdizc", sr.toString());
		assertFalse(sr.carry());

		sr.setCarry(true);
		assertEquals(1, sr.carryAsZeroOrOne());
		assertEquals("nv-bdizC", sr.toString());
		assertTrue(sr.carry());

		sr.setCarry(false);
		assertEquals(0, sr.carryAsZeroOrOne());
		assertEquals("nv-bdizc", sr.toString());
		assertFalse(sr.carry());
	}

	@Test
	public void zero() {
		assertFalse(sr.zero());
		assertEquals("nv-bdizc", sr.toString());

		sr.setZero(true);
		assertTrue(sr.zero());
		assertEquals("nv-bdiZc", sr.toString());

		sr.setZero(false);
		assertFalse(sr.zero());
		assertEquals("nv-bdizc", sr.toString());
	}

	@Test
	public void negative() {
		assertFalse(sr.negative());
		assertEquals("nv-bdizc", sr.toString());

		sr.setNegative(true);
		assertTrue(sr.negative());
		assertEquals("Nv-bdizc", sr.toString());

		sr.setNegative(false);
		assertFalse(sr.negative());
		assertEquals("nv-bdizc", sr.toString());
	}

	@Test
	public void updateN() {
		assertFalse(sr.negative());

		sr.updateN((byte) 0xFF);
		assertTrue(sr.negative());

		sr.updateN((byte) 0x00);
		assertFalse(sr.negative());
	}

	@Test
	public void directManipulationOfOverflow() {
		assertFalse(sr.overflow());
		assertEquals("nv-bdizc", sr.toString());

		sr.setOverflow(true);
		assertTrue(sr.overflow());
		assertEquals("nV-bdizc", sr.toString());

		sr.setOverflow(false);
		assertFalse(sr.overflow());
		assertEquals("nv-bdizc", sr.toString());
	}

	// corner values for overflow, min and max refer to the unsigned value:

	private static final byte OOx = (byte) 0x00;
	private static final byte OIx = (byte) 0x40;
	private static final byte IOx = (byte) 0x80;
	private static final byte IIx = (byte) 0xC0;

	private void noOverflow(byte oldValue, byte newValue) {
		sr.updateV(oldValue, newValue);
		assertFalse(sr.overflow());
	}

	private void doesOverflow(byte oldValue, byte newValue) {
		sr.updateV(oldValue, newValue);
		assertTrue(sr.overflow());
	}

	@Test
	public void noOverflowWhenSignStaysTheSame() {
		noOverflow(IIx, IOx);
		noOverflow(IOx, IOx);

		noOverflow(OOx, OIx);
		noOverflow(OIx, OOx);
	}

	@Test
	public void noOverflowWhenSignChangesButDoesNotComeFromBit6() {
		noOverflow(OOx, IOx);
		noOverflow(OOx, IIx);

		noOverflow(IIx, OOx);
		noOverflow(IIx, OIx);
	}

	@Test
	public void overflowWhenSignChangesAndComesFromBit6() {
		doesOverflow(OIx, IOx);
		doesOverflow(OIx, IIx);

		doesOverflow(IOx, OOx);
		doesOverflow(IOx, OIx);
	}

	@Test
	public void overflowAsBitAndToString() {
		sr.updateV(OIx, IIx);
		assertTrue(sr.overflow());

		assertEquals(0x60, sr.signedByte());
		assertEquals("nV-bdizc", sr.toString());
	}

	@Test
	public void toggleCarry() {
		assertFalse(sr.carry());
		sr.toggleCarry();
		assertTrue(sr.carry());
		sr.toggleCarry();
		assertFalse(sr.carry());
	}

	@Test
	public void setNotifiesOfNewValueAndUnchangedValueInDifferentSyntax() {
		assertFalse(sr.carry());

		listener.clearEventLog();
		sr.setCarry(true);
		assertEquals("0:      (* SR:C)\n0:      (* nv-bdizC)\n",
				listener.eventLog());

		listener.clearEventLog();
		sr.setCarry(true);
		assertEquals("0:      (- SR:C)\n0:      (- nv-bdizC)\n",
				listener.eventLog());
	}

}
