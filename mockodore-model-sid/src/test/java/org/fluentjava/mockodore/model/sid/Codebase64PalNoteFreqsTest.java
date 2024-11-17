package org.fluentjava.mockodore.model.sid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Codebase64PalNoteFreqsTest {

	private Codebase64PalNoteFreqs freqs;

	@BeforeEach
	public void before() {
		freqs = new Codebase64PalNoteFreqs();
	}

	@Test
	public void someValues() {
		assertEquals(UnsignedByte.x17, freqs.lo(Codebase64PalNote.C__1));
		assertEquals(UnsignedByte.x01, freqs.hi(Codebase64PalNote.C__1));

		assertEquals(UnsignedByte.x43, freqs.lo(Codebase64PalNote.G__2));
		assertEquals(UnsignedByte.x03, freqs.hi(Codebase64PalNote.G__2));

		assertEquals(UnsignedByte.xA3, freqs.lo(Codebase64PalNote.A__4));
		assertEquals(UnsignedByte.x0E, freqs.hi(Codebase64PalNote.A__4));

		assertEquals(UnsignedByte.x82, freqs.lo(Codebase64PalNote.Ais4));
		assertEquals(UnsignedByte.x0F, freqs.hi(Codebase64PalNote.Ais4));
	}

}
