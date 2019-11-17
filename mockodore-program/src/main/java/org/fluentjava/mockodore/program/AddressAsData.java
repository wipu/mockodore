package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class AddressAsData implements ByteExtent {

	private final AbsRef address;

	public AddressAsData(AbsRef address) {
		this.address = address;
	}

	public static AddressAsData from(AbsRef address) {
		return new AddressAsData(address);
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		address.writeTo(ctx);
	}

	@Override
	public void toAssembler(StringBuilder b, String indentation) {
		b.append(indentation);
		b.append(".b #<").append(address).append("\n");
		b.append(indentation);
		b.append(".b #>").append(address);
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append("data(" + address.asJava() + ")");
	}

}
