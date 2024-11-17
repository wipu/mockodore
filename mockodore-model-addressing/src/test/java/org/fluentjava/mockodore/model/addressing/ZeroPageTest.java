package org.fluentjava.mockodore.model.addressing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.Test;

public class ZeroPageTest {

	@Test
	public void equalsAndHashCode() {
		assertTrue(ZeroPage.from(0x00).equals(ZeroPage.from(0x00)));
		assertFalse(ZeroPage.from(0x00).equals(ZeroPage.from(0x01)));

		assertEquals(0x00, ZeroPage.from(0x00).hashCode());
		assertEquals(0x01, ZeroPage.from(0x01).hashCode());
	}

	@Test
	public void plusWraps() {
		assertEquals(ZeroPage.from(0x00), ZeroPage.from(0xFF).plus(1));
		assertEquals(ZeroPage.from(0x01), ZeroPage.from(0xFE).plus(3));
		assertEquals(ZeroPage.from(0x7F), ZeroPage.from(0x80).plus(-1));
		assertEquals(ZeroPage.from(0x81), ZeroPage.from(0x01).plus(0x80));
	}

	@Test
	public void fromLegalRawAddress() {
		ZeroPage zp = ZeroPage.from(RawAddress.named(0xFB));
		assertEquals(UnsignedByte.xFB, zp.value());
	}

	@Test
	public void fromTooLargeARawAddress() {
		try {
			ZeroPage.from(RawAddress.named(256));
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Not a suitable address for zeropage: $100",
					e.getMessage());
		}
	}

}
