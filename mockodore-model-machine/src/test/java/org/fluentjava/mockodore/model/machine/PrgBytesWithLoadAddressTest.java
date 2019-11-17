package org.fluentjava.mockodore.model.machine;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class PrgBytesWithLoadAddressTest {

	@Test
	public void allBytes() {
		assertEquals("[1, 2, 3, 4]",
				Arrays.toString(
						new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3, 4, })
								.allBytes()));
		assertEquals("[2, 3, 4]",
				Arrays.toString(
						new PrgBytesWithLoadAddress(new byte[] { 2, 3, 4, })
								.allBytes()));
	}

	@Test
	public void address() {
		assertEquals("$201",
				new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3, 4, })
						.address().toString());
		assertEquals("$302",
				new PrgBytesWithLoadAddress(new byte[] { 2, 3, 4, }).address()
						.toString());
	}

	@Test
	public void justPrgBytes() {
		assertEquals("[3, 4]",
				Arrays.toString(
						new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3, 4, })
								.justPrgBytes()));
		assertEquals("[4]",
				Arrays.toString(
						new PrgBytesWithLoadAddress(new byte[] { 2, 3, 4, })
								.justPrgBytes()));
	}

	@Test
	public void equalsAndHashcode() {
		EqualsTester et = new EqualsTester();
		et.addEqualityGroup(new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3 }),
				new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3 }));
		et.addEqualityGroup(
				new PrgBytesWithLoadAddress(new byte[] { 4, 5, 6, 7 }),
				new PrgBytesWithLoadAddress(new byte[] { 4, 5, 6, 7 }));

		et.testEquals();
	}

	@Test
	public void toStringIsHex() {
		assertEquals("01 02 03",
				new PrgBytesWithLoadAddress(new byte[] { 1, 2, 3 }).toString());
	}

}
