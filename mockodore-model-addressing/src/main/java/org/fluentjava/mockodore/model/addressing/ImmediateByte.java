package org.fluentjava.mockodore.model.addressing;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class ImmediateByte implements Operand {

	private final UnsignedByte value;

	public ImmediateByte(UnsignedByte value) {
		this.value = value;
	}

	public ImmediateByte(int value) {
		this(UnsignedByte.from(value));
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		ctx.write(value.signedByte());
	}

	@Override
	public String asAssy() {
		return value.toString();
	}

	public UnsignedByte value() {
		return value;
	}

	@Override
	public String toString() {
		return asAssy();
	}

	@Override
	public String asJava() {
		return "0x" + ByteArrayPrettyPrinter
				.spaceSeparatedHex(value().signedByte());
	}

}
