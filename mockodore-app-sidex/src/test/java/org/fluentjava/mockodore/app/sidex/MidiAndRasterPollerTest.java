package org.fluentjava.mockodore.app.sidex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.simulator.Memory;
import org.fluentjava.mockodore.simulator.RawMemory;
import org.junit.jupiter.api.Test;

public class MidiAndRasterPollerTest extends SidexTestBase {

	private static final int RTRIG = MidiAndRasterPoller.RASTER_TRIGGER;
	private static final RawAddress FRAMECOUNTER = RawAddress.named(0x400);
	private static final RawAddress MIDIBUF = RawAddress.named(0x401);
	private MidiAndRasterPoller poller;

	@Override
	void beforeTest() {
		Label handleRaster = Label.named("handleRaster");
		Label handleMidi = Label.named("handleMidi");
		poller = new MidiAndRasterPoller(p, handleRaster, handleMidi);

		BasicLoader.def(p);
		p.ldx(0);
		poller.def();

		p.label(handleRaster);
		p.inc(FRAMECOUNTER);
		p.rts();

		p.label(handleMidi);
		p.sta(MIDIBUF.plusX());
		p.inx();
		p.rts();

		simLoadedWithPrg().simpleSys(MidiAndRasterPoller.START);
	}

	@Override
	protected Memory mem() {
		return new MemoryWithAutoZeroMidiStatus(new RawMemory());
	}

	private MidiAndRasterPollerTest timePasses() {
		simLoadedWithPrg().tick(100);
		return this;
	}

	private MidiAndRasterPollerTest newRasterLine(int newValue) {
		simLoadedWithPrg().spontaneouslyWrite(
				MidiAndRasterPoller.CURRENT_RASTER_LINE,
				UnsignedByte.from(newValue));
		return this;
	}

	private MidiAndRasterPollerTest frameCounterShallBe(int value) {
		// the simulated initial value on screen is $20:
		UnsignedByte expected = UnsignedByte.x20.plus(UnsignedByte.from(value));
		assertEquals(
				ByteArrayPrettyPrinter.spaceSeparatedHex(expected.signedByte()),
				simLoadedWithPrg().hexDump(FRAMECOUNTER, 1));
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

	private MidiAndRasterPollerTest newMidiData(int value,
			ReceiveOverrun overrun) {
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_RECV_DATA,
				UnsignedByte.from(value));
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.x01.or(overrun.value));
		return this;
	}

	private MidiAndRasterPollerTest newMidiData(int value) {
		return newMidiData(value, inTime());
	}

	private void midiBufferShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(MIDIBUF, 4));
	}

	// the tests
	// --------------------------------------

	@Test
	public void rasterTriggerValue() {
		assertEquals(57, RTRIG);
	}

	@Test
	public void itCallsRasterRoutineWhenRasterLineAdvancesToSetValue() {
		timePasses().frameCounterShallBe(0);
		newRasterLine(RTRIG - 1).timePasses().frameCounterShallBe(0);
		newRasterLine(RTRIG).timePasses().frameCounterShallBe(1);
		newRasterLine(RTRIG + 1).timePasses().frameCounterShallBe(1);
	}

	@Test
	public void itCallsRasterRoutineWhenRasterLinePassesOverSetValue() {
		timePasses().frameCounterShallBe(0);
		newRasterLine(RTRIG - 1).timePasses().frameCounterShallBe(0);
		newRasterLine(RTRIG + 1).timePasses().frameCounterShallBe(1);
		newRasterLine(RTRIG + 2).timePasses().frameCounterShallBe(1);
	}

	@Test
	public void itCallsRasterRoutineAgainWhenRasterPassesOverAgain() {
		itCallsRasterRoutineWhenRasterLinePassesOverSetValue();

		newRasterLine(RTRIG + 10).timePasses().frameCounterShallBe(1);
		newRasterLine(RTRIG + 20).timePasses().frameCounterShallBe(1);

		// new frame
		newRasterLine(RTRIG - 1).timePasses().frameCounterShallBe(1);
		newRasterLine(RTRIG + 1).timePasses().frameCounterShallBe(2);
		newRasterLine(RTRIG + 2).timePasses().frameCounterShallBe(2);
	}

	@Test
	public void itCallsMidiRoutineWhenDataAvailableBeforeRasterLine() {
		timePasses().midiBufferShallBe("20 20 20 20");
		newMidiData(1).timePasses().midiBufferShallBe("01 20 20 20");
		newMidiData(2).timePasses().midiBufferShallBe("01 02 20 20");
	}

	@Test
	public void itCallsMidiRoutineWhenDataAvailableAfterRasterLine() {
		itCallsRasterRoutineWhenRasterLinePassesOverSetValue();
		midiBufferShallBe("20 20 20 20");
		timePasses();
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
