package org.fluentjava.mockodore.model.labels;

import org.fluentjava.mockodore.model.addressing.AbsRefPlusX;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class LabelPlusX implements AbsRefPlusX {

	private final Label address;

	public LabelPlusX(Label address) {
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
		return address + ",X";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".plusX()";
	}

}
