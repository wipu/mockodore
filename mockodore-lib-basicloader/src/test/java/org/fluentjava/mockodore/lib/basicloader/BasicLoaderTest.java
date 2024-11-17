package org.fluentjava.mockodore.lib.basicloader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.junit.jupiter.api.Test;

public class BasicLoaderTest {

	@Test
	public void loadAddressAndPrgOfTinyPrgWithLoader() {
		C64AssyLangForProgram p = MockodoreProgram.with();
		BasicLoader.def(p);
		p.lda(0x01).rts();

		assertEquals("01 08 0C 08 0A 00 9E 20 32 30 36 32 00 00 00 A9 01 60",
				ByteArrayPrettyPrinter
						.spaceSeparatedHex(p.end().asPrgBytes().allBytes()));
	}

}
