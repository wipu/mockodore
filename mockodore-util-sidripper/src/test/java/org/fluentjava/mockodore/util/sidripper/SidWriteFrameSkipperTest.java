package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.Test;

public class SidWriteFrameSkipperTest {

	private static void writeAllRegs(SidWriteFrameSkipper sid, UnsignedByte v) {
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			sid.reg(reg).write(v);
		}
	}

	@Test
	public void noSkip() {
		SidWriteCompactLogger d = new SidWriteCompactLogger();
		SidWriteFrameSkipper skipper = new SidWriteFrameSkipper(d, 0);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x01);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x02);

		assertEquals("\n" + "00=01\n" + "01=01\n" + "02=01\n" + "03=01\n"
				+ "04=01\n" + "05=01\n" + "06=01\n" + "07=01\n" + "08=01\n"
				+ "09=01\n" + "0A=01\n" + "0B=01\n" + "0C=01\n" + "0D=01\n"
				+ "0E=01\n" + "0F=01\n" + "10=01\n" + "11=01\n" + "12=01\n"
				+ "13=01\n" + "14=01\n" + "15=01\n" + "16=01\n" + "17=01\n"
				+ "18=01\n" + "\n" + "00=02\n" + "01=02\n" + "02=02\n"
				+ "03=02\n" + "04=02\n" + "05=02\n" + "06=02\n" + "07=02\n"
				+ "08=02\n" + "09=02\n" + "0A=02\n" + "0B=02\n" + "0C=02\n"
				+ "0D=02\n" + "0E=02\n" + "0F=02\n" + "10=02\n" + "11=02\n"
				+ "12=02\n" + "13=02\n" + "14=02\n" + "15=02\n" + "16=02\n"
				+ "17=02\n" + "18=02\n" + "", d.toString());
	}

	@Test
	public void skipOne() {
		SidWriteCompactLogger d = new SidWriteCompactLogger();
		SidWriteFrameSkipper skipper = new SidWriteFrameSkipper(d, 1);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x01);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x02);

		// just one frame, including initial write plus actual first write:
		assertEquals("\n" + "00=01\n" + "01=01\n" + "02=01\n" + "03=01\n"
				+ "04=01\n" + "05=01\n" + "06=01\n" + "07=01\n" + "08=01\n"
				+ "09=01\n" + "0A=01\n" + "0B=01\n" + "0C=01\n" + "0D=01\n"
				+ "0E=01\n" + "0F=01\n" + "10=01\n" + "11=01\n" + "12=01\n"
				+ "13=01\n" + "14=01\n" + "15=01\n" + "16=01\n" + "17=01\n"
				+ "18=01\n" + "00=02\n" + "01=02\n" + "02=02\n" + "03=02\n"
				+ "04=02\n" + "05=02\n" + "06=02\n" + "07=02\n" + "08=02\n"
				+ "09=02\n" + "0A=02\n" + "0B=02\n" + "0C=02\n" + "0D=02\n"
				+ "0E=02\n" + "0F=02\n" + "10=02\n" + "11=02\n" + "12=02\n"
				+ "13=02\n" + "14=02\n" + "15=02\n" + "16=02\n" + "17=02\n"
				+ "18=02\n" + "", d.toString());
	}

	@Test
	public void skipTwo() {
		SidWriteCompactLogger d = new SidWriteCompactLogger();
		SidWriteFrameSkipper skipper = new SidWriteFrameSkipper(d, 2);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x01);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x02);
		skipper.playCallStarting();
		writeAllRegs(skipper, UnsignedByte.x03);

		// first frame includes initial value plus actual frame, second is a
		// normal frame:
		assertEquals("\n" + "00=02\n" + "01=02\n" + "02=02\n" + "03=02\n"
				+ "04=02\n" + "05=02\n" + "06=02\n" + "07=02\n" + "08=02\n"
				+ "09=02\n" + "0A=02\n" + "0B=02\n" + "0C=02\n" + "0D=02\n"
				+ "0E=02\n" + "0F=02\n" + "10=02\n" + "11=02\n" + "12=02\n"
				+ "13=02\n" + "14=02\n" + "15=02\n" + "16=02\n" + "17=02\n"
				+ "18=02\n" + "00=03\n" + "01=03\n" + "02=03\n" + "03=03\n"
				+ "04=03\n" + "05=03\n" + "06=03\n" + "07=03\n" + "08=03\n"
				+ "09=03\n" + "0A=03\n" + "0B=03\n" + "0C=03\n" + "0D=03\n"
				+ "0E=03\n" + "0F=03\n" + "10=03\n" + "11=03\n" + "12=03\n"
				+ "13=03\n" + "14=03\n" + "15=03\n" + "16=03\n" + "17=03\n"
				+ "18=03\n" + "", d.toString());
	}

}
