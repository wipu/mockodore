package org.fluentjava.mockodore.app.sidex;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.app.sidex.CLabMidi;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.junit.Test;

public class CLabMidiHelloworldTest extends SidexTestBase {

	private static final RawAddress BGCOLOR_ADDR = RawAddress.named(0xD020);
	private static final RawAddress SCREEN = RawAddress.named(0x400);

	@Override
	void beforeTest() {
		// nothing
	}

	@Test
	public void initAndBusyloopThatPrintsValuesFromMidiIn() throws IOException {
		Label resetAndInit = Label.named("resetAndInit");

		BasicLoader.def(p);
		p.label(resetAndInit);
		p.lda(CLabMidi.ACIA_RESET).sta(CLabMidi.MIDI_CTRL);
		p.lda(CLabMidi.ACIA_ENABLE_WITH_NO_IRQ).sta(CLabMidi.MIDI_CTRL);
		p.ldx(0);

		Label readLoop = Label.named("readLoop");
		p.label(readLoop);
		p.lda(CLabMidi.MIDI_STATUS);
		p.lsr();
		p.bcc(readLoop);
		p.commentLine("Flash bg color to show we received data:");
		p.inc(BGCOLOR_ADDR);
		p.sta(SCREEN.plus(257));
		p.lda(CLabMidi.MIDI_RECV_DATA);

		p.commentLine("write midi data on screen");
		p.sta(SCREEN.plusX());
		p.inx();
		p.lda(0xFF).sta(SCREEN.plusX());
		p.commentLine("Restore bg color to show we handled received data:");
		p.dec(BGCOLOR_ADDR);
		p.jmp(readLoop);

		simLoadedWithPrg().simpleSys(resetAndInit);

		int snapshotFreq = 50;

		// initial screen content ...
		assertEquals("20 20 20", simLoadedWithPrg().hexDump(SCREEN, 3));

		// ... stays visible ...
		simLoadedWithPrg().tick(snapshotFreq);
		assertEquals("20 20 20", simLoadedWithPrg().hexDump(SCREEN, 3));

		// ... until midi data arrives ...
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_RECV_DATA,
				UnsignedByte.$77);
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.$01);
		simLoadedWithPrg().tick(snapshotFreq);
		assertEquals("77 FF 20", simLoadedWithPrg().hexDump(SCREEN, 3));

		// ... and some more
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_RECV_DATA,
				UnsignedByte.$78);
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.$01);
		simLoadedWithPrg().tick(snapshotFreq);
		assertEquals("77 78 FF", simLoadedWithPrg().hexDump(SCREEN, 3));

		FileUtils.writeByteArrayToFile(
				new File("/tmp/" + getClass().getCanonicalName() + ".prg"),
				p.end().asPrgBytes().allBytes());
	}

}
