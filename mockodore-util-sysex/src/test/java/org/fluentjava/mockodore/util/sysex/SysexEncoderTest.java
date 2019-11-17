package org.fluentjava.mockodore.util.sysex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.Test;

public class SysexEncoderTest {

	private static List<UnsignedByte> sysexEncoded(int... bytes) {
		return SysexEncoder.sysexEncoded(bytes);
	}

	private static void sysexEncodingShallFail(int... bytes) {
		try {
			sysexEncoded(bytes);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Data size must be multiple of 7", e.getMessage());
		}
	}

	private static void sysexEncodingShallBe(String expected, int... bytes) {
		List<UnsignedByte> actual = sysexEncoded(bytes);
		byte[] actualBytes = new byte[actual.size()];
		for (int i = 0; i < actualBytes.length; i++) {
			actualBytes[i] = actual.get(i).signedByte();
		}
		assertEquals(expected,
				ByteArrayPrettyPrinter.spaceSeparatedHex(actualBytes));
	}

	@Test
	public void sysexEncodingSucceedsIfAndOnlyWhenSizeIsMultipleOf7() {
		sysexEncodingShallBe("");
		sysexEncodingShallFail(1);
		sysexEncodingShallFail(1, 2, 3, 4, 5, 6);
		sysexEncodingShallBe("01 02 03 04 05 06 07 00", 1, 2, 3, 4, 5, 6, 7);
		sysexEncodingShallFail(1, 2, 3, 4, 5, 6, 7, 8);
		sysexEncodingShallBe("01 02 03 04 05 06 07 00 08 09 0A 0B 0C 0D 0E 00",
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
	}

	@Test
	public void sysexEncodingPutsMsbsAfterGroupOf7() {
		sysexEncodingShallBe("01 02 03 04 05 06 07 00", 1, 2, 3, 4, 5, 6, 7);
		sysexEncodingShallBe("01 02 03 04 05 06 07 7F", 0x81, 0x82, 0x83, 0x84,
				0x85, 0x86, 0x87);

		// here we see the order
		sysexEncodingShallBe("01 02 03 04 05 06 07 01", 0x81, 2, 3, 4, 5, 6, 7);
		sysexEncodingShallBe("01 02 03 04 05 06 07 40", 1, 2, 3, 4, 5, 6, 0x87);
	}

	@Test
	public void endOfSysexIsNotAProblem() {
		sysexEncodingShallBe("77 77 77 77 77 77 77 7F", 0xF7, 0xF7, 0xF7, 0xF7,
				0xF7, 0xF7, 0xF7);
	}

}
