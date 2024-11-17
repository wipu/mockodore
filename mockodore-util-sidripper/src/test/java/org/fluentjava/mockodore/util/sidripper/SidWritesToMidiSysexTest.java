package org.fluentjava.mockodore.util.sidripper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidWritesToMidiSysexTest {

	private SidWritesToMidiSysex toMidi;
	private ConvenientSidWriteListener log;

	@BeforeEach
	public void before() {
		toMidi = new SidWritesToMidiSysex();
		log = new ConvenientSidWriteListener(toMidi);
	}

	@Test
	public void frameWithDefaults() {
		log.playCallStarting();

		assertEquals("(seq\n" + " division:PPQ\n" + " resolution:96\n"
				+ "  (track\n"
				+ "    (ev @4 (sysex 44 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F7))\n"
				+ "    (ev @8 (sysex 44 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F7))\n"
				+ "    (ev @8 (end-of-track))\n" + "  ) # track\n" + ") # seq\n"
				+ "", toMidi.toMidiseq().toString());
	}

	@Test
	public void frameWithOverwrittenValue() {
		log.freqLo1().write(UnsignedByte.x01);

		assertEquals("(seq\n" + " division:PPQ\n" + " resolution:96\n"
				+ "  (track\n"
				+ "    (ev @4 (sysex 44 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F7))\n"
				+ "    (ev @4 (end-of-track))\n" + "  ) # track\n" + ") # seq\n"
				+ "", toMidi.toMidiseq().toString());
	}

	@Test
	public void overwritesAreRememberedInNextFrameToo() {
		log.freqLo1().write(UnsignedByte.x01);
		log.playCallStarting();
		log.freqHi1().write(UnsignedByte.x02);

		assertEquals("(seq\n" + " division:PPQ\n" + " resolution:96\n"
				+ "  (track\n"
				+ "    (ev @4 (sysex 44 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F7))\n"
				+ "    (ev @8 (sysex 44 00 00 02 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F7))\n"
				+ "    (ev @8 (end-of-track))\n" + "  ) # track\n" + ") # seq\n"
				+ "", toMidi.toMidiseq().toString());
	}

	@Test
	public void frameWithOverwrittenValuesToAllRegisters() {
		log.freqLo1().write(UnsignedByte.x01);
		log.freqHi1().write(UnsignedByte.x02);
		log.pwLo1().write(UnsignedByte.x03);
		log.pwHi1().write(UnsignedByte.x04);
		log.cr1().write(UnsignedByte.x05);
		log.ad1().write(UnsignedByte.x06);
		log.sr1().write(UnsignedByte.x07);

		log.freqLo2().write(UnsignedByte.x08);
		log.freqHi2().write(UnsignedByte.x09);
		log.pwLo2().write(UnsignedByte.x0A);
		log.pwHi2().write(UnsignedByte.x0B);
		log.cr2().write(UnsignedByte.x0C);
		log.ad2().write(UnsignedByte.x0D);
		log.sr2().write(UnsignedByte.x0E);

		log.freqLo3().write(UnsignedByte.x0F);
		log.freqHi3().write(UnsignedByte.x10);
		log.pwLo3().write(UnsignedByte.x11);
		log.pwHi3().write(UnsignedByte.x12);
		log.cr3().write(UnsignedByte.x13);
		log.ad3().write(UnsignedByte.x14);
		log.sr3().write(UnsignedByte.x15);

		log.fcLo().write(UnsignedByte.x16);
		log.fcHi().write(UnsignedByte.x17);
		log.resFilt().write(UnsignedByte.x18);
		log.modeVol().write(UnsignedByte.x19);

		assertEquals("(seq\n" + " division:PPQ\n" + " resolution:96\n"
				+ "  (track\n"
				+ "    (ev @4 (sysex 44 06 07 02 01 04 03 05 00 0D 0E 09 08 0B 0A 0C 00 14 15 10 0F 12 11 13 00 17 16 19 18 00 00 00 00 F7))\n"
				+ "    (ev @4 (end-of-track))\n" + "  ) # track\n" + ") # seq\n"
				+ "", toMidi.toMidiseq().toString());
	}

}
