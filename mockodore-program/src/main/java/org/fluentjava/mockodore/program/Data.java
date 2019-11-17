package org.fluentjava.mockodore.program;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class Data implements ByteExtent {

	private final int[] bytes;

	public Data(int[] bytes) {
		this.bytes = bytes;
	}

	public static Data bytes(byte... bytes) {
		int[] ints = new int[bytes.length];
		// System.arraycopy cannot be used because destination is int[]:
		for (int i = 0; i < bytes.length; i++) { // NOPMD
			ints[i] = bytes[i];
		}
		return new Data(ints);
	}

	public static Data bytes(int... bytes) {
		return new Data(bytes);
	}

	public static Data bytes(UnsignedByte... bytes) {
		// TODO delegate conversion to UnsignedByte
		int[] rawBytes = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			UnsignedByte b = bytes[i];
			rawBytes[i] = b.uInt();
		}
		return new Data(rawBytes);
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		ctx.write(bytes);
	}

	@Override
	public void toAssembler(StringBuilder b, String indentation) {
		b.append(indentation);
		b.append(".b ");
		for (int i = 0; i < bytes.length; i++) {
			int aByte = bytes[i];
			b.append("$");
			ByteArrayPrettyPrinter.append(b, (byte) aByte);
			if (i < bytes.length - 1) {
				b.append(", ");
			}
		}
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append("data(");
		for (int i = 0; i < bytes.length; i++) {
			b.append("0x").append(
					ByteArrayPrettyPrinter.spaceSeparatedHex((byte) bytes[i]));
			if (i < bytes.length - 1) {
				b.append(", ");
			}
		}
		b.append(")");
	}

	@Override
	public int length() {
		return bytes.length;
	}

}
