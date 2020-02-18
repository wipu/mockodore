package org.fluentjava.mockodore.app.sidex;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;

public class MidiPoller extends C64AssyLangProxy<MidiPoller, MockodoreProgram> {

	private static final RawAddress BGCOLOR = RawAddress.named(0xD020);
	private static final int COLOR_DEF = 0; // black
	private static final int COLOR_MIDI = 1; // white
	private static final int OVERRUN_CHAR_INDEX = 24 * 40;
	public static final RawAddress OVERRUN_INDICATOR = RawAddress
			.named(0x400 + OVERRUN_CHAR_INDEX);
	private static final RawAddress OVERRUN_INDICATOR_COLOR_ADDR = RawAddress
			.named(55296 + OVERRUN_CHAR_INDEX);
	public static final Label START = Label.named("MidiPoller");
	private final Label handleMidi;

	public MidiPoller(C64AssyLangOf<?, MockodoreProgram> out,
			Label handleMidi) {
		super(out);
		this.handleMidi = handleMidi;
	}

	public void def() {
		lda(CLabMidi.ACIA_RESET).sta(CLabMidi.MIDI_CTRL);
		lda(CLabMidi.ACIA_ENABLE_WITH_NO_IRQ).sta(CLabMidi.MIDI_CTRL);
		lda(2).sta(OVERRUN_INDICATOR_COLOR_ADDR); // red
		lda(0x20).sta(OVERRUN_INDICATOR);

		label(START);
		pollAndHandleMidi(START);
		jmp(START);
	}

	private void pollAndHandleMidi(Label label) {
		Label midiHandled = label.subLabel("_midiHandled");
		Label midiOverrrunHandled = label.subLabel("_midiOverrunHandled");

		lda(CLabMidi.MIDI_STATUS);
		lsr();
		bcc(midiHandled);

		and(0x10).commentAfterInstr("bit 5 after lsr");
		beq(midiOverrrunHandled);
		inc(OVERRUN_INDICATOR);
		label(midiOverrrunHandled);

		lda(CLabMidi.MIDI_RECV_DATA);
		setBgColor(COLOR_MIDI);
		jsr(handleMidi);
		setBgColor(COLOR_DEF);

		label(midiHandled);
	}

	private void setBgColor(int color) {
		ldy(color);
		sty(BGCOLOR);
	}

}
