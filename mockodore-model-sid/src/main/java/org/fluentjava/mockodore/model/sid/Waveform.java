package org.fluentjava.mockodore.model.sid;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class Waveform {

	private static final List<Waveform> atoms = new ArrayList<>();

	private static Waveform atom(String name, int value) {
		Waveform wf = new Waveform(name, UnsignedByte.from(value));
		atoms.add(wf);
		return wf;
	}

	public static final Waveform NOISE = atom("NOISE", 0x80);
	public static final Waveform PULSE = atom("PULSE", 0x40);
	public static final Waveform SAW = atom("SAW", 0x20);
	public static final Waveform TRIANGLE = atom("TRIANGLE", 0x10);
	public static final Waveform ALL = NOISE.or(PULSE).or(SAW).or(TRIANGLE);

	private final String name;
	private final UnsignedByte value;

	private Waveform(String name, UnsignedByte value) {
		this.name = name;
		this.value = value;
	}

	public int uInt() {
		return value.uInt();
	}

	public UnsignedByte value() {
		return value;
	}

	public Waveform or(Waveform other) {
		UnsignedByte newValue = value.or(other.value);
		StringBuilder b = new StringBuilder();
		for (Waveform atom : atoms) {
			if (!newValue.and(atom.value).isZero()) {
				b.append(atom.name).append("|");
			}
		}
		String newName = b.toString().replaceFirst("\\|$", "");
		return new Waveform(newName, newValue);
	}

	@Override
	public String toString() {
		return name;
	}

}
