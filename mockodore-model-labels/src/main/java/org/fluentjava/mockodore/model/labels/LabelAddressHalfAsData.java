package org.fluentjava.mockodore.model.labels;

import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class LabelAddressHalfAsData implements ByteExtent {

	private final Operand halfAddress;

	private LabelAddressHalfAsData(Operand halfAddress) {
		this.halfAddress = halfAddress;
	}

	public static LabelAddressHalfAsData from(LabelLsb halfAddress) {
		return new LabelAddressHalfAsData(halfAddress);
	}

	public static LabelAddressHalfAsData from(LabelMsb halfAddress) {
		return new LabelAddressHalfAsData(halfAddress);
	}

	@Override
	public int length() {
		return 1;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		halfAddress.writeTo(ctx);
	}

	@Override
	public void toAssembler(StringBuilder b, String indentation) {
		b.append(indentation);
		b.append(".b ").append(halfAddress);
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append("data(").append(halfAddress.asJava()).append(")");
	}

}
