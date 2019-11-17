package org.fluentjava.mockodore.model.addressing;

public class ZeroPagePlusX implements Operand {

	private final ZeroPage address;

	public ZeroPagePlusX(ZeroPage address) {
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
		return address.asAssy() + ",X";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".plusX()";
	}

}
