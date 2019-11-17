package org.fluentjava.mockodore.util.sidripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;

public class SidWriteRipper extends C64SimulatorEventListenerProxy {

	private final SidWriteListener sid;
	private final StringBuilder sidWriteLog;
	private final SidWriteVisualizer visualizer;
	private final SidWritesToMidiSysex toMidi;
	private final SidWriteHtmlWriter htmlWriter;

	public static SidWriteRipper using(C64SimulatorEventListener delegate,
			int frameCount) {
		C64SimulatorEventListener mem = new ReadBeforeWriterWarner(delegate);
		mem = new StatusRegisterUpdateChecker(mem);
		SidWriteHtmlWriter htmlWriter = new SidWriteHtmlWriter();
		SidWriteVisualizer visualizer = new SidWriteVisualizer(frameCount,
				htmlWriter);
		StringBuilder sidWriteLog = new StringBuilder();
		SidWritePrettyLogger prettyLogger = new SidWritePrettyLogger(
				sidWriteLog);
		SidWritesToMidiSysex toMidi = new SidWritesToMidiSysex();
		SidWriteListener sid = SidWriteListenerHub.delegatingTo(visualizer,
				prettyLogger, toMidi, htmlWriter);
		SidWriteDelegator sidDelegator = new SidWriteDelegator(mem, sid);
		return new SidWriteRipper(sidDelegator, sid, sidWriteLog, visualizer,
				toMidi, htmlWriter);
	}

	public SidWriteRipper(C64SimulatorEventListener delegate,
			SidWriteListener sid, StringBuilder sidWriteLog,
			SidWriteVisualizer visualizer, SidWritesToMidiSysex toMidi,
			SidWriteHtmlWriter htmlWriter) {
		super(delegate);
		this.sid = sid;
		this.sidWriteLog = sidWriteLog;
		this.visualizer = visualizer;
		this.toMidi = toMidi;
		this.htmlWriter = htmlWriter;

	}

	public void playCallStarting() {
		sid.playCallStarting();
	}

	public String sidWriteLog() {
		return sidWriteLog.toString();
	}

	public void writeGifTo(File file) throws IOException {
		BufferedImage image = visualizer.resultImage();
		System.err.println("Writing " + file);
		ImageIO.write(image, "gif", file);
		System.err.println("Wrote " + file);
	}

	public String toHtml(File gifFile) {
		return htmlWriter.toHtml(gifFile);
	}

	public byte[] toMidiSysex() {
		return toMidi.toMidiseq().asBytes();
	}

}
