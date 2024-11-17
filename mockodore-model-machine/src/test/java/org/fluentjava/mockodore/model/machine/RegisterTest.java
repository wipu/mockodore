package org.fluentjava.mockodore.model.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTest {

	private C64SimulatorEventListener listener;

	@BeforeEach
	public void before() {
		listener = new C64SimulatorLineLogger();
	}

	private class TestRegister extends Register {

		public TestRegister(String name, C64SimulatorEventListener listener) {
			super(name, listener);
		}

		@Override
		protected void logChangedValue(UnsignedByte oldValue) {
			listener.writeA(this, oldValue);
		}

	}

	@Test
	public void writeAndToHex() {
		Register reg = new TestRegister("R", listener);

		reg.setSignedByte((byte) 0x00);
		assertEquals("00", reg.hexValue());

		reg.setSignedByte((byte) 0x10);
		assertEquals("10", reg.hexValue());

		reg.setSignedByte((byte) -1);
		assertEquals("FF", reg.hexValue());
	}

	@Test
	public void unsignedAndUnsignedRead() {
		Register reg = new TestRegister("R", listener);

		reg.setSignedByte((byte) 0x20);
		assertEquals(32, reg.uInt());
		assertEquals((byte) 32, reg.signedByte());

		reg.setSignedByte((byte) -2);
		assertEquals(254, reg.uInt());
		assertEquals((byte) -2, reg.signedByte());
	}

	@Test
	public void toStringValue() {
		Register reg = new TestRegister("R", listener);

		reg.setSignedByte((byte) 0x20);
		assertEquals("R=$20", reg.toString());
	}

	@Test
	public void setAndGetMyByte() {
		Register reg = new TestRegister("R", listener);

		reg.set(UnsignedByte.from(0x01));

		assertEquals(UnsignedByte.from(0x01), reg.value());
	}

}
