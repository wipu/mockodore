package org.fluentjava.mockodore.model.labels;

import org.fluentjava.mockodore.model.addressing.AbsRefPlusY;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class LabelPlusY implements AbsRefPlusY {

	private final Label address;

	public LabelPlusY(Label address) {
		this.address = address;
	}

	public Label address() {
		return address;
	}

	@Override
	public String toString() {
		return asAssy();
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		address.writeTo(ctx);
	}

	@Override
	public String asAssy() {
		return address + ",Y";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".plusY()";
	}

}
