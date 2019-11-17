package org.fluentjava.mockodore.model.addressing;

public class RawAddressPlusX implements AbsRefPlusX {

	private final RawAddress address;

	public RawAddressPlusX(RawAddress address) {
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
		return address + ",X";
	}

	@Override
	public String asJava() {
		return address.asJava() + ".plusX()";
	}

}
