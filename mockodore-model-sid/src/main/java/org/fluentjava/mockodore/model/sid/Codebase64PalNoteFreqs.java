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
		octaveLo(1, $17, $27, $39, $4B, $5F, $74, $8A, $A1, $BA, $D4, $F0, $0E);
		octaveLo(2, $2D, $4E, $71, $96, $BE, $E8, $14, $43, $74, $A9, $E1, $1C);
		octaveLo(3, $5A, $9C, $E2, $2D, $7C, $CF, $28, $85, $E8, $52, $C1, $37);
		octaveLo(4, $B4, $39, $C5, $5A, $F7, $9E, $4F, $0A, $D1, $A3, $82, $6E);
		octaveLo(5, $68, $71, $8A, $B3, $EE, $3C, $9E, $15, $A2, $46, $04, $DC);
		octaveLo(6, $D0, $E2, $14, $67, $DD, $79, $3C, $29, $44, $8D, $08, $B8);
		octaveLo(7, $A1, $C5, $28, $CD, $BA, $F1, $78, $53, $87, $1A, $10, $71);
		octaveLo(8, $42, $89, $4F, $9B, $74, $E2, $F0, $A6, $0E, $33, $20, $FF);
	}

	static {
		octaveHi(1, $01, $01, $01, $01, $01, $01, $01, $01, $01, $01, $01, $02);
		octaveHi(2, $02, $02, $02, $02, $02, $02, $03, $03, $03, $03, $03, $04);
		octaveHi(3, $04, $04, $04, $05, $05, $05, $06, $06, $06, $07, $07, $08);
		octaveHi(4, $08, $09, $09, $0A, $0A, $0B, $0C, $0D, $0D, $0E, $0F, $10);
		octaveHi(5, $11, $12, $13, $14, $15, $17, $18, $1A, $1B, $1D, $1F, $20);
		octaveHi(6, $22, $24, $27, $29, $2B, $2E, $31, $34, $37, $3A, $3E, $41);
		octaveHi(7, $45, $49, $4E, $52, $57, $5C, $62, $68, $6E, $75, $7C, $83);
		octaveHi(8, $8B, $93, $9C, $A5, $AF, $B9, $C4, $D0, $DD, $EA, $F8, $FF);
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
