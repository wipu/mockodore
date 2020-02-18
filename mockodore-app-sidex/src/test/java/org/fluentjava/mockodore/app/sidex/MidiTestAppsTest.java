package org.fluentjava.mockodore.app.sidex;

import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$00;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$0F;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$11;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$81;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$F0;
import static org.fluentjava.joulu.unsignedbyte.UnsignedByte.$FF;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fluentjava.joulu.midievents.Midievents;
import org.fluentjava.joulu.midievents.Midievents.MidieventsPlease;
import org.fluentjava.joulu.midievents.Midiseq;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.app.sidex.CLabMidi;
import org.fluentjava.mockodore.app.sidex.MidiAndRasterPoller;
import org.fluentjava.mockodore.app.sidex.MidiDispatcher;
import org.fluentjava.mockodore.app.sidex.MidiPoller;
import org.fluentjava.mockodore.app.sidex.SysexEventListener;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.sid.Codebase64PalNote;
import org.fluentjava.mockodore.model.sid.Codebase64PalNoteFreqs;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.fluentjava.mockodore.model.sid.SidRegisterAddresses;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.simulator.Memory;
import org.fluentjava.mockodore.simulator.RawMemory;
import org.fluentjava.mockodore.util.sidripper.SidWritesToMidiSysex;
import org.fluentjava.mockodore.util.sysex.SysexEncoder;
import org.junit.Test;

public class MidiTestAppsTest extends Widi64TestBase {

	private static final RawAddress BGCOLOR_ADDR = RawAddress.named(0xD020);
	private static final UnsignedByte LIGHT_RED = UnsignedByte.$0A;
	private static final UnsignedByte LIGHT_GREEN = UnsignedByte.$0D;
	private static final RawAddress DEFAULT_BUF = RawAddress.named(0x400);
	private static final RawAddress SYSEX_BUF = DEFAULT_BUF.plus(256);
	private static final Label START = Label.named("START");
	private static final RawAddress RASTER_COUNTER = MidiAndRasterPoller.OVERRUN_INDICATOR
			.plus(1);
	private static final RawAddress VIC2_CONTROL = RawAddress.named(0xD011);

	@Override
	protected Memory mem() {
		return new MemoryWithAutoZeroMidiStatus(new RawMemory());
	}

	private class MidiAndSysexVisualizer
			extends C64AssyLangProxy<MidiAndSysexVisualizer, MockodoreProgram>
			implements SysexEventListener {

		public MidiAndSysexVisualizer(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}

		void def() {
			Label defaultBufOffset = Label.named("defaultBufOffset");
			Label handleDefault = Label.named("handleDefault");
			Label handleRaster = Label.named("handleRaster");

			MidiDispatcher dispatcher = new MidiDispatcher(p, handleDefault,
					SYSEX_BUF, 1, this);
			MidiAndRasterPoller poller = new MidiAndRasterPoller(out,
					handleRaster, dispatcher.label());

			BasicLoader.def(p);

			label(START);
			poller.def();
			dispatcher.defRoutine();

			label(handleDefault);
			data(Op.LDX_IMMEDIATE).label(defaultBufOffset).data(0);
			sta(DEFAULT_BUF.plusX());
			inc(defaultBufOffset);
			rts();

			label(handleRaster);
			Label handleRasterDelay = handleRaster.subLabel("_delay");
			commentLine("delaying to look like a realistic raster routine");
			ldx(50);
			label(handleRasterDelay);
			dex();
			bne(handleRasterDelay);
			inc(RASTER_COUNTER);
			rts();
		}

		@Override
		public void sysexSkipByteBegin() {
			p.ldx(LIGHT_RED).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexSkipByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeBegin() {
			commentLine("disable raster routine:");
			p.ldx(Op.LDA_ABS.byteValue())
					.stx(MidiAndRasterPoller.JSR_RASTER_ROUTINE);
			commentLine("clear overrun indicator:");
			p.ldx(0x20).stx(MidiAndRasterPoller.OVERRUN_INDICATOR);
		}

		@Override
		public void sysexReceiveByteBegin() {
			p.ldx(LIGHT_GREEN).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeEnd() {
			Label reenableRasterRoutine = Label.named("reenableRasterRoutine");
			Label reenableRasterRoutineDone = reenableRasterRoutine
					.subLabel("_done");

			label(reenableRasterRoutine);
			ldx(MidiAndRasterPoller.OVERRUN_INDICATOR);
			cpx(0x20);
			bne(reenableRasterRoutineDone);
			p.ldx(Op.JSR.byteValue())
					.stx(MidiAndRasterPoller.JSR_RASTER_ROUTINE);
			label(reenableRasterRoutineDone);
		}

	}

	@Override
	void beforeTest() {
		// nothing
	}

	@Test
	public void midiAndSysexVisualizerCompiles() throws IOException {
		new MidiAndSysexVisualizer(p).def();
		FileUtils
				.writeByteArrayToFile(
						new File("/tmp/" + MidiAndSysexVisualizer.class
								.getCanonicalName() + ".prg"),
						p.end().asPrgBytes().allBytes());
	}

	private MidiTestAppsTest midiIn(int data) {
		return midiIn(UnsignedByte.from(data));
	}

	private MidiTestAppsTest midiIn(int data, boolean isInTime) {
		return midiIn(UnsignedByte.from(data), isInTime);
	}

	private MidiTestAppsTest midiIn(UnsignedByte data) {
		return midiIn(data, true);
	}

	private MidiTestAppsTest midiIn(UnsignedByte data, boolean isInTime) {
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_RECV_DATA, data);
		UnsignedByte status = UnsignedByte.$01;
		if (!isInTime) {
			status = status.or(UnsignedByte.$20);
		}
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS, status);
		return this;
	}

