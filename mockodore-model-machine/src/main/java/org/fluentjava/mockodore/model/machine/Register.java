package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public abstract class Register {

	private final String name;
	protected final RegisterWriteListener listener;
	private UnsignedByte value = UnsignedByte.from(0);

	public Register(String name, RegisterWriteListener listener) {
		this.name = name;
		this.listener = listener;
	}

	public void setSignedByte(byte unsignedByte) {
		set(UnsignedByte.from(unsignedByte));
	}

	public void set(UnsignedByte value) {
		UnsignedByte oldValue = this.value;
		this.value = value;
		logChangedValue(oldValue);
	}

	protected abstract void logChangedValue(UnsignedByte oldValue);

	public int uInt() {
		return value.uInt();
	}

	public byte signedByte() {
		return value.signedByte();
	}

	public String hexValue() {
		return ByteArrayPrettyPrinter.spaceSeparatedHex(value.signedByte());
	}

	@Override
	public String toString() {
		return name + "=$" + hexValue();
	}

	public UnsignedByte value() {
		return value;
	}

}
