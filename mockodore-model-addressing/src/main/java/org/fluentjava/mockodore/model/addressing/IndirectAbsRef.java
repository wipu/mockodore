package org.fluentjava.mockodore.model.addressing;

public class IndirectAbsRef implements Operand {

	private final AbsRef address;

	private IndirectAbsRef(AbsRef address) {
		this.address = address;
	}

	public static IndirectAbsRef at(AbsRef address) {
		return new IndirectAbsRef(address);
	}

	@Override
	public String toString() {
		return asAssy();
	}

	@Override
	public String asJava() {
		return address.asJava() + ".indirect()";
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		address.writeTo(ctx);
	}

	@Override
	public String asAssy() {
		return "(" + address + ")";
	}

	public AbsRef address() {
		return address;
	}

}
