package org.fluentjava.mockodore.model.addressing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class RawAddress implements AbsRef {

	private static final Map<Integer, RawAddress> labelsByValue = new LinkedHashMap<>();

	private final int value;

	private RawAddress(int value) {
		this.value = value;
	}

	public static RawAddress named(int value) {
		if (value < 0 || value > 65535) {
			throw new IllegalArgumentException(
					"Invalid value for address: " + value);
		}
		RawAddress label = labelsByValue.get(value);
		if (label == null) {
			label = new RawAddress(value);
			labelsByValue.put(value, label);
		}
		return label;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		return asAssy();
	}

	public byte lsb() {
		return (byte) (value & 0xff);
	}

	public byte msb() {
		return (byte) ((value & 0xff00) >> 8);
	}

	public IndirectAbsRef indirect() {
		return IndirectAbsRef.at(this);
	}

	static int lsbAndMsbToInt(byte lsb, byte msb) {
		return ((msb << 8) & 0xFF00) | (lsb & 0xFF);
	}

	static int lsbAndMsbToInt(UnsignedByte lsb, UnsignedByte msb) {
		return lsbAndMsbToInt(lsb.signedByte(), msb.signedByte());
	}

	public static RawAddress fromLsbAndMsb(byte lsb, byte msb) {
		return RawAddress.named(lsbAndMsbToInt(lsb, msb));
	}

	public static RawAddress fromLsbAndMsb(UnsignedByte lsb, UnsignedByte msb) {
		return RawAddress.named(lsbAndMsbToInt(lsb, msb));
	}

	public static RawAddress fromMsbAndLsb(byte[] lsbAndMsb) {
		byte msb = lsbAndMsb[0];
		byte lsb = lsbAndMsb[1];
		return RawAddress.fromLsbAndMsb(lsb, msb);
	}

	public static RawAddress fromLsbAndMsb(byte[] lsbAndMsb) {
		byte lsb = lsbAndMsb[0];
		byte msb = lsbAndMsb[1];
		return RawAddress.fromLsbAndMsb(lsb, msb);
	}

	@Override
	public RawAddress plus(int delta) {
		return named(value + delta);
	}

	public RawAddressPlusX plusX() {
		return new RawAddressPlusX(this);
	}

	public RawAddressPlusY plusY() {
		return new RawAddressPlusY(this);
	}

	@Override
	public String asAssy() {
		return "$" + Integer.toHexString(value).toUpperCase();
	}

	@Override
	public String asJava() {
		return "RawAddress.named(0x" + Integer.toHexString(value).toUpperCase()
				+ ")";
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		ctx.write(lsb());
		ctx.write(msb());
	}

}
