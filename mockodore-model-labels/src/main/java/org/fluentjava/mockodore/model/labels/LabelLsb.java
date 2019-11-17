package org.fluentjava.mockodore.model.labels;

import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class LabelLsb implements Operand {

	private final Label label;

	public LabelLsb(Label label) {
		this.label = label;
	}

	public Label label() {
		return label;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		RawAddress raw = ctx.resolve(label);
		ctx.write(raw.lsb());
	}

	@Override
	public String asAssy() {
		return "#<" + label;
	}

	@Override
	public String asJava() {
		return label.asJava() + ".lsb()";
	}

	@Override
	public String toString() {
		return asAssy();
	}

}
