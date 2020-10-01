package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class StatusRegister extends Register {

	public StatusRegister(RegisterWriteListener listener) {
		super("SR", listener);
		set(UnsignedByte.x20);
	}

	public int carryAsZeroOrOne() {
		return signedByte() & 0x01;
	}

	public void setCarry(boolean value) {
		listener.writeStatusC(this, value);
		set(value().withBit0(value));
	}

	public boolean zero() {
		return (signedByte() & 0x02) != 0;
	}

	public void setZero(boolean zero) {
		listener.writeStatusZ(this, zero);
		set(value().withBit2(zero));
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(negative() ? "N" : "n");
		b.append(overflow() ? "V" : "v");
		b.append("-bdi");
		b.append(zero() ? "Z" : "z");
		b.append(carry() ? "C" : "c");
		return b.toString();
	}

	public boolean negative() {
		return (signedByte() & 0x80) != 0;
	}

	public void setNegative(boolean negative) {
		listener.writeStatusN(this, negative);
		set(value().withBit7(negative));
	}

	public void updateN(byte newByte) {
		int newInt = newByte;
		setNegative(newInt < 0);
	}

	public boolean overflow() {
		return (signedByte() & 0x40) != 0;
	}

	public void updateV(byte oldValue, byte newValue) {
		boolean old7 = (oldValue & 0x80) != 0;
		boolean new7 = (newValue & 0x80) != 0;
		if (old7 == new7) {
			// no sign change
			setOverflow(false);
			return;
		}
		boolean old6 = (oldValue & 0x40) != 0;
		boolean cameFromBit6 = (old6 == new7);
		setOverflow(cameFromBit6);
	}

	public void setOverflow(boolean overflow) {
		listener.writeStatusV(this, overflow);
		set(value().withBit6(overflow));
	}

	public boolean carry() {
		return carryAsZeroOrOne() != 0;
	}

	public void toggleCarry() {
		setCarry(!carry());
	}

	@Override
	protected void logChangedValue(UnsignedByte oldValue) {
		listener.writeSr(this, oldValue);
	}

}
