package org.fluentjava.mockodore.model.addressing;

public class RelativeAddress {

	private final AbsRef to;

	private RelativeAddress(AbsRef to) {
		this.to = to;

	}

	public static RelativeAddress to(AbsRef to) {
		return new RelativeAddress(to);
	}

	public AbsRef value() {
		return to;
	}

}
