package org.fluentjava.mockodore.model.sid;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.Ignore;
import org.junit.Test;

public class Codebase64PalNoteTest {

	@Test
	public void someValues() {
		assertEquals(UnsignedByte.$2D, Codebase64PalNote.A__4.value());
		assertEquals(UnsignedByte.$2E, Codebase64PalNote.Ais4.value());
	}

	@Ignore
	@Test
	public void unignoreMeToGenerateTheNoteConstants() {
		String s = "is";
		String[] names = { "C__", "C" + s, "D__", "D" + s, "E__", "F__",
				"F" + s, "G__", "G" + s, "A__", "A" + s, "B__" };
		int i = 0;
		for (int octave = 0; octave < 8; octave++) {
			for (String name : names) {
				String fullName = name + (octave + 1);
				System.out.println("public static final Codebase64PalNote "
						+ fullName + " = instance(" + i++ + ", \"" + fullName
						+ "\");");
			}
		}
	}

	@Test
	public void allReturnsCorrectAmountOfNotes() {
		List<Codebase64PalNote> all = Codebase64PalNote.all();

		assertEquals(96, all.size());

		assertEquals("#$02", all.get(2).value().toString());
		assertEquals("#$04", all.get(4).value().toString());
	}

	@Test
	public void toStringIsReadable() {
		assertEquals("A__1", Codebase64PalNote.A__1.toString());
		assertEquals("Gis4", Codebase64PalNote.Gis4.toString());
	}

}
