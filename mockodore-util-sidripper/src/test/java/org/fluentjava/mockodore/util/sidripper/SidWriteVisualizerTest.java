package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x00;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x01;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x03;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x04;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x06;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x07;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x08;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x0E;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x0F;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x28;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x5A;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.xA9;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.xE1;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.xE8;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.xFE;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.xFF;
import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.Test;

public class SidWriteVisualizerTest {

	@Test
	public void intFromLsbMsb() {
		assertEquals(0, SidWriteVisualizer.intFromLsbMsb(x00, x00));
		assertEquals(254, SidWriteVisualizer.intFromLsbMsb(xFE, x00));
		assertEquals(256, SidWriteVisualizer.intFromLsbMsb(x00, x01));
		assertEquals(65535, SidWriteVisualizer.intFromLsbMsb(xFF, xFF));
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
		xCoordFromFreqFromBytes(0, 0, x00, x00);
		xCoordFromFreqFromBytes(64, 1, x01, x00);
		xCoordFromFreqFromBytes(512, 256, x00, x01);

		// ekakoe bassdrum:
		xCoordFromFreqFromBytes(679, 1576, x28, x06);
		xCoordFromFreqFromBytes(690, 1768, xE8, x06);
		xCoordFromFreqFromBytes(631, 937, xA9, x03);
		xCoordFromFreqFromBytes(647, 1114, x5A, x04);
		xCoordFromFreqFromBytes(637, 993, xE1, x03);
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
		assertEquals(-1.0D, SidWriteVisualizer.signedPw(x00, x00), 0.01D);
		assertEquals(-0.88D, SidWriteVisualizer.signedPw(xFE, x00), 0.01D);
		assertEquals(-0.13D, SidWriteVisualizer.signedPw(x00, x07), 0.01D);
		assertEquals(0.0D, SidWriteVisualizer.signedPw(x00, x08), 0.01D);
		assertEquals(0.75D, SidWriteVisualizer.signedPw(x00, x0E), 0.01D);
		assertEquals(1.0D, SidWriteVisualizer.signedPw(xFF, x0F), 0.01D);
	}

}
