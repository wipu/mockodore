package org.fluentjava.mockodore.util.sidripper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.machine.Accumulator;
import org.fluentjava.mockodore.model.machine.C64SimulatorLineLogger;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SidWriteRipperTest {

	@TempDir
	public File tmpDir;

	/**
	 * Just a smoke test, details are tested in subcomponent tests
	 */
	@Test
	public void allWantedOutputIsGottenAndWritesArePassedToDelegate()
			throws IOException {
		C64SimulatorLineLogger delegate = new C64SimulatorLineLogger();
		SidWriteRipper ripper = SidWriteRipper.using(delegate, 0, 10);

		ripper.playCallStarting();
		// first a non-sid event, to be delegated
		Accumulator a = new Accumulator(ripper);
		a.set(UnsignedByte.x01);
		// then sid writes
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			ripper.writeMem(reg.address().value(), UnsignedByte.x02);
		}

		ripper.playCallStarting();
		// and another frame with some sid writes
		ripper.writeMem(SidRegisterAddress.AD_1.address().value(),
				UnsignedByte.x03);
		ripper.writeMem(SidRegisterAddress.SR_1.address().value(),
				UnsignedByte.x03);

		// assert results:

		// the non sid-write was delegated:
		assertTrue(delegate.eventLog().contains("A=$01"));
		// also sid-writes were delegated:
		assertTrue(delegate.eventLog().contains("$D400 [] <-#$02"));

		String prettyLog = ripper.sidWriteLog();
		assertTrue(prettyLog.startsWith(
				"=== Frame 0\n" + " 1   | FreqLo1=#$02 ($D400=#$02)\n" + ""));

		// NOTE: this doesn't show the last frame, it only appends at every
		// frame start:
		String fullFrameHexDump = ripper.fullFrameHexDump();
		assertEquals("00000000000000000000000000000000000000000000000000\n"
				+ "02020202020202020202020202020202020202020202020202\n" + "",
				fullFrameHexDump);

		byte[] midiSysex = ripper.toMidiSysex();
		assertTrue(ByteArrayPrettyPrinter.spaceSeparatedHex(midiSysex)
				.startsWith("4D 54 68 64 00 "));

		File gif = new File(tmpDir, "sid-writes.gif");
		ripper.writeGifTo(gif);
		assertTrue(gif.exists());

		String html = ripper.toHtml(gif);
		assertTrue(html.contains("<html>"));
		assertTrue(html.contains("<img src=\"sid-writes.gif\""));

		assertEquals("[AD1, SR1]", ripper.sidWriteOrderAnalysis());
	}

	@Test
	public void skippedFramesAreExecutedForEffectButNotReported()
			throws IOException {
		C64SimulatorLineLogger delegate = new C64SimulatorLineLogger();
		SidWriteRipper ripper = SidWriteRipper.using(delegate, 1, 4);

		// frame 0, write first reg for effect
		ripper.playCallStarting();
		ripper.writeMem(SidRegisterAddress.FREQ_LO_1.address().value(),
				UnsignedByte.x01);

		// frame 1, first frame to be reported
		ripper.playCallStarting();
		ripper.writeMem(SidRegisterAddress.FREQ_HI_1.address().value(),
				UnsignedByte.x02);

		// frame 2, second frame to be reported
		ripper.playCallStarting();
		ripper.writeMem(SidRegisterAddress.PW_LO_1.address().value(),
				UnsignedByte.x03);
		// TODO don't write these, now we are preventing a npe in html
		// generator:
		ripper.writeMem(SidRegisterAddress.CR_1.address().value(),
				UnsignedByte.x02);
		ripper.writeMem(SidRegisterAddress.CR_2.address().value(),
				UnsignedByte.x02);
		ripper.writeMem(SidRegisterAddress.CR_3.address().value(),
				UnsignedByte.x02);

		// frame 3, to be reported, but see problem below:
		ripper.playCallStarting();
		ripper.writeMem(SidRegisterAddress.PW_HI_1.address().value(),
				UnsignedByte.x04);

		// NOTE: this doesn't show the last frame, it only appends at every
		// frame start:
		// But in this test the point is that the first "01" only frame is not
		// reported:
		String fullFrameHexDump = ripper.fullFrameHexDump();
		assertEquals("00000000000000000000000000000000000000000000000000\n"
				+ "01020000000000000000000000000000000000000000000000\n"
				+ "01020300020000000000000200000000000002000000000000\n" + "",
				fullFrameHexDump);

		File gif = new File(tmpDir, "sid-writes.gif");
		ripper.writeGifTo(gif);
		assertTrue(gif.exists());

		String html = ripper.toHtml(gif);
		// html contains only frames 0-1 (it counts from zero):
		assertTrue(html.contains("0 OSC_1"));
		assertTrue(html.contains("1 OSC_1"));
		// but not frame 2:
		assertFalse(html.contains("2 OSC_1"));
	}

}
