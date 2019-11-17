package org.fluentjava.mockodore.model.labels;

public class AddressRelativeToLabel extends Label {

	private final Label reference;
	private final int offset;

	public AddressRelativeToLabel(Label reference, int offset) {
		super(reference.value() + "+(" + offset + ")");
		this.reference = reference;
		this.offset = offset;
	}

	public Label reference() {
		return reference;
	}

	public int offset() {
		return offset;
	}

	@Override
	public String toString() {
		return asAssy();
	}

	@Override
	public String asAssy() {
		return reference + "+(" + offset + ")";
	}

	@Override
	public String asJava() {
		return reference.asJava() + ".plus(" + offset + ")";
	}

}