	private MidiTestAppsTest timePasses(int ticks) {
		simLoadedWithPrg().tick(ticks);
		return this;
	}

	private MidiTestAppsTest timePasses() {
		return timePasses(500);
	}

	private MidiTestAppsTest defaultBufShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(DEFAULT_BUF, 4));
		return this;
	}

	private MidiTestAppsTest sysexBufShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(SYSEX_BUF, 4));
		return this;
	}

	private MidiTestAppsTest sidRegistersShallBe(String expected) {
		assertEquals(expected,
				simLoadedWithPrg().hexDump(SidRegisterAddress.first().address(),
						SidRegisterAddress.all().size()));
		return this;
	}

	private MidiTestAppsTest midiOverrunIndicatorShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg()
				.hexDump(MidiAndRasterPoller.OVERRUN_INDICATOR, 1));
		return this;
	}

	private MidiTestAppsTest rasterCounterShallBe(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(RASTER_COUNTER, 1));
		return this;
	}

	private MidiTestAppsTest rasterLineChangesTo(int value) {
		simLoadedWithPrg().spontaneouslyWrite(
				MidiAndRasterPoller.CURRENT_RASTER_LINE,
				UnsignedByte.from(value));
		return this;
	}

	private void midiAndSysexVisualizerStarts() {
		new MidiAndSysexVisualizer(p).def();
		simLoadedWithPrg().simpleSys(START);
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.$00);
	}

	private void sysexPlayerStartsAndTimePasses() {
		new SysexPlayer(p).def();
		simLoadedWithPrg().simpleSys(START);
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.$00);
		timePasses(2000);
	}

	@Test
	public void midiAndSysexVisualizerVisualizesAllEventTypes() {
		midiAndSysexVisualizerStarts();

		// raster counter
		rasterLineChangesTo(0);
		rasterCounterShallBe("20");
		rasterLineChangesTo(150).timePasses(1000);
		rasterCounterShallBe("21");

		// normal midi buf
		midiIn(0x80).timePasses();
		defaultBufShallBe("80 20 20 20").sysexBufShallBe("20 20 20 20");

		// midi sysex buf
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiIn(0x01).timePasses();
		defaultBufShallBe("80 20 20 20").sysexBufShallBe("01 20 20 20");

		// overrun indicator
		midiOverrunIndicatorShallBe("20");
		midiIn(0x02, false).timePasses().sysexBufShallBe("01 02 20 20");
		midiOverrunIndicatorShallBe("21");

		// new sysex start clears overrun indicator
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiOverrunIndicatorShallBe("20"); // clear again
	}

	@Test
	public void rasterRoutineIsNotCalledDuringSysexReceive() {
		midiAndSysexVisualizerStarts();

		rasterLineChangesTo(0);
		rasterCounterShallBe("20");

		// sysex starts
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiIn(0x01).timePasses();
		defaultBufShallBe("20 20 20 20").sysexBufShallBe("01 20 20 20");

		// rasterline is passed but no routine call
		rasterLineChangesTo(150).timePasses(5000);
		rasterCounterShallBe("20"); // still

		// sysex ends
		midiIn(0xF7).timePasses();
		defaultBufShallBe("20 20 20 20").sysexBufShallBe("01 20 20 20");

		// rasterline is passed again and now raster routine is called
		rasterLineChangesTo(0).timePasses(500);
		rasterLineChangesTo(150).timePasses(5000);
		rasterCounterShallBe("21");
	}

	/**
	 * Unless of course the number of overruns is divisible by 256...
	 */
	@Test
	public void rasterRoutineCallIsNotResumedIfThereWasOverrunDuringSysexReceive() {
		midiAndSysexVisualizerStarts();

		rasterLineChangesTo(0);
		rasterCounterShallBe("20");

		// sysex starts
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiIn(0x01).timePasses().sysexBufShallBe("01 20 20 20");

		// rasterline is passed but no routine call
		rasterLineChangesTo(150).timePasses(5000);
		rasterCounterShallBe("20"); // still

		// sysex ends but not before some overrun
		midiIn(0x02, false).timePasses();
		midiIn(0xF7).timePasses().sysexBufShallBe("01 02 20 20");

		// rasterline is passed again but still raster routine is not called
		rasterLineChangesTo(0).timePasses(500);
		rasterLineChangesTo(150).timePasses(5000);
		rasterCounterShallBe("20"); // not safe to call it
	}

	private class SysexPlayer
			extends C64AssyLangProxy<SysexPlayer, MockodoreProgram>
			implements SysexEventListener {

		final Label initShovel = Label.named("initShovel");

		public SysexPlayer(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}

		void def() {
			Label defaultBufOffset = Label.named("defaultBufOffset");
			Label handleDefault = Label.named("handleDefault");

			// lda imm + lda abs = 2 + 3 = 5
			int sysexBufInterleave = 5;
			MidiDispatcher dispatcher = new MidiDispatcher(p, handleDefault,
					SYSEX_BUF.plus(1), sysexBufInterleave, this);
			MidiPoller poller = new MidiPoller(out, dispatcher.label());

			BasicLoader.def(p);

			label(START);

			blankScreen();
			jsr(initShovel);

			poller.def();
			dispatcher.defRoutine();

			label(handleDefault);
			data(Op.LDX_IMMEDIATE).label(defaultBufOffset).data(0);
			sta(DEFAULT_BUF.plusX());
			inc(defaultBufOffset);
			rts();

			label(initShovel);
			commentLine("Initialize sysex buffer with shoveling code:");
			int bufIdx = 0;
			for (SidRegisterAddress reg : SidRegisterAddresses
					.allInDefaultWritingOrder()) {
				commentLine("  " + reg.name());
				lda(Op.LDA_IMMEDIATE).sta(SYSEX_BUF.plus(bufIdx++));
				lda(0).sta(SYSEX_BUF.plus(bufIdx++));
				lda(Op.STA_ABS).sta(SYSEX_BUF.plus(bufIdx++));
				lda(reg.address().lsb()).sta(SYSEX_BUF.plus(bufIdx++));
				lda(reg.address().msb()).sta(SYSEX_BUF.plus(bufIdx++));
			}
			commentLine(
					"And finally write RTS to end the sysex buffer shoveling routine:");
			lda(Op.RTS).sta(SYSEX_BUF.plus(bufIdx++));
			rts();
		}

		private void blankScreen() {
			p.commentLine("Blank screen to get more CPU cycles");
			// TODO library function for this
			p.lda(UnsignedByte.$10.not());
			p.and(VIC2_CONTROL);
			p.sta(VIC2_CONTROL);
		}

		@Override
		public void sysexSkipByteBegin() {
			p.ldx(LIGHT_RED).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexSkipByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeBegin() {
			// nothing to do
		}

		@Override
		public void sysexReceiveByteBegin() {
			p.ldx(LIGHT_GREEN).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeEnd() {
			Label writeFrameToSid = Label.named("writeFrameToSid");

			label(writeFrameToSid);
			jsr(SYSEX_BUF);
		}

	}

	@Test
	public void sysexPlayerCompiles() throws IOException {
		new SysexPlayer(p).def();
		FileUtils.writeByteArrayToFile(new File(
				"/tmp/" + SysexPlayer.class.getCanonicalName() + ".prg"),
				p.end().asPrgBytes().allBytes());
	}

	private void midiInAndTimePasses(List<UnsignedByte> bytes) {
		for (UnsignedByte b : bytes) {
			midiIn(b).timePasses();
		}
	}

	@Test
	public void sysexPlayerWritesGivenBytesToSidRegisters() {
		sysexPlayerStartsAndTimePasses();

		// frame 0
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiInAndTimePasses(SysexEncoder.sysexEncoded(1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
				0, 0, 0));
		midiIn(0xF7).timePasses(1000);

		sidRegistersShallBe(
				"04 03 06 05 07 01 02 0B 0A 0D 0C 0E 08 09 12 11 14 13 15 0F 10 17 16 19 18");

		// frame 1
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiInAndTimePasses(SysexEncoder.sysexEncoded(10, 11, 12, 13, 14, 15,
				16, 17, 18, 19, 110, 111, 112, 113, 114, 115, 116, 117, 118,
				119, 120, 121, 122, 123, 124, 0, 0, 0));
		midiIn(0xF7).timePasses(1000);

		sidRegistersShallBe(
				"0D 0C 0F 0E 10 0A 0B 6E 13 70 6F 71 11 12 75 74 77 76 78 72 73 7A 79 7C 7B");
	}

	/**
	 * BEFORE, when buffer was a continous data area, and shovel a dedicated
	 * ldaAbs-staAbs chain, cycle count of shoveling was 200:
	 * 
	 * Worst case: let's say a bad line adds 43 more cycles => frame dump takes
	 * 243 cycles.
	 * 
	 * On my PAL machine it is 243 / 985248 Hz = .0002466 s.
	 * 
	 * Midi comes in at 31250 b/s = 3906.25 B/s, so the interval between
	 * received bytes is the inverse of it, .0002560 s.
	 * 
	 * So in theory the register dump is just *barely* fast enough, but practice
	 * shows otherwise: there are overruns. Yes, this test doesn't even include
	 * the overhead of calling the shovel routine.
	 * 
	 * If the shovel code contains LDA immediates, and midi receive writes to
	 * their operands, the switch from LDA abs to LDA immediate saves 4-2=2
	 * cycles per sidregister, 2*25=50 cycles, more than a bad line! So the
	 * worst case would be 193 / 985248 Hz = .0001959, well below the midi byte
	 * receive interval.
	 * 
	 * AFTER, when buffer was code of ldaImm,staAbs, count went down to 150.
	 * Downside: at least currently registers are written in index order, not in
	 * order of audible artifacts.
	 */
	@Test
	public void sysexPlayerShovelRoutineCycleCountIsLowEnoughForNoMidiBufferOverruns() {
		SysexPlayer sysexPlayer = new SysexPlayer(p);
		sysexPlayer.def();
		// pretend relevant init code is called:
		simLoadedWithPrg().simpleSysAndAutoTick(sysexPlayer.initShovel);
		// pretend buffer just got full:
		for (int i = 0; i < SidRegisterAddress.all().size(); i++) {
			simLoadedWithPrg().spontaneouslyWrite(SYSEX_BUF.plus(1 + i * 5),
					$11);
		}
		// then measure cycles until all registers have been shoveled:
		simLoadedWithPrg().simpleSys(SYSEX_BUF);
		int startTime = logger.time();
		while (isSomeSidRegisterZero()
				&& simLoadedWithPrg().isSimpleSysRunning()) {
			simLoadedWithPrg().tick();
		}

		assertEquals(150, logger.time() - startTime);
	}

	@Test
	public void sysexPlayerTurnsOffScreenToGetMoreCpuCycles() {
		new SysexPlayer(p).def();
		simLoadedWithPrg().simpleSys(START);
		// write FF to make sure only the wanted bit gets cleared
		simLoadedWithPrg().spontaneouslyWrite(VIC2_CONTROL, UnsignedByte.$FF);
		timePasses(100);
		assertEquals(UnsignedByte.$EF,
				simLoadedWithPrg().valueIn(VIC2_CONTROL));
	}

	private boolean isSomeSidRegisterZero() {
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			if (simLoadedWithPrg().valueIn(reg.address()).equals($00)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * App for testing sysex transfer reliability. Plays a nice sound when
	 * receives dump with good checksum, and noise when the checksum is
	 * incorrect.
	 */
	private class SysexChecker
			extends C64AssyLangProxy<SysexChecker, MockodoreProgram>
			implements SysexEventListener {

		private final Label handleReceivedDump = Label
				.named("handleReceivedDump");
		private final Codebase64PalNoteFreqs freqs = new Codebase64PalNoteFreqs();

		public SysexChecker(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}

		void def() {
			Label defaultBufOffset = Label.named("defaultBufOffset");
			Label handleDefault = Label.named("handleDefault");
			Label handleRaster = Label.named("handleRaster");
			Label addThem = handleReceivedDump.subLabel("_addThem");
			Label sumGood = handleReceivedDump.subLabel("_sumGood");
			Label isEnd = handleReceivedDump.subLabel("_isEnd");

			MidiDispatcher dispatcher = new MidiDispatcher(p, handleDefault,
					SYSEX_BUF, 1, this);
			MidiAndRasterPoller poller = new MidiAndRasterPoller(out,
					handleRaster, dispatcher.label());

			BasicLoader.def(p);

			label(START);
			poller.def();
			dispatcher.defRoutine();

			label(handleDefault);
			data(Op.LDX_IMMEDIATE).label(defaultBufOffset).data(0);
			sta(DEFAULT_BUF.plusX());
			inc(defaultBufOffset);
			rts();

			label(handleRaster);
			inc(RASTER_COUNTER);
			rts();

			label(handleReceivedDump);
			ldx(28 - 1).commentAfterInstr("init counter with dump size -1");
			lda(0);
			label(addThem);
			clc();
			adc(SYSEX_BUF.plusX());
			dex();
			cpx($FF); // TODO optimize with proper branch instr
			bne(addThem);

			commentLine("check the sum");
			cmp(0);
			beq(sumGood);
			commentLine("sum is bad, sound noise");
			Codebase64PalNote noiseNote = Codebase64PalNote.A__4;
			lda(freqs.hi(noiseNote))
					.sta(SidRegisterAddress.FREQ_HI_1.address());
			lda(freqs.lo(noiseNote))
					.sta(SidRegisterAddress.FREQ_LO_1.address());
			lda($00).sta(SidRegisterAddress.AD_1.address());
			lda($F0).sta(SidRegisterAddress.SR_1.address());
			lda($81).sta(SidRegisterAddress.CR_1.address());
			lda($0F).sta(SidRegisterAddress.MODE_VOL.address());
			rts();

			label(sumGood);
			lda(SYSEX_BUF);
			cmp(0);
			beq(isEnd);
			commentLine("sum is good, not end, sound nice sound");
			Codebase64PalNote niceNote = Codebase64PalNote.A__4;
			lda(freqs.hi(niceNote)).sta(SidRegisterAddress.FREQ_HI_1.address());
			lda(freqs.lo(niceNote)).sta(SidRegisterAddress.FREQ_LO_1.address());
			lda($00).sta(SidRegisterAddress.AD_1.address());
			lda($F0).sta(SidRegisterAddress.SR_1.address());
			lda($11).sta(SidRegisterAddress.CR_1.address());
			lda($0F).sta(SidRegisterAddress.MODE_VOL.address());
			rts();

			label(isEnd);
			commentLine("end of test => silence");
			lda(0).sta(SidRegisterAddress.MODE_VOL.address());
			rts();
		}

		@Override
		public void sysexSkipByteBegin() {
			p.ldx(LIGHT_RED).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexSkipByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeBegin() {
			commentLine("disable raster routine:");
			p.ldx(Op.LDA_ABS.byteValue())
					.stx(MidiAndRasterPoller.JSR_RASTER_ROUTINE);
		}

		@Override
		public void sysexReceiveByteBegin() {
			p.ldx(LIGHT_GREEN).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveByteEnd() {
			p.ldx(0).stx(BGCOLOR_ADDR);
		}

		@Override
		public void sysexReceiveModeEnd() {
			Label writeFrameToSid = Label.named("writeFrameToSid");

			label(writeFrameToSid);
			jsr(handleReceivedDump);

			commentLine("re-enable raster routine:");
			p.ldx(Op.JSR.byteValue())
					.stx(MidiAndRasterPoller.JSR_RASTER_ROUTINE);
		}

	}

	private void sysexCheckerStarts() {
		new SysexChecker(p).def();
		simLoadedWithPrg().simpleSys(START);
		simLoadedWithPrg().spontaneouslyWrite(CLabMidi.MIDI_STATUS,
				UnsignedByte.$00);
	}

	@Test
	public void sysexCheckerCompiles() throws IOException {
		new SysexChecker(p).def();
		FileUtils.writeByteArrayToFile(new File(
				"/tmp/" + SysexChecker.class.getCanonicalName() + ".prg"),
				p.end().asPrgBytes().allBytes());
	}

	@Test
	public void sysexCheckerTestData() throws IOException {
		MidieventsPlease e = Midievents.usingDefaults();

		int frameCount = 50 * 30;

		for (int f = 0; f < frameCount; f++) {
			List<UnsignedByte> msg = new ArrayList<>();
			msg.add(UnsignedByte.$F0);
			msg.add(UnsignedByte.$44); // Casio
			if (f == frameCount - 1) {
				// end
				msg.addAll(SysexEncoder.sysexEncoded(0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0));
			} else if (f % 100 == 0) {
				// deliberate checksum failure
				msg.addAll(SysexEncoder.sysexEncoded(1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						0));
			} else {
				// normal frame
				msg.addAll(SysexEncoder.sysexEncoded(1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						-27));
			}
			msg.add(UnsignedByte.$F7);
			// TODO make it easier to add sysex (this is copied from
			// SidWritesToMidiSysex):
			e.after(SidWritesToMidiSysex.TICKS_PER_FRAME);
			byte[] bytes = new byte[msg.size()];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = msg.get(i).signedByte();
			}
			e.sysex(bytes);
		}

		Midiseq seq = Midiseq.usingDefaults().events(e.end()).end();
		FileUtils.writeByteArrayToFile(new File("/tmp/c64-sysexcheck.mid"),
				seq.asBytes());
	}

	@Test
	public void sysexCheckerPlaysNiceToneIffNoPacketLoss() {
		sysexCheckerStarts();

		// frame 0: checksum matches
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiInAndTimePasses(SysexEncoder.sysexEncoded(1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -27));
		midiIn(0xF7).timePasses(1000);

		assertEquals(
				"01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 E5",
				simLoadedWithPrg().hexDump(SYSEX_BUF, 28));

		// => nice triangle sound
		sidRegistersShallBe(
				"A3 0E 00 00 11 00 F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F");

		// frame 1: checksum matches
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiInAndTimePasses(SysexEncoder.sysexEncoded(1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0));
		midiIn(0xF7).timePasses(1000);

		// => harsh noise
		sidRegistersShallBe(
				"A3 0E 00 00 81 00 F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F");

		// frame 2: checksum matches and buffer starts with zero
		midiIn(0xF0).timePasses();
		midiIn(MidiDispatcher.MANUFACTURER).timePasses();
		midiInAndTimePasses(SysexEncoder.sysexEncoded(0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		midiIn(0xF7).timePasses(1000);

		// => end of test, stop all sound
		sidRegistersShallBe(
				"A3 0E 00 00 81 00 F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

	}

}
