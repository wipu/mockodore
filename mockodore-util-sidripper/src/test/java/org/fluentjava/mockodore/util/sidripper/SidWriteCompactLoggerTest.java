package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCHI;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCLO;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.MODE_VOL;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.RES_FILT;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidWriteCompactLoggerTest {

	private SidWriteCompactLogger log;

	@BeforeEach
	public void before() {
		log = new SidWriteCompactLogger();
	}

	@Test
	public void crWrites() {
		log.reg(CR_1).write(UnsignedByte.x09);
		log.reg(CR_2).write(UnsignedByte.x11);
		log.reg(CR_3).write(UnsignedByte.x23);

		assertEquals("04=09\n" + "0B=11\n" + "12=23\n" + "", log.toString());
	}

	@Test
	public void freqWrites() {
		log.reg(FREQ_LO_1).write(UnsignedByte.x12);
		log.reg(FREQ_HI_1).write(UnsignedByte.x34);
		log.reg(FREQ_LO_2).write(UnsignedByte.x56);
		log.reg(FREQ_HI_2).write(UnsignedByte.x78);

		assertEquals("00=12\n" + "01=34\n" + "07=56\n" + "08=78\n" + "",
				log.toString());
	}

	@Test
	public void pwWrites() {
		log.reg(PW_LO_1).write(UnsignedByte.x12);
		log.reg(PW_HI_1).write(UnsignedByte.x34);
		log.reg(PW_LO_2).write(UnsignedByte.x56);
		log.reg(PW_HI_2).write(UnsignedByte.x78);

		assertEquals("02=12\n" + "03=34\n" + "09=56\n" + "0A=78\n" + "",
				log.toString());
	}

	@Test
	public void adWrites() {
		log.reg(AD_1).write(UnsignedByte.x12);
		log.reg(AD_1).write(UnsignedByte.x34);
		log.reg(AD_2).write(UnsignedByte.x56);
		log.reg(AD_2).write(UnsignedByte.x78);

		assertEquals("05=12\n" + "05=34\n" + "0C=56\n" + "0C=78\n" + "",
				log.toString());
	}

	@Test
	public void srWrites() {
		log.reg(SR_1).write(UnsignedByte.x12);
		log.reg(SR_1).write(UnsignedByte.x34);
		log.reg(SR_2).write(UnsignedByte.x56);
		log.reg(SR_2).write(UnsignedByte.x78);

		assertEquals("06=12\n" + "06=34\n" + "0D=56\n" + "0D=78\n" + "",
				log.toString());
	}

	@Test
	public void globals() {
		log.reg(FCLO).write(UnsignedByte.x12);
		log.reg(FCHI).write(UnsignedByte.x34);
		log.reg(MODE_VOL).write(UnsignedByte.x56);
		log.reg(RES_FILT).write(UnsignedByte.x78);

		assertEquals("15=12\n" + "16=34\n" + "18=56\n" + "17=78\n" + "",
				log.toString());
	}

	@Test
	public void playCallStarts() {
		log.reg(AD_1).write(UnsignedByte.x01);
		log.playCallStarting();
		log.reg(AD_1).write(UnsignedByte.x02);
		log.playCallStarting();
		log.reg(AD_1).write(UnsignedByte.x03);

		assertEquals("05=01\n" + "\n" + "05=02\n" + "\n" + "05=03\n" + "",
				log.toString());
	}

	@Test
	public void customRegWriteSeparator() {
		log = new SidWriteCompactLogger(" ");

		log.reg(AD_1).write(UnsignedByte.x01);
		log.reg(CR_1).write(UnsignedByte.x10);
		log.playCallStarting();
		log.reg(AD_1).write(UnsignedByte.x02);
		log.reg(CR_1).write(UnsignedByte.x20);
		log.playCallStarting();
		log.reg(AD_1).write(UnsignedByte.x03);
		log.reg(CR_1).write(UnsignedByte.x30);

		assertEquals("05=01 04=10 \n" + "05=02 04=20 \n" + "05=03 04=30 ",
				log.toString());
	}

}
