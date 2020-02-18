package org.fluentjava.mockodore.app.sidex;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;

/**
 * C64 library that polls midi and raster liner, and calls handlers for received
 * midi bytes or passed raster line. It also increments a buffer overrun
 * indicator on screen and raster line counter.
 * 
 * TODO let handler change background color so we don't waste cycles when they
 * matter.
 * 
 * Also, this is probably not needed, because midi is a good source for timing,
 * and there is probably not much time to do anything in the raster handler, if
 * midi buffer overruns are to be avoided.
 */
public class MidiAndRasterPoller
		extends C64AssyLangProxy<MidiAndRasterPoller, MockodoreProgram> {

	public static final RawAddress CURRENT_RASTER_LINE = RawAddress
			.named(0xD012);
	/**
	 * This is big enough a value so it doesn't occur twice in the lowest 8 bits
	 * stored at CURRENT_RASTER_LINE. A lower value would require checking the
	 * highest bit also in a different register.
	 */
	public static final int RASTER_TRIGGER = 312 - 256 + 1;
	private static final RawAddress BGCOLOR = RawAddress.named(0xD020);
	private static final int COLOR_DEF = 0; // black
	private static final int COLOR_RASTER = 7; // yellow
	private static final int COLOR_MIDI = 1; // white
	private static final int OVERRUN_CHAR_INDEX = 24 * 40;
	public static final RawAddress OVERRUN_INDICATOR = RawAddress
			.named(0x400 + OVERRUN_CHAR_INDEX);
	private static final RawAddress OVERRUN_INDICATOR_COLOR_ADDR = RawAddress
			.named(55296 + OVERRUN_CHAR_INDEX);
	public static final Label START = Label.named("MidiAndRasterPoller");
	public static final Label JSR_RASTER_ROUTINE = START
			.subLabel("_jsrRasterRoutine");
	private final Label handleRaster;
	private final Label handleMidi;

	public MidiAndRasterPoller(C64AssyLangOf<?, MockodoreProgram> out,
			Label handleRaster, Label handleMidi) {
		super(out);
		this.handleRaster = handleRaster;
		this.handleMidi = handleMidi;
	}

	public void def() {
		Label topLoop = Label.named("topLoop");
		Label bottomLoop = Label.named("bottomLoop");

		lda(CLabMidi.ACIA_RESET).sta(CLabMidi.MIDI_CTRL);
		lda(CLabMidi.ACIA_ENABLE_WITH_NO_IRQ).sta(CLabMidi.MIDI_CTRL);
		lda(2).sta(OVERRUN_INDICATOR_COLOR_ADDR); // red
		lda(0x20).sta(OVERRUN_INDICATOR);

		label(START);

		label(topLoop);
		pollAndHandleMidi(topLoop);
		lda(CURRENT_RASTER_LINE);
		cmp(RASTER_TRIGGER);
		bcc(topLoop);

		setBgColor(COLOR_RASTER);
		label(JSR_RASTER_ROUTINE);
		jsr(handleRaster);
		setBgColor(COLOR_DEF);

		label(bottomLoop);
		pollAndHandleMidi(bottomLoop);
		lda(CURRENT_RASTER_LINE);
		cmp(RASTER_TRIGGER);
		bcs(bottomLoop);

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
