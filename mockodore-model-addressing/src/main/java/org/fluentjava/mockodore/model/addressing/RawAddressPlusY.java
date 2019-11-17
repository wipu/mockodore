package org.fluentjava.mockodore.model.addressing;

public class RawAddressPlusY implements AbsRefPlusY {

	private final RawAddress address;

	public RawAddressPlusY(RawAddress address) {
		this.address = address;
	}

	public RawAddress address() {
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
