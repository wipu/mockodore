package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$00;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$01;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$03;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$04;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$06;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$07;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$08;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$0E;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$0F;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$28;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$5A;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$A9;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$E1;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$E8;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$FE;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$FF;
import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.Test;

public class SidWriteVisualizerTest {

	@Test
	public void intFromLsbMsb() {
		assertEquals(0, SidWriteVisualizer.intFromLsbMsb($00, $00));
		assertEquals(254, SidWriteVisualizer.intFromLsbMsb($FE, $00));
		assertEquals(256, SidWriteVisualizer.intFromLsbMsb($00, $01));
		assertEquals(65535, SidWriteVisualizer.intFromLsbMsb($FF, $FF));
	}

	@Test
	public void log2() {
		assertEquals(0, SidWriteVisualizer.log2(1), 0.01D);
		assertEquals(1, SidWriteVisualizer.log2(2), 0.01D);
		assertEquals(1.58, SidWriteVisualizer.log2(3), 0.01D);
		assertEquals(2, SidWriteVisualizer.log2(4), 0.01D);
		assertEquals(2.58, SidWriteVisualizer.log2(6), 0.01D);
		assertEquals(3, SidWriteVisualizer.log2(8), 0.01D);
		assertEquals(8, SidWriteVisualizer.log2(256), 0.01D);
		assertEquals(15, SidWriteVisualizer.log2(32768), 0.01D);
		assertEquals(16, SidWriteVisualizer.log2(65536), 0.01D);
	}

	private static void xCoordFromFreqFromBytes(int x, int freq,
			UnsignedByte lsb, UnsignedByte msb) {
		assertEquals(x, SidWriteVisualizer.freqAsXCoord(freq));
		assertEquals(freq, SidWriteVisualizer.intFromLsbMsb(lsb, msb));
	}

	@Test
	public void someXCoordsFromFreqsFromBytes() {
		xCoordFromFreqFromBytes(0, 0, $00, $00);
		xCoordFromFreqFromBytes(64, 1, $01, $00);
		xCoordFromFreqFromBytes(512, 256, $00, $01);

		// ekakoe bassdrum:
		xCoordFromFreqFromBytes(679, 1576, $28, $06);
		xCoordFromFreqFromBytes(690, 1768, $E8, $06);
		xCoordFromFreqFromBytes(631, 937, $A9, $03);
		xCoordFromFreqFromBytes(647, 1114, $5A, $04);
		xCoordFromFreqFromBytes(637, 993, $E1, $03);
	}

	@Test
	public void freqAsXCoord() {
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(-1));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(0));
		assertEquals(64, SidWriteVisualizer.freqAsXCoord(1));
		assertEquals(101, SidWriteVisualizer.freqAsXCoord(2));
		assertEquals(148, SidWriteVisualizer.freqAsXCoord(4));
		assertEquals(202, SidWriteVisualizer.freqAsXCoord(8));

		assertEquals(512, SidWriteVisualizer.freqAsXCoord(256));
		assertEquals(553, SidWriteVisualizer.freqAsXCoord(400));

		assertEquals(640, SidWriteVisualizer.freqAsXCoord(1024));

		assertEquals(832, SidWriteVisualizer.freqAsXCoord(8192));
		assertEquals(896, SidWriteVisualizer.freqAsXCoord(16384));
		assertEquals(960, SidWriteVisualizer.freqAsXCoord(32768));
		assertEquals(978, SidWriteVisualizer.freqAsXCoord(40000));

		assertEquals(1023, SidWriteVisualizer.freqAsXCoord(65535));
		assertEquals(1023, SidWriteVisualizer.freqAsXCoord(655350));
	}

	@Test
	public void signedPw() {
		assertEquals(-1.0D, SidWriteVisualizer.signedPw($00, $00), 0.01D);
		assertEquals(-0.88D, SidWriteVisualizer.signedPw($FE, $00), 0.01D);
		assertEquals(-0.13D, SidWriteVisualizer.signedPw($00, $07), 0.01D);
		assertEquals(0.0D, SidWriteVisualizer.signedPw($00, $08), 0.01D);
		assertEquals(0.75D, SidWriteVisualizer.signedPw($00, $0E), 0.01D);
		assertEquals(1.0D, SidWriteVisualizer.signedPw($FF, $0F), 0.01D);
	}

}
