package org.fluentjava.mockodore.model.labels;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.IndirectAbsRef;
import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class Label implements AbsRef {

	private static final Map<String, Label> labelsByValue = new LinkedHashMap<>();

	private final String value;

	public Label(String value) {
		this.value = value;
	}

	public static Label named(String value) {
		if (!isValidName(value)) {
			throw new MockodoreException(
					"Invalid label syntax: '" + value + "'");
		}
		Label label = labelsByValue.get(value);
		if (label == null) {
			label = new Label(value);
			labelsByValue.put(value, label);
		}
		return label;
	}

	private static boolean isValidName(String s) {
		if (s.length() <= 0) {
			return false;
		}
		if (Character.isDigit(s.charAt(0))) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!isValidNameChar(c)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidNameChar(char c) {
		if (Character.isDigit(c)) {
			return true;
		}
		if ('a' <= c && c <= 'z') {
			return true;
		}
		if ('A' <= c && c <= 'Z') {
			return true;
		}
		if ("-_".indexOf(c) >= 0) {
			return true;
		}
		return false;
	}

	public String value() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	public LabelLsb lsb() {
		return new LabelLsb(this);
	}

	public LabelMsb msb() {
		return new LabelMsb(this);
	}

	@Override
	public Label plus(int offset) {
		return new AddressRelativeToLabel(this, offset);
	}

	public IndirectAbsRef indirect() {
		return IndirectAbsRef.at(this);
	}

	public LabelPlusX plusX() {
		return new LabelPlusX(this);
	}

	public LabelPlusY plusY() {
		return new LabelPlusY(this);
	}

	@Override
	public String asAssy() {
		return value();
	}

	@Override
	public String asJava() {
		return value;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		RawAddress raw = ctx.resolve(this);
		raw.writeTo(ctx);
	}

	public Label subLabel(String subName) {
		return Label.named(value() + subName);
	}

}
