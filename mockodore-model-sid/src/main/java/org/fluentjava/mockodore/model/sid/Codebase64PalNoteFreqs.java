package org.fluentjava.mockodore.model.sid;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class Codebase64PalNoteFreqs {

	private static final List<UnsignedByte> lo = new ArrayList<>();
	private static final List<UnsignedByte> hi = new ArrayList<>();

	private static void octaveLo(@SuppressWarnings("unused") int octave,
			UnsignedByte... values) {
		lo.addAll(Arrays.asList(values));
	}

	private static void octaveHi(@SuppressWarnings("unused") int octave,
			UnsignedByte... values) {
		hi.addAll(Arrays.asList(values));
	}

	static {
		octaveLo(1, x17, x27, x39, x4B, x5F, x74, x8A, xA1, xBA, xD4, xF0, x0E);
		octaveLo(2, x2D, x4E, x71, x96, xBE, xE8, x14, x43, x74, xA9, xE1, x1C);
		octaveLo(3, x5A, x9C, xE2, x2D, x7C, xCF, x28, x85, xE8, x52, xC1, x37);
		octaveLo(4, xB4, x39, xC5, x5A, xF7, x9E, x4F, x0A, xD1, xA3, x82, x6E);
		octaveLo(5, x68, x71, x8A, xB3, xEE, x3C, x9E, x15, xA2, x46, x04, xDC);
		octaveLo(6, xD0, xE2, x14, x67, xDD, x79, x3C, x29, x44, x8D, x08, xB8);
		octaveLo(7, xA1, xC5, x28, xCD, xBA, xF1, x78, x53, x87, x1A, x10, x71);
		octaveLo(8, x42, x89, x4F, x9B, x74, xE2, xF0, xA6, x0E, x33, x20, xFF);
	}

	static {
		octaveHi(1, x01, x01, x01, x01, x01, x01, x01, x01, x01, x01, x01, x02);
		octaveHi(2, x02, x02, x02, x02, x02, x02, x03, x03, x03, x03, x03, x04);
		octaveHi(3, x04, x04, x04, x05, x05, x05, x06, x06, x06, x07, x07, x08);
		octaveHi(4, x08, x09, x09, x0A, x0A, x0B, x0C, x0D, x0D, x0E, x0F, x10);
		octaveHi(5, x11, x12, x13, x14, x15, x17, x18, x1A, x1B, x1D, x1F, x20);
		octaveHi(6, x22, x24, x27, x29, x2B, x2E, x31, x34, x37, x3A, x3E, x41);
		octaveHi(7, x45, x49, x4E, x52, x57, x5C, x62, x68, x6E, x75, x7C, x83);
		octaveHi(8, x8B, x93, x9C, xA5, xAF, xB9, xC4, xD0, xDD, xEA, xF8, xFF);
	}

	public UnsignedByte lo(Codebase64PalNote note) {
		return lo.get(note.value().uInt());
	}

	public UnsignedByte hi(Codebase64PalNote note) {
		return hi.get(note.value().uInt());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
