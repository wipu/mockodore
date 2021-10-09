package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.Before;
import org.junit.Test;

public class FullFrameHexDumperTest {

	private List<UnsignedByte> sid;
	private FullFrameHexDumper log;

	@Before
	public void before() {
		sid = new ArrayList<>();
		for (int i = 0; i < SidRegisterAddress.all().size(); i++) {
			sid.add(UnsignedByte.xCC);
		}
		log = new FullFrameHexDumper(sid);
	}

	@Test
	public void arglessConstructorUsesZeros() {
		log = new FullFrameHexDumper();
		log.playCallStarting();
		assertEquals(
				"00000000000000000000000000000000000000000000000000\n" + "",
				log.toString());
	}

	@SuppressWarnings("unused")
	@Test
	public void wrongSizeOfInitialValuesIsAnError() {
		List<UnsignedByte> tooShort = new ArrayList<>(sid);
		tooShort.remove(0);
		try {
			new FullFrameHexDumper(tooShort);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Wrong size of sid register list: 24", e.getMessage());
		}

		List<UnsignedByte> tooLong = new ArrayList<>(sid);
		tooLong.add(UnsignedByte.xEE);
		try {
			new FullFrameHexDumper(tooLong);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Wrong size of sid register list: 26", e.getMessage());
		}
	}

	@Test
	public void ifNothingIsWrittenInitialStateIsPrinted() {
		log.playCallStarting();
		assertEquals(
				"CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n" + "",
				log.toString());
	}

	@Test
	public void oneNewRegisterValuePerFrame() {
		log.playCallStarting();
		log.freqLo(OscName.OSC_1).write(UnsignedByte.x01);
		log.playCallStarting();
		log.freqHi(OscName.OSC_1).write(UnsignedByte.x02);
		log.playCallStarting();
		log.pwLo(OscName.OSC_1).write(UnsignedByte.x03);
		log.playCallStarting();
		log.pwHi(OscName.OSC_1).write(UnsignedByte.x04);
		log.playCallStarting();
		log.cr(OscName.OSC_1).write(UnsignedByte.x05);
		log.playCallStarting();
		log.ad(OscName.OSC_1).write(UnsignedByte.x06);
		log.playCallStarting();
		log.sr(OscName.OSC_1).write(UnsignedByte.x07);

		log.playCallStarting();
		log.freqLo(OscName.OSC_2).write(UnsignedByte.x08);
		log.playCallStarting();
		log.freqHi(OscName.OSC_2).write(UnsignedByte.x09);
		log.playCallStarting();
		log.pwLo(OscName.OSC_2).write(UnsignedByte.x0A);
		log.playCallStarting();
		log.pwHi(OscName.OSC_2).write(UnsignedByte.x0B);
		log.playCallStarting();
		log.cr(OscName.OSC_2).write(UnsignedByte.x0C);
		log.playCallStarting();
		log.ad(OscName.OSC_2).write(UnsignedByte.x0D);
		log.playCallStarting();
		log.sr(OscName.OSC_2).write(UnsignedByte.x0E);

		log.playCallStarting();
		log.freqLo(OscName.OSC_3).write(UnsignedByte.x0F);
		log.playCallStarting();
		log.freqHi(OscName.OSC_3).write(UnsignedByte.x10);
		log.playCallStarting();
		log.pwLo(OscName.OSC_3).write(UnsignedByte.x11);
		log.playCallStarting();
		log.pwHi(OscName.OSC_3).write(UnsignedByte.x12);
		log.playCallStarting();
		log.cr(OscName.OSC_3).write(UnsignedByte.x13);
		log.playCallStarting();
		log.ad(OscName.OSC_3).write(UnsignedByte.x14);
		log.playCallStarting();
		log.sr(OscName.OSC_3).write(UnsignedByte.x15);

		log.playCallStarting();
		log.fcLo().write(UnsignedByte.x16);
		log.playCallStarting();
		log.fcHi().write(UnsignedByte.x17);
		log.playCallStarting();
		log.resFilt().write(UnsignedByte.x18);
		log.playCallStarting();
		log.modeVol().write(UnsignedByte.x19);

		// A small ugliness: you need to make one more call to save the effects
		// of the last frame, too:
		log.playCallStarting();

		assertEquals("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "01CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "010203CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "01020304CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "010203040506CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "01020304050607CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "010203040506070809CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090ACCCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0BCCCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0CCCCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0DCCCCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0ECCCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0FCCCCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F10CCCCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F1011CCCCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F101112CCCCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F10111213CCCCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F1011121314CCCCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F101112131415CCCCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F10111213141516CCCCCC\n"
				+ "0102030405060708090A0B0C0D0E0F1011121314151617CCCC\n"
				+ "0102030405060708090A0B0C0D0E0F101112131415161718CC\n"
				+ "0102030405060708090A0B0C0D0E0F10111213141516171819\n" + "",
				log.toString());
	}

}
