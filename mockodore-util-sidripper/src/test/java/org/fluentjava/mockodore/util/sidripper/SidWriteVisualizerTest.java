package org.fluentjava.mockodore.util.sidripper;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x00;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x01;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.x02;
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
import org.fluentjava.mockodore.model.sid.Codebase64PalNote;
import org.fluentjava.mockodore.model.sid.Codebase64PalNoteFreqs;
import org.junit.Test;

public class SidWriteVisualizerTest {

	@Test
	public void intFromLsbMsb() {
		assertEquals(0, SidWriteVisualizer.intFromLsbMsb(x00, x00));
		assertEquals(254, SidWriteVisualizer.intFromLsbMsb(xFE, x00));
		assertEquals(256, SidWriteVisualizer.intFromLsbMsb(x00, x01));
		assertEquals(257, SidWriteVisualizer.intFromLsbMsb(x01, x01));
		assertEquals(512, SidWriteVisualizer.intFromLsbMsb(x00, x02));
		assertEquals(513, SidWriteVisualizer.intFromLsbMsb(x01, x02));
		assertEquals(1027, SidWriteVisualizer.intFromLsbMsb(x03, x04));
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
		xCoordFromFreqFromBytes(0, 1, x01, x00);
		xCoordFromFreqFromBytes(0, 256, x00, x01);

		// ekakoe bassdrum:
		xCoordFromFreqFromBytes(324, 1576, x28, x06);
		xCoordFromFreqFromBytes(346, 1768, xE8, x06);
		xCoordFromFreqFromBytes(227, 937, xA9, x03);
		xCoordFromFreqFromBytes(259, 1114, x5A, x04);
		xCoordFromFreqFromBytes(238, 993, xE1, x03);
	}

	@Test
	public void freqAsXCoord() {
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(-1));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(0));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(1));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(2));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(4));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(8));

		assertEquals(0, SidWriteVisualizer.freqAsXCoord(256));
		assertEquals(25, SidWriteVisualizer.freqAsXCoord(320));
		assertEquals(59, SidWriteVisualizer.freqAsXCoord(384));
		assertEquals(67, SidWriteVisualizer.freqAsXCoord(400));

		assertEquals(243, SidWriteVisualizer.freqAsXCoord(1024));

		assertEquals(633, SidWriteVisualizer.freqAsXCoord(8192));
		assertEquals(763, SidWriteVisualizer.freqAsXCoord(16384));
		assertEquals(893, SidWriteVisualizer.freqAsXCoord(32768));
		assertEquals(931, SidWriteVisualizer.freqAsXCoord(40000));

		assertEquals(1023, SidWriteVisualizer.freqAsXCoord(65535));
		assertEquals(1023, SidWriteVisualizer.freqAsXCoord(655350));
	}

	@Test
	public void knownNoteFreqsAsXCoords() {
		Codebase64PalNote c1 = Codebase64PalNote.C__1;
		Codebase64PalNote c2 = Codebase64PalNote.C__2;
		Codebase64PalNote c3 = Codebase64PalNote.C__3;
		Codebase64PalNote c4 = Codebase64PalNote.C__4;
		Codebase64PalNote c5 = Codebase64PalNote.C__5;
		Codebase64PalNote c6 = Codebase64PalNote.C__6;
		Codebase64PalNote c7 = Codebase64PalNote.C__7;
		Codebase64PalNote c8 = Codebase64PalNote.C__8;
		Codebase64PalNote highest = Codebase64PalNote.B__8;

		Codebase64PalNoteFreqs f = new Codebase64PalNoteFreqs();

		// just checking what the lo-hi values are:
		assertEquals("#$17 #$01", f.lo(c1) + " " + f.hi(c1));
		assertEquals("#$2D #$02", f.lo(c2) + " " + f.hi(c2));
		assertEquals("#$5A #$04", f.lo(c3) + " " + f.hi(c3));
		assertEquals("#$B4 #$08", f.lo(c4) + " " + f.hi(c4));
		assertEquals("#$68 #$11", f.lo(c5) + " " + f.hi(c5));
		assertEquals("#$FF #$FF", f.lo(highest) + " " + f.hi(highest));

		// and ints:
		assertEquals(0, SidWriteVisualizer.intFromLsbMsb(x00, x00));
		assertEquals(279, SidWriteVisualizer.intFromLsbMsb(f.lo(c1), f.hi(c1)));
		assertEquals(557, SidWriteVisualizer.intFromLsbMsb(f.lo(c2), f.hi(c2)));
		assertEquals(1114,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c3), f.hi(c3)));
		assertEquals(2228,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c4), f.hi(c4)));
		assertEquals(4456,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c5), f.hi(c5)));
		assertEquals(8912,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c6), f.hi(c6)));
		assertEquals(17825,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c7), f.hi(c7)));
		assertEquals(35650,
				SidWriteVisualizer.intFromLsbMsb(f.lo(c8), f.hi(c8)));
		assertEquals(65535,
				SidWriteVisualizer.intFromLsbMsb(f.lo(highest), f.hi(highest)));

		// then the actual assertions:
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(x00, x00));
		assertEquals(0, SidWriteVisualizer.freqAsXCoord(f.lo(c1), f.hi(c1)));
		assertEquals(129, SidWriteVisualizer.freqAsXCoord(f.lo(c2), f.hi(c2)));
		assertEquals(259, SidWriteVisualizer.freqAsXCoord(f.lo(c3), f.hi(c3)));
		assertEquals(389, SidWriteVisualizer.freqAsXCoord(f.lo(c4), f.hi(c4)));
		assertEquals(519, SidWriteVisualizer.freqAsXCoord(f.lo(c5), f.hi(c5)));
		assertEquals(649, SidWriteVisualizer.freqAsXCoord(f.lo(c6), f.hi(c6)));
		assertEquals(779, SidWriteVisualizer.freqAsXCoord(f.lo(c7), f.hi(c7)));
		assertEquals(909, SidWriteVisualizer.freqAsXCoord(f.lo(c8), f.hi(c8)));
		assertEquals(1023,
				SidWriteVisualizer.freqAsXCoord(f.lo(highest), f.hi(highest)));
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
