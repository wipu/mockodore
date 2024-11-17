package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCHI;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCLO;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.MODE_VOL;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.RES_FILT;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FullFrameHexDumperTest {

	private List<UnsignedByte> sid;
	private FullFrameHexDumper log;

	@BeforeEach
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
		log.reg(FREQ_LO_1).write(UnsignedByte.x01);
		log.playCallStarting();
		log.reg(FREQ_HI_1).write(UnsignedByte.x02);
		log.playCallStarting();
		log.reg(PW_LO_1).write(UnsignedByte.x03);
		log.playCallStarting();
		log.reg(PW_HI_1).write(UnsignedByte.x04);
		log.playCallStarting();
		log.reg(CR_1).write(UnsignedByte.x05);
		log.playCallStarting();
		log.reg(AD_1).write(UnsignedByte.x06);
		log.playCallStarting();
		log.reg(SR_1).write(UnsignedByte.x07);

		log.playCallStarting();
		log.reg(FREQ_LO_2).write(UnsignedByte.x08);
		log.playCallStarting();
		log.reg(FREQ_HI_2).write(UnsignedByte.x09);
		log.playCallStarting();
		log.reg(PW_LO_2).write(UnsignedByte.x0A);
		log.playCallStarting();
		log.reg(PW_HI_2).write(UnsignedByte.x0B);
		log.playCallStarting();
		log.reg(CR_2).write(UnsignedByte.x0C);
		log.playCallStarting();
		log.reg(AD_2).write(UnsignedByte.x0D);
		log.playCallStarting();
		log.reg(SR_2).write(UnsignedByte.x0E);

		log.playCallStarting();
		log.reg(FREQ_LO_3).write(UnsignedByte.x0F);
		log.playCallStarting();
		log.reg(FREQ_HI_3).write(UnsignedByte.x10);
		log.playCallStarting();
		log.reg(PW_LO_3).write(UnsignedByte.x11);
		log.playCallStarting();
		log.reg(PW_HI_3).write(UnsignedByte.x12);
		log.playCallStarting();
		log.reg(CR_3).write(UnsignedByte.x13);
		log.playCallStarting();
		log.reg(AD_3).write(UnsignedByte.x14);
		log.playCallStarting();
		log.reg(SR_3).write(UnsignedByte.x15);

		log.playCallStarting();
		log.reg(FCLO).write(UnsignedByte.x16);
		log.playCallStarting();
		log.reg(FCHI).write(UnsignedByte.x17);
		log.playCallStarting();
		log.reg(RES_FILT).write(UnsignedByte.x18);
		log.playCallStarting();
		log.reg(MODE_VOL).write(UnsignedByte.x19);

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
