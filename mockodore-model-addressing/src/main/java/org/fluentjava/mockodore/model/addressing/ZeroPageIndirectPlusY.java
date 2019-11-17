package org.fluentjava.mockodore.model.addressing;

public class ZeroPageIndirectPlusY implements Operand {

	private final ZeroPage address;

	public ZeroPageIndirectPlusY(ZeroPage address) {
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
		return "(" + address + "),Y";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".indirectPlusY()";
	}

}
