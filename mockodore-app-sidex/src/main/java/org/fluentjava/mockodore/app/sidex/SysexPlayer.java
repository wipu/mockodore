package org.fluentjava.mockodore.app.sidex;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.fluentjava.mockodore.model.sid.SidRegisterAddresses;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;

public class SysexPlayer extends C64AssyLangProxy<SysexPlayer, MockodoreProgram>
		implements SysexEventListener {

	private static final RawAddress BGCOLOR_ADDR = RawAddress.named(0xD020);
	private static final UnsignedByte LIGHT_RED = UnsignedByte.$0A;
	private static final UnsignedByte LIGHT_GREEN = UnsignedByte.$0D;
	private static final RawAddress DEFAULT_BUF = RawAddress.named(0x400);
	private static final RawAddress SYSEX_BUF = DEFAULT_BUF.plus(256);
	private static final Label START = Label.named("START");
	private static final RawAddress VIC2_CONTROL = RawAddress.named(0xD011);
	final Label initShovel = Label.named("initShovel");
	private final C64AssyLangForProgram p;

	public SysexPlayer(C64AssyLangForProgram out) {
		super(out);
		this.p = out;
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
