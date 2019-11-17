package org.fluentjava.mockodore.model.addressing;

public class ImpliedOperand implements Operand {

	public static final ImpliedOperand INSTANCE = new ImpliedOperand();

	private ImpliedOperand() {
		// nothing to do
	}

	@Override
	public String asAssy() {
		return "";
	}

	@Override
	public String asJava() {
		return "";
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		// nothing
	}

}