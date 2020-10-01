package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.junit.Before;
import org.junit.Test;

public class SidWriteCompactLoggerTest {

	private SidWriteCompactLogger log;

	@Before
	public void before() {
		log = new SidWriteCompactLogger();
	}

	@Test
	public void crWrites() {
		log.cr(OscName.OSC_1).write(UnsignedByte.x09);
		log.cr(OscName.OSC_2).write(UnsignedByte.x11);
		log.cr(OscName.OSC_3).write(UnsignedByte.x23);

		assertEquals("04=09\n" + "0B=11\n" + "12=23\n" + "", log.toString());
	}

	@Test
	public void freqWrites() {
		log.freqLo(OscName.OSC_1).write(UnsignedByte.x12);
		log.freqHi(OscName.OSC_1).write(UnsignedByte.x34);
		log.freqLo(OscName.OSC_2).write(UnsignedByte.x56);
		log.freqHi(OscName.OSC_2).write(UnsignedByte.x78);

		assertEquals("00=12\n" + "01=34\n" + "07=56\n" + "08=78\n" + "",
				log.toString());
	}

	@Test
	public void pwWrites() {
		log.pwLo(OscName.OSC_1).write(UnsignedByte.x12);
		log.pwHi(OscName.OSC_1).write(UnsignedByte.x34);
		log.pwLo(OscName.OSC_2).write(UnsignedByte.x56);
		log.pwHi(OscName.OSC_2).write(UnsignedByte.x78);

		assertEquals("02=12\n" + "03=34\n" + "09=56\n" + "0A=78\n" + "",
				log.toString());
	}

	@Test
	public void adWrites() {
		log.ad(OscName.OSC_1).write(UnsignedByte.x12);
		log.ad(OscName.OSC_1).write(UnsignedByte.x34);
		log.ad(OscName.OSC_2).write(UnsignedByte.x56);
		log.ad(OscName.OSC_2).write(UnsignedByte.x78);

		assertEquals("05=12\n" + "05=34\n" + "0C=56\n" + "0C=78\n" + "",
				log.toString());
	}

	@Test
	public void srWrites() {
		log.sr(OscName.OSC_1).write(UnsignedByte.x12);
		log.sr(OscName.OSC_1).write(UnsignedByte.x34);
		log.sr(OscName.OSC_2).write(UnsignedByte.x56);
		log.sr(OscName.OSC_2).write(UnsignedByte.x78);

		assertEquals("06=12\n" + "06=34\n" + "0D=56\n" + "0D=78\n" + "",
				log.toString());
	}

	@Test
	public void globals() {
		log.fcLo().write(UnsignedByte.x12);
		log.fcHi().write(UnsignedByte.x34);
		log.modeVol().write(UnsignedByte.x56);
		log.resFilt().write(UnsignedByte.x78);

		assertEquals("15=12\n" + "16=34\n" + "18=56\n" + "17=78\n" + "",
				log.toString());
	}

	@Test
	public void playCallStarts() {
		log.ad(OscName.OSC_1).write(UnsignedByte.x01);
		log.playCallStarting();
		log.ad(OscName.OSC_1).write(UnsignedByte.x02);
		log.playCallStarting();
		log.ad(OscName.OSC_1).write(UnsignedByte.x03);

		assertEquals("05=01\n" + "\n" + "05=02\n" + "\n" + "05=03\n" + "",
				log.toString());
	}

	@Test
	public void customRegWriteSeparator() {
		log = new SidWriteCompactLogger(" ");

		log.ad(OscName.OSC_1).write(UnsignedByte.x01);
		log.cr(OscName.OSC_1).write(UnsignedByte.x10);
		log.playCallStarting();
		log.ad(OscName.OSC_1).write(UnsignedByte.x02);
		log.cr(OscName.OSC_1).write(UnsignedByte.x20);
		log.playCallStarting();
		log.ad(OscName.OSC_1).write(UnsignedByte.x03);
		log.cr(OscName.OSC_1).write(UnsignedByte.x30);

		assertEquals("05=01 04=10 \n" + "05=02 04=20 \n" + "05=03 04=30 ",
				log.toString());
	}

}
