package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.machine.Accumulator;
import org.fluentjava.mockodore.model.machine.C64SimulatorLineLogger;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SidWriteRipperTest {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	/**
	 * Just a smoke test, details are tested in subcomponent tests
	 */
	@Test
	public void allWantedOutputIsGottenAndWritesArePassedToDelegate()
			throws IOException {
		C64SimulatorLineLogger delegate = new C64SimulatorLineLogger();
		SidWriteRipper ripper = SidWriteRipper.using(delegate, 10);

		ripper.playCallStarting();
		// first a non-sid event, to be delegated
		Accumulator a = new Accumulator(ripper);
		a.set(UnsignedByte.x01);
		// then sid writes
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			ripper.writeMem(reg.address().value(), UnsignedByte.x02);
		}

		ripper.playCallStarting();
		// and another frame with a sid write
		ripper.writeMem(SidRegisterAddress.AD_1.address().value(),
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

		File gif = new File(tmpDir.getRoot(), "sid-writes.gif");
		ripper.writeGifTo(gif);
		assertTrue(gif.exists());

		String html = ripper.toHtml(gif);
		assertTrue(html.contains("<html>"));
		assertTrue(html.contains("<img src=\"sid-writes.gif\""));
	}

}
