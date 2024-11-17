package org.fluentjava.mockodore.model.addressing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class RawAddressTest {

	@Test
	public void lsbAndMsbToInt() {
		assertEquals(0x0201,
				RawAddress.lsbAndMsbToInt((byte) 0x01, (byte) 0x02));
		assertEquals(0xEFFE,
				RawAddress.lsbAndMsbToInt((byte) 0xFE, (byte) 0xEF));
	}

	@Test
	public void fromMsbAndLsbArray() {
		assertEquals(0x0102,
				RawAddress.fromMsbAndLsb(new byte[] { 1, 2 }).value());
		assertEquals(0xF1E2,
				RawAddress
						.fromMsbAndLsb(new byte[] { (byte) 0xF1, (byte) 0xE2 })
						.value());
	}

	@Test
	public void fromLsbAndMsbArray() {
		assertEquals(0x0201,
				RawAddress.fromLsbAndMsb(new byte[] { 1, 2 }).value());
		assertEquals(0xE2F1,
				RawAddress
						.fromLsbAndMsb(new byte[] { (byte) 0xF1, (byte) 0xE2 })
						.value());
	}

	@Test
	public void equalsAndHashcode() {
		// TODO use guava
		assertTrue(RawAddress.named(0).equals(RawAddress.named(0)));
		assertFalse(RawAddress.named(0).equals(RawAddress.named(1)));
	}

	@Test
	public void equalsAfterPlus() {
		assertEquals(RawAddress.named(1), RawAddress.named(0).plus(1));
	}

	@Test
	public void fromTooSmallAValue() {
		try {
			RawAddress.named(-1);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid value for address: -1", e.getMessage());
		}
	}

	@Test
	public void fromTooLargeAValue() {
		try {
			RawAddress.named(0x10000);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid value for address: 65536", e.getMessage());
		}
	}

}
