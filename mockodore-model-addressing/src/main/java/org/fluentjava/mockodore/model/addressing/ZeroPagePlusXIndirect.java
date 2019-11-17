package org.fluentjava.mockodore.model.addressing;

public class ZeroPagePlusXIndirect implements Operand {

	private final ZeroPage address;

	public ZeroPagePlusXIndirect(ZeroPage address) {
		this.address = address;
	}

	public ZeroPage address() {
		return address;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		address.writeTo(ctx);
	}

	@Override
	public String asAssy() {
		return "(" + address + ",X)";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".plusXIndirect()";
	}

}
