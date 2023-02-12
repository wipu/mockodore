package org.fluentjava.mockodore.util.sidripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

import javax.imageio.ImageIO;

import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;

public class SidWriteRipper extends C64SimulatorEventListenerProxy {

	private final SidWriteListener sid;
	private final StringBuilder sidWriteLog;
	private final SidWriteVisualizer visualizer;
	private final SidWritesToMidiSysex toMidi;
	private final SidWriteHtmlWriter htmlWriter;
	private final FullFrameHexDumper fullFrameHexDumper;
	private final SidRegisterWriteOrderAnalyzer writeOrderAnalyzer;

	public static SidWriteRipper using(C64SimulatorEventListener delegate,
			int framesToSkip, int frameCount) {
		C64SimulatorEventListener mem = new ReadBeforeWriterWarner(delegate);
		mem = new StatusRegisterUpdateChecker(mem);
		SidWriteHtmlWriter htmlWriter = new SidWriteHtmlWriter();
		SidWriteVisualizer visualizer = new SidWriteVisualizer(
				frameCount - framesToSkip, htmlWriter);
		StringBuilder sidWriteLog = new StringBuilder();
		SidWritePrettyLogger prettyLogger = new SidWritePrettyLogger(
				sidWriteLog);
		FullFrameHexDumper fullFrameHexDumper = new FullFrameHexDumper();
		SidWritesToMidiSysex toMidi = new SidWritesToMidiSysex();

		// the frame skipper will dump all registers, which would confuse the
		// write order analyzer, so let it skip that frame:
		int numberOfInitialFramesToIgnoreWhenAnalyzingWriteOrder = 1;
		SidRegisterWriteOrderAnalyzer writeOrderAnalyzer = new SidRegisterWriteOrderAnalyzer(
				numberOfInitialFramesToIgnoreWhenAnalyzingWriteOrder);

		SidWriteListener sid = SidWriteListenerHub.delegatingTo(visualizer,
				prettyLogger, toMidi, htmlWriter, fullFrameHexDumper,
				writeOrderAnalyzer);
		sid = new SidWriteFrameSkipper(sid, framesToSkip);
		SidWriteDelegator sidDelegator = new SidWriteDelegator(mem, sid);
		return new SidWriteRipper(sidDelegator, sid, sidWriteLog, visualizer,
				toMidi, htmlWriter, fullFrameHexDumper, writeOrderAnalyzer);
	}

	private SidWriteRipper(C64SimulatorEventListener delegate,
			SidWriteListener sid, StringBuilder sidWriteLog,
			SidWriteVisualizer visualizer, SidWritesToMidiSysex toMidi,
			SidWriteHtmlWriter htmlWriter,
			FullFrameHexDumper fullFrameHexDumper,
			SidRegisterWriteOrderAnalyzer writeOrderAnalyzer) {
		super(delegate);
		this.sid = sid;
		this.sidWriteLog = sidWriteLog;
		this.visualizer = visualizer;
		this.toMidi = toMidi;
		this.htmlWriter = htmlWriter;
		this.fullFrameHexDumper = fullFrameHexDumper;
		this.writeOrderAnalyzer = writeOrderAnalyzer;
	}

	public void playCallStarting() {
		sid.playCallStarting();
	}

	public String sidWriteLog() {
		return sidWriteLog.toString();
	}

	public String fullFrameHexDump() {
		return fullFrameHexDumper.toString();
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

	public String sidWriteOrderAnalysis() {
		SortedSet<String> contradictions = writeOrderAnalyzer.contradictions();
		if (!contradictions.isEmpty()) {
			return "contradictions: " + contradictions;
		}
		return writeOrderAnalyzer.commonOrderOfRegisters().toString();
	}

}
