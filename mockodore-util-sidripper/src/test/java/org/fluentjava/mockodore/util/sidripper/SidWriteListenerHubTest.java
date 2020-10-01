package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.junit.Before;
import org.junit.Test;

public class SidWriteListenerHubTest {

	private SidWriteCompactLogger d1;
	private SidWriteCompactLogger d2;
	private SidWriteListener hub;

	@Before
	public void before() {
		d1 = new SidWriteCompactLogger(" ");
		d2 = new SidWriteCompactLogger(" ");
		hub = SidWriteListenerHub.delegatingTo(d1, d2);
	}

	@Test
	public void allCallsAreDelegatedToAllDelegates() {
		hub.ad(OscName.OSC_1).write(UnsignedByte.x01);
		hub.playCallStarting();
		hub.cr(OscName.OSC_1).write(UnsignedByte.x02);
		hub.fcHi().write(UnsignedByte.x03);
		hub.fcLo().write(UnsignedByte.x04);
		hub.freqHi(OscName.OSC_1).write(UnsignedByte.x05);
		hub.freqLo(OscName.OSC_1).write(UnsignedByte.x06);
		hub.modeVol().write(UnsignedByte.x07);
		hub.pwHi(OscName.OSC_1).write(UnsignedByte.x08);
		hub.pwLo(OscName.OSC_1).write(UnsignedByte.x09);
		hub.resFilt().write(UnsignedByte.x0A);
		hub.sr(OscName.OSC_1).write(UnsignedByte.x0B);

		assertEquals("05=01 \n"
				+ "04=02 16=03 15=04 01=05 00=06 18=07 03=08 02=09 17=0A 06=0B ",
				d1.toString());
		assertEquals(d1.toString(), d2.toString());
	}

}
