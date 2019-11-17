package org.fluentjava.mockodore.model.sid;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.Before;
import org.junit.Test;

public class Codebase64PalNoteFreqsTest {

	private Codebase64PalNoteFreqs freqs;

	@Before
	public void before() {
		freqs = new Codebase64PalNoteFreqs();
	}

	@Test
	public void someValues() {
		assertEquals(UnsignedByte.$17, freqs.lo(Codebase64PalNote.C__1));
		assertEquals(UnsignedByte.$01, freqs.hi(Codebase64PalNote.C__1));

		assertEquals(UnsignedByte.$43, freqs.lo(Codebase64PalNote.G__2));
		assertEquals(UnsignedByte.$03, freqs.hi(Codebase64PalNote.G__2));

		assertEquals(UnsignedByte.$A3, freqs.lo(Codebase64PalNote.A__4));
		assertEquals(UnsignedByte.$0E, freqs.hi(Codebase64PalNote.A__4));

		assertEquals(UnsignedByte.$82, freqs.lo(Codebase64PalNote.Ais4));
		assertEquals(UnsignedByte.$0F, freqs.hi(Codebase64PalNote.Ais4));
	}

}
