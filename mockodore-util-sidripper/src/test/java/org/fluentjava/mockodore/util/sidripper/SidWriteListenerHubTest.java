package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCHI;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCLO;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.MODE_VOL;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.RES_FILT;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidWriteListenerHubTest {

	private SidWriteCompactLogger d1;
	private SidWriteCompactLogger d2;
	private SidWriteListener hub;

	@BeforeEach
	public void before() {
		d1 = new SidWriteCompactLogger(" ");
		d2 = new SidWriteCompactLogger(" ");
		hub = SidWriteListenerHub.delegatingTo(d1, d2);
	}

	@Test
	public void allCallsAreDelegatedToAllDelegates() {
		hub.reg(AD_1).write(UnsignedByte.x01);
		hub.playCallStarting();
		hub.reg(CR_1).write(UnsignedByte.x02);
		hub.reg(FCHI).write(UnsignedByte.x03);
		hub.reg(FCLO).write(UnsignedByte.x04);
		hub.reg(FREQ_HI_1).write(UnsignedByte.x05);
		hub.reg(FREQ_LO_1).write(UnsignedByte.x06);
		hub.reg(MODE_VOL).write(UnsignedByte.x07);
		hub.reg(PW_HI_1).write(UnsignedByte.x08);
		hub.reg(PW_LO_1).write(UnsignedByte.x09);
		hub.reg(RES_FILT).write(UnsignedByte.x0A);
		hub.reg(SR_1).write(UnsignedByte.x0B);

		assertEquals("05=01 \n"
				+ "04=02 16=03 15=04 01=05 00=06 18=07 03=08 02=09 17=0A 06=0B ",
				d1.toString());
		assertEquals(d1.toString(), d2.toString());
	}

}
