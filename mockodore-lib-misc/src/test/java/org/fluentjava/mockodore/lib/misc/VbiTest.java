package org.fluentjava.mockodore.lib.misc;

import static org.junit.Assert.assertEquals;

import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.junit.Test;

public class VbiTest {

	@Test
	public void registerVbiAsAssy() {
		C64AssyLangForProgram p = MockodoreProgram.with();
		Vbi.registerVbi(p, 0x10, Label.named("vbi"));

		assertEquals("sei\n" + "lda #$7F\n" + "sta $DC0D\n" + "and $D011\n"
				+ "sta $D011\n" + "lda #<vbi\n" + "sta $314\n" + "lda #>vbi\n"
				+ "sta $315\n" + "lda #$10\n" + "sta $D012\n" + "sta $02\n"
				+ "lda #$01\n" + "sta $D01A\n" + "cli\n" + "",
				p.end().asAssy());
	}

}
