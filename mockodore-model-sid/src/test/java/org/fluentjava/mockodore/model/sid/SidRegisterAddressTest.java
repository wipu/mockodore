package org.fluentjava.mockodore.model.sid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SidRegisterAddressTest {

	@Test
	public void someAddresses() {
		assertEquals("$D400",
				SidRegisterAddress.FREQ_LO_1.address().toString());
		assertEquals("$D401",
				SidRegisterAddress.FREQ_HI_1.address().toString());
		assertEquals("$D402", SidRegisterAddress.PW_LO_1.address().toString());
		assertEquals("$D403", SidRegisterAddress.PW_HI_1.address().toString());
		assertEquals("$D404", SidRegisterAddress.CR_1.address().toString());
		assertEquals("$D405", SidRegisterAddress.AD_1.address().toString());
		assertEquals("$D406", SidRegisterAddress.SR_1.address().toString());

		assertEquals("$D408",
				SidRegisterAddress.FREQ_HI_2.address().toString());

		assertEquals("$D415", SidRegisterAddress.FCLO.address().toString());
		assertEquals("$D416", SidRegisterAddress.FCHI.address().toString());
		assertEquals("$D417", SidRegisterAddress.RES_FILT.address().toString());
		assertEquals("$D418", SidRegisterAddress.MODE_VOL.address().toString());
	}

	@Test
	public void someNames() {
		assertEquals("FreqLo1", SidRegisterAddress.FREQ_LO_1.name());
		assertEquals("FreqHi1", SidRegisterAddress.FREQ_HI_1.name());

		assertEquals("FreqHi2", SidRegisterAddress.FREQ_HI_2.name());

		assertEquals("FcHi", SidRegisterAddress.FCHI.name());
		assertEquals("ResFilt", SidRegisterAddress.RES_FILT.name());
	}

	@Test
	public void blockOffset() {
		assertEquals(0, SidRegisterAddress.FREQ_LO_1.blockOffset());
		assertEquals(0, SidRegisterAddress.FREQ_HI_1.blockOffset());

		assertEquals(1, SidRegisterAddress.FREQ_LO_2.blockOffset());

		assertEquals(3, SidRegisterAddress.MODE_VOL.blockOffset());
	}

	@Test
	public void indexInBlock() {
		assertEquals(0, SidRegisterAddress.FREQ_LO_1.offsetInBlock());
		assertEquals(1, SidRegisterAddress.FREQ_HI_1.offsetInBlock());

		assertEquals(0, SidRegisterAddress.FREQ_LO_2.offsetInBlock());
		assertEquals(1, SidRegisterAddress.FREQ_HI_2.offsetInBlock());

		assertEquals(3, SidRegisterAddress.MODE_VOL.offsetInBlock());
	}

	@Test
	public void offsetInSid() {
		assertEquals(0x0, SidRegisterAddress.FREQ_LO_1.offsetInSid());
		assertEquals(0x1, SidRegisterAddress.FREQ_HI_1.offsetInSid());

		assertEquals(0x7, SidRegisterAddress.FREQ_LO_2.offsetInSid());
		assertEquals(0x8, SidRegisterAddress.FREQ_HI_2.offsetInSid());

		assertEquals(0x18, SidRegisterAddress.MODE_VOL.offsetInSid());
	}

	@Test
	public void collectionOfAll() {
		List<SidRegisterAddress> all = SidRegisterAddress.all();

		assertEquals(0x19, all.size());
		assertSame(SidRegisterAddress.FREQ_LO_1, all.get(0x00));
		assertSame(SidRegisterAddress.MODE_VOL, all.get(0x18));
	}

	@Test
	public void ofOscAndRegName() {
		assertSame(SidRegisterAddress.FREQ_LO_1,
				SidRegisterAddress.of(OscName.OSC_1, OscRegisterName.FREQ_LO));
		assertSame(SidRegisterAddress.FREQ_LO_2,
				SidRegisterAddress.of(OscName.OSC_2, OscRegisterName.FREQ_LO));
		assertSame(SidRegisterAddress.AD_3,
				SidRegisterAddress.of(OscName.OSC_3, OscRegisterName.AD));
	}

	@Test
	public void offsetInBlockOfRegName() {
		assertEquals(0,
				SidRegisterAddress.offsetInBlock(OscRegisterName.FREQ_LO));
		assertEquals(1,
				SidRegisterAddress.offsetInBlock(OscRegisterName.FREQ_HI));
	}

	@Test
	public void firstAndLast() {
		assertEquals(0xD400, SidRegisterAddress.first().address().value());
		assertEquals(0xD418, SidRegisterAddress.last().address().value());
	}

	@Test
	public void oscName() {
		assertEquals(OscName.OSC_1, SidRegisterAddress.AD_1.osc());
		assertEquals(OscName.OSC_1, SidRegisterAddress.CR_1.osc());

		assertEquals(OscName.OSC_2, SidRegisterAddress.AD_2.osc());
		assertEquals(OscName.OSC_2, SidRegisterAddress.CR_2.osc());

		assertEquals(OscName.OSC_3, SidRegisterAddress.AD_3.osc());
		assertEquals(OscName.OSC_3, SidRegisterAddress.CR_3.osc());

		assertEquals(OscName.GLOBAL, SidRegisterAddress.FCHI.osc());
		assertEquals(OscName.GLOBAL, SidRegisterAddress.MODE_VOL.osc());
	}

}
