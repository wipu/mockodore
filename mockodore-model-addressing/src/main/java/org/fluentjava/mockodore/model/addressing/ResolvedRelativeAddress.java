package org.fluentjava.mockodore.model.addressing;

public class ResolvedRelativeAddress implements Operand {

	private final int programRelativeFrom;
	private final AbsRef to;

	private ResolvedRelativeAddress(int programRelativeFrom, AbsRef to) {
		this.programRelativeFrom = programRelativeFrom;
		this.to = to;

	}

	public static Operand fromTo(int programRelativeFrom, AbsRef to) {
		return new ResolvedRelativeAddress(programRelativeFrom, to);
	}

	@Override
	public String asAssy() {
		return to.asAssy();
	}

	@Override
	public String asJava() {
		return to.asJava();
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		RawAddress rawFrom = ctx.resolve(programRelativeFrom);
		RawAddress rawTo = ctx.resolve(to);
		int diff = rawTo.value() - rawFrom.value() - 2;
		if (diff < -128 || diff > 127) {
			throw tooFar(diff, rawFrom);
		}
		ctx.write(diff);
	}

	private MockodoreException tooFar(int diff, RawAddress rawFrom) {
		String sign = diff < 0 ? "" : "+";
		throw new MockodoreException("Address '" + to + "' is too far from "
				+ rawFrom + " for a relative reference: " + sign + diff);
	}

}
