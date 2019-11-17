package org.fluentjava.mockodore.model.sid;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class ControlRegister {

	private final UnsignedByte value;

	private ControlRegister(UnsignedByte value) {
		this.value = value;
	}

	public static ControlRegister from(UnsignedByte value) {
		return new ControlRegister(value);
	}

	public boolean isGate() {
		return value.isBit0();
	}

	public boolean isSync() {
		return value.isBit1();
	}

	public boolean isTriangle() {
		return value.isBit4();
	}

	public boolean isSaw() {
		return value.isBit5();
	}

	public boolean isPulse() {
		return value.isBit6();
	}

	public boolean isNoise() {
		return value.isBit7();
	}

}
