package org.fluentjava.mockodore.util.sidripper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidWritePrettyLoggerTest {

	private ConvenientSidWriteListener log;

	@BeforeEach
	public void before() {
		log = new ConvenientSidWriteListener(new SidWritePrettyLogger());
	}

	@Test
	public void crWithDifferentCombinations() {
		log.cr1().write(UnsignedByte.x09);
		log.cr1().write(UnsignedByte.x11);
		log.cr1().write(UnsignedByte.x41);
		log.cr1().write(UnsignedByte.x81);
		log.cr1().write(UnsignedByte.x83);
		log.cr1().write(UnsignedByte.x85);
		log.cr1().write(UnsignedByte.xFF);

		assertEquals(" 1   | CR1=||||TEST|||GATE ($D404=#$09)\n"
				+ " 1   | CR1=|||TRI||||GATE ($D404=#$11)\n"
				+ " 1   | CR1=|PULSE||||||GATE ($D404=#$41)\n"
				+ " 1   | CR1=NOISE|||||||GATE ($D404=#$81)\n"
				+ " 1   | CR1=NOISE||||||SYNC|GATE ($D404=#$83)\n"
				+ " 1   | CR1=NOISE|||||RING||GATE ($D404=#$85)\n"
				+ " 1   | CR1=NOISE|PULSE|SAW|TRI|TEST|RING|SYNC|GATE ($D404=#$FF)\n"
				+ "", log.toString());
	}

	@Test
	public void cr2IsIndented() {
		log.cr2().write(UnsignedByte.x11);

		assertEquals("  2  | CR2=|||TRI||||GATE ($D40B=#$11)\n" + "",
				log.toString());
	}

	@Test
	public void cr3IsIndented() {
		log.cr3().write(UnsignedByte.x11);

		assertEquals("   3 | CR3=|||TRI||||GATE ($D412=#$11)\n" + "",
				log.toString());
	}

	@Test
	public void freqLo() {
		log.freqLo1().write(UnsignedByte.x11);
		log.freqLo2().write(UnsignedByte.x22);
		log.freqLo3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | FreqLo1=#$11 ($D400=#$11)\n"
						+ "  2  | FreqLo2=#$22 ($D407=#$22)\n"
						+ "   3 | FreqLo3=#$33 ($D40E=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void freqHi() {
		log.freqHi1().write(UnsignedByte.x11);
		log.freqHi2().write(UnsignedByte.x22);
		log.freqHi3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | FreqHi1=#$11 ($D401=#$11)\n"
						+ "  2  | FreqHi2=#$22 ($D408=#$22)\n"
						+ "   3 | FreqHi3=#$33 ($D40F=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void pwLo() {
		log.pwLo1().write(UnsignedByte.x11);
		log.pwLo2().write(UnsignedByte.x22);
		log.pwLo3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | PwLo1=#$11 ($D402=#$11)\n"
						+ "  2  | PwLo2=#$22 ($D409=#$22)\n"
						+ "   3 | PwLo3=#$33 ($D410=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void pwHi() {
		log.pwHi1().write(UnsignedByte.x11);
		log.pwHi2().write(UnsignedByte.x22);
		log.pwHi3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | PwHi1=#$11 ($D403=#$11)\n"
						+ "  2  | PwHi2=#$22 ($D40A=#$22)\n"
						+ "   3 | PwHi3=#$33 ($D411=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void ad() {
		log.ad1().write(UnsignedByte.x11);
		log.ad2().write(UnsignedByte.x22);
		log.ad3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | AD1=01 01 ($D405=#$11)\n"
						+ "  2  | AD2=02 02 ($D40C=#$22)\n"
						+ "   3 | AD3=03 03 ($D413=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void sr() {
		log.sr1().write(UnsignedByte.x11);
		log.sr2().write(UnsignedByte.x22);
		log.sr3().write(UnsignedByte.x33);

		assertEquals(
				" 1   | SR1=01 01 ($D406=#$11)\n"
						+ "  2  | SR2=02 02 ($D40D=#$22)\n"
						+ "   3 | SR3=03 03 ($D414=#$33)\n" + "",
				log.toString());
	}

	@Test
	public void fcLoAndHi() {
		log.fcLo().write(UnsignedByte.x11);
		log.fcHi().write(UnsignedByte.x22);

		assertEquals(
				"     | FcLo=#$11 ($D415=#$11)\n"
						+ "     | FcHi=#$22 ($D416=#$22)\n" + "",
				log.toString());
	}

	@Test
	public void modeVolVolume() {
		log.modeVol().write(UnsignedByte.x00);
		log.modeVol().write(UnsignedByte.x01);
		log.modeVol().write(UnsignedByte.x0E);
		log.modeVol().write(UnsignedByte.x0F);

		assertEquals(
				"     | ModeVol=||||vol=#$00 ($D418=#$00)\n"
						+ "     | ModeVol=||||vol=#$01 ($D418=#$01)\n"
						+ "     | ModeVol=||||vol=#$0E ($D418=#$0E)\n"
						+ "     | ModeVol=||||vol=#$0F ($D418=#$0F)\n" + "",
				log.toString());
	}

	@Test
	public void modeVolBitField() {
		log.modeVol().write(UnsignedByte.x0F);
		log.modeVol().write(UnsignedByte.x1F);
		log.modeVol().write(UnsignedByte.x2F);
		log.modeVol().write(UnsignedByte.x4F);
		log.modeVol().write(UnsignedByte.x8F);
		log.modeVol().write(UnsignedByte.xFF);

		assertEquals("     | ModeVol=||||vol=#$0F ($D418=#$0F)\n"
				+ "     | ModeVol=|||LOWPASS|vol=#$0F ($D418=#$1F)\n"
				+ "     | ModeVol=||BANDPASS||vol=#$0F ($D418=#$2F)\n"
				+ "     | ModeVol=|HIGHPASS|||vol=#$0F ($D418=#$4F)\n"
				+ "     | ModeVol=CHAN_3_OFF||||vol=#$0F ($D418=#$8F)\n"
				+ "     | ModeVol=CHAN_3_OFF|HIGHPASS|BANDPASS|LOWPASS|vol=#$0F ($D418=#$FF)\n"
				+ "", log.toString());
	}

	@Test
	public void resFiltResonance() {
		log.resFilt().write(UnsignedByte.x00);
		log.resFilt().write(UnsignedByte.x10);
		log.resFilt().write(UnsignedByte.xE0);
		log.resFilt().write(UnsignedByte.xF0);

		assertEquals(
				"     | ResFilt=res=#$00|||| ($D417=#$00)\n"
						+ "     | ResFilt=res=#$01|||| ($D417=#$10)\n"
						+ "     | ResFilt=res=#$0E|||| ($D417=#$E0)\n"
						+ "     | ResFilt=res=#$0F|||| ($D417=#$F0)\n" + "",
				log.toString());
	}

	@Test
	public void resFiltBits() {
		log.resFilt().write(UnsignedByte.x00);
		log.resFilt().write(UnsignedByte.x01);
		log.resFilt().write(UnsignedByte.x02);
		log.resFilt().write(UnsignedByte.x04);
		log.resFilt().write(UnsignedByte.x08);

		assertEquals("     | ResFilt=res=#$00|||| ($D417=#$00)\n"
				+ "     | ResFilt=res=#$00||||FILT_1 ($D417=#$01)\n"
				+ "     | ResFilt=res=#$00|||FILT_2| ($D417=#$02)\n"
				+ "     | ResFilt=res=#$00||FILT_3|| ($D417=#$04)\n"
				+ "     | ResFilt=res=#$00|FILT_EX||| ($D417=#$08)\n" + "",
				log.toString());
	}

	@Test
	public void playCallStarts() {
		log.ad1().write(UnsignedByte.x01);
		log.playCallStarting();
		log.ad1().write(UnsignedByte.x02);
		log.playCallStarting();
		log.ad1().write(UnsignedByte.x03);

		assertEquals(
				" 1   | AD1=00 01 ($D405=#$01)\n" + "=== Frame 0\n"
						+ " 1   | AD1=00 02 ($D405=#$02)\n" + "=== Frame 1\n"
						+ " 1   | AD1=00 03 ($D405=#$03)\n" + "",
				log.toString());
	}

}
