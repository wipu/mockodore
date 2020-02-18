package org.fluentjava.mockodore.app.sidex;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;

public class MidiDispatcher
		extends C64AssyLangProxy<MidiDispatcher, MockodoreProgram> {

	private static final UnsignedByte SYSEX_START = UnsignedByte.$F0;
	public static final UnsignedByte MANUFACTURER = UnsignedByte.$44; // Casio
	private static final UnsignedByte SYSEX_END = UnsignedByte.$F7;
	private final Label label = Label.named("midiDispatcher");
	private final Label state = label.subLabel("_state");
	private final Label bytesUntilHighBitsByte = label
			.subLabel("_bytesUntilHighBitsByte");
	private final Label nextSysexDest = label.subLabel("_nextSysexDest");
	private final Label handleDefault;
	private final RawAddress sysexBuf;
	private final int sysexBufInterleave;
	private final SysexEventListener sysexListener;

	public MidiDispatcher(C64AssyLangOf<?, MockodoreProgram> out,
			Label handleDefault, RawAddress sysexBuf, int sysexBufInterleave,
			SysexEventListener sysexVisualizer) {
		super(out);
		this.handleDefault = handleDefault;
		this.sysexBuf = sysexBuf;
		this.sysexBufInterleave = sysexBufInterleave;
		this.sysexListener = sysexVisualizer;
	}

	public Label label() {
		return label;
	}

	public void defRoutine() {
		Label stateDefault = state.subLabel("_default");
		Label stateWaitManufacturer = state.subLabel("_waitManufacturer");
		Label stateSkipSysex = state.subLabel("_skipSysex");
		Label notSysexStart = label.subLabel("_notSysexStart");
		Label notMyManufacturer = stateWaitManufacturer
				.subLabel("_notMyManufacturer");
		Label stillSkippingSysex = stateSkipSysex
				.subLabel("_stillSkippingSysex");
		Label stateReceiveSysex = state.subLabel("_receiveSysex");
		Label fixHighBits = stateReceiveSysex.subLabel("_fixHighBits");
		Label stillReceivingSysex = stateReceiveSysex
				.subLabel("_stillReceivingSysex");
		Label received7bitSysex = stateReceiveSysex
				.subLabel("_received7bitSysex");

		label(label);
		cmp(SYSEX_START).bne(notSysexStart);
		setState(stateWaitManufacturer);
		rts();
		label(notSysexStart);
		data(Op.JMP_ABS).label(state).data(stateDefault);

		label(stateDefault);
		jmp(handleDefault);

		label(stateWaitManufacturer);
		cmp(MANUFACTURER).bne(notMyManufacturer);
		resetSysexReceiver();
		setState(stateReceiveSysex);
		sysexListener.sysexReceiveModeBegin();
		rts();
		label(notMyManufacturer);
		setState(stateSkipSysex).rts();

		label(stateSkipSysex);
		sysexListener.sysexSkipByteBegin();
		cmp(SYSEX_END).bne(stillSkippingSysex);
		setState(stateDefault);
		label(stillSkippingSysex);
		sysexListener.sysexSkipByteEnd();
		rts();

		label(stateReceiveSysex);
		sysexListener.sysexReceiveByteBegin();
		cmp(SYSEX_END).bne(stillReceivingSysex);
		setState(stateDefault);
		sysexListener.sysexReceiveByteEnd();
		sysexListener.sysexReceiveModeEnd();
		rts();
		label(stillReceivingSysex);
		cmp(0x00);
		bpl(received7bitSysex);
		sysexListener.sysexReceiveByteEnd();
		rts();
		label(received7bitSysex);
		data(Op.LDX_IMMEDIATE).label(bytesUntilHighBitsByte).data(7);
		beq(fixHighBits);
		data(Op.STA_ABS).label(nextSysexDest).data(sysexBuf);
		dec(bytesUntilHighBitsByte);
		commentLine("increment receive buffer address by " + sysexBufInterleave
				+ ":");
		add16(nextSysexDest, sysexBufInterleave, 0);
		sysexListener.sysexReceiveByteEnd();
		rts();
		label(fixHighBits);
		handleHighBitsByte();
		sysexListener.sysexReceiveByteEnd();
		rts();
	}

	private void handleHighBitsByte() {
		List<MsbFixer> fixers = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			fixers.add(new MsbFixer(i));
		}
		for (MsbFixer fixer : fixers) {
			fixer.consumeLsbFromAcc();
		}
		for (MsbFixer fixer : fixers) {
			fixer.updateAddrToFix();
		}
		for (MsbFixer fixer : fixers) {
			fixer.doFix();
		}

		resetGroupCounter();
	}

	private MidiDispatcher setState(Label newState) {
		ldx(newState.lsb()).stx(state);
		ldx(newState.msb()).stx(state.plus(1));
		return this;
	}

	private class MsbFixer {

		private final int index;
		private final Label l;
		private final Label ar;
		private final Label aw;
		private final Label valueToOr;

		MsbFixer(int index) {
			this.index = index;
			l = label.subLabel("_msbfixer" + index);
			ar = l.subLabel("_ar");
			aw = l.subLabel("_aw");
			valueToOr = l.subLabel("_valueToOr");
		}

		void consumeLsbFromAcc() {
			Label done = l.subLabel("_fixBitDone");
			commentLine("Consume acc lsb to fix " + index);
			ldx(0);
			lsr();
			bcc(done);
			ldx(0x80);
			label(done);
			stx(valueToOr);
		}

		void updateAddrToFix() {
			int offset = index - 7;
			commentLine("Update addr to fix msb: " + index + " (offset "
					+ offset + ")");
			clc();
			lda(nextSysexDest).adc(offset).sta(ar).sta(aw);
			// FF means duplication of the negative bit here:
			lda(nextSysexDest.plus(1)).adc(0xFF).sta(ar.plus(1))
					.sta(aw.plus(1));
		}

		void doFix() {
			data(Op.LDA_ABS).label(ar).data(sysexBuf);
			data(Op.ORA_IMMEDIATE).label(valueToOr).data(0);
			data(Op.STA_ABS).label(aw).data(sysexBuf);
		}

	}

	private void resetSysexReceiver() {
		resetGroupCounter();
		ldx(sysexBuf.lsb()).stx(nextSysexDest);
		ldx(sysexBuf.msb()).stx(nextSysexDest.plus(1));
	}

	private void resetGroupCounter() {
		ldx(7).stx(bytesUntilHighBitsByte);
	}

	private void add16(Label addr, int lsb, int msb) {
		clc();
		lda(addr).adc(lsb).sta(addr);
		lda(addr.plus(1)).adc(msb).sta(addr.plus(1));
	}

}
