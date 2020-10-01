package org.fluentjava.mockodore.app.sidex;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.simulator.Memory;
import org.fluentjava.mockodore.simulator.RawMemory;
import org.junit.Test;

/**
 * C64 library that polls midi and calls handler code for received bytes. It
 * also increments a buffer overrun indicator on screen.
 * 
 * TODO let handler change background color so we don't waste cycles when they
 * matter.
 */
public class MidiPollerTest extends SidexTestBase {

	private static final RawAddress MIDIBUF = RawAddress.named(0x401);
	private MidiPoller poller;

	@Override
	void beforeTest() {
		Label handleMidi = Label.named("handleMidi");
		poller = new MidiPoller(p, handleMidi);

		BasicLoader.def(p);
		p.ldx(0);
		poller.def();

		p.label(handleMidi);
		p.sta(MIDIBUF.plusX());
		p.inx();
		p.rts();

		simLoadedWithPrg().simpleSys(MidiPoller.START);
	}

	@Override
	protected Memory mem() {
		return new MemoryWithAutoZeroMidiStatus(new RawMemory());
	}

	private MidiPollerTest timePasses() {
		simLoadedWithPrg().tick(100);
		return this;
	}

	private static class ReceiveOverrun {

		private UnsignedByte value;

		ReceiveOverrun(UnsignedByte value) {
			this.value = value;
		}

	}

	private static ReceiveOverrun tooLate() {
		return new ReceiveOverrun(UnsignedByte.x20);
	}

	private static ReceiveOverrun inTime() {
		return new ReceiveOverrun(UnsignedByte.x00);
	}

	private MidiPollerTest newMidiData(int value, ReceiveOverrun overrun) {
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_RECV_DATA,
				UnsignedByte.from(value));
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.x01.or(overrun.value));
		return this;
	}

	private MidiPollerTest newMidiData(int value) {
		return newMidiData(value, inTime());
	}

	private void midiBufferShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(MIDIBUF, 4));
	}

	// the tests
	// --------------------------------------

	@Test
	public void itCallsMidiRoutineWhenDataAvailable() {
		timePasses().midiBufferShallBe("20 20 20 20");
		newMidiData(1).timePasses().midiBufferShallBe("01 20 20 20");
		newMidiData(2).timePasses().midiBufferShallBe("01 02 20 20");
	}

	@Test
	public void itIncrementsErrorCharOnScreenAtMidiReceiverOverrun() {
		assertEquals("20", simLoadedWithPrg()
				.hexDump(MidiAndRasterPoller.OVERRUN_INDICATOR, 1));
		midiBufferShallBe("20 20 20 20");

		// no overrun
		newMidiData(1, inTime()).timePasses().midiBufferShallBe("01 20 20 20");
		assertEquals("20", simLoadedWithPrg()
				.hexDump(MidiAndRasterPoller.OVERRUN_INDICATOR, 1));
		midiBufferShallBe("01 20 20 20");

		// overrun
		newMidiData(2, tooLate()).timePasses().midiBufferShallBe("01 02 20 20");
		assertEquals("21", simLoadedWithPrg()
				.hexDump(MidiAndRasterPoller.OVERRUN_INDICATOR, 1));
		midiBufferShallBe("01 02 20 20");
	}

}
