package org.fluentjava.mockodore.util.sidripper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.ControlRegister;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteVisualizer implements SidWriteListener {

	private static final int[] BLACK = new int[] { 0, 0, 0 };
	@SuppressWarnings("unused")
	private static final int[] CYAN_LIGHT = new int[] { 200, 255, 255 };
	private static final int[] GRAY_DARK = new int[] { 100, 100, 100 };
	private static final int[] GRAY_LIGHT = new int[] { 210, 210, 210 };
	private static final int[] GRAY_LIGHTEST = new int[] { 240, 240, 240 };
	private static final int[] GREEN = new int[] { 0, 255, 0 };
	private static final int[] RED = new int[] { 255, 50, 50 };
	private static final int[] WHITE = new int[] { 255, 255, 255 };

	private static final int[] SYNC = RED;
	private static final int[] UNKNOWN = GRAY_LIGHT;
	private static final int[] NOISE_COLOR = GRAY_DARK;
	private static final int[] SAW_COLOR = RED;
	private static final int[] TRIANGLE_COLOR = GREEN;

	private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);

	private static final int WIDTH = 1024;
	private final int height;
	private final SidWriteVisualizerListener listener;
	private final BufferedImage image;
	private final WritableRaster raster;
	private final UnsignedByte[] freqLo = zeroBytes(3);
	private final UnsignedByte[] freqHi = zeroBytes(3);
	private final boolean[] wfNoise = new boolean[3];
	private final boolean[] wfPulse = new boolean[3];
	private final boolean[] wfSaw = new boolean[3];
	private final boolean[] wfTriangle = new boolean[3];
	private final boolean[] prevGate = new boolean[3];
	private final boolean[] gate = new boolean[3];
	private final boolean[] sync = new boolean[3];
	private final UnsignedByte[] pwLo = zeroBytes(3);
	private final UnsignedByte[] pwHi = zeroBytes(3);
	private int frameNumber = -1;

	private static UnsignedByte[] zeroBytes(int length) {
		UnsignedByte[] array = new UnsignedByte[length];
		Arrays.fill(array, UnsignedByte.$00);
		return array;
	}

	public SidWriteVisualizer(int frameCount,
			SidWriteVisualizerListener listener) {
		this.height = frameCount;
		this.listener = listener;
		image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_RGB);
		raster = image.getRaster();
		for (int y = 0; y < height; y++) {
			int[] rowColor = WHITE;
			if (y % 50 == 0) {
				rowColor = GRAY_LIGHTEST;
			}
			for (int x = 0; x < WIDTH; x++) {
				raster.setPixel(x, y, rowColor);
			}
		}
		Graphics g2d = image.getGraphics();
		g2d.setColor(Color.BLACK);
		g2d.setFont(FONT);
		for (int y = 0; y < height; y += 50) {
			int seconds = y / 50;
			int minutes = seconds / 60;
			int secondsPastMinute = seconds % 60;
			String timeString = String.format("%d:%02d", minutes,
					secondsPastMinute);
			g2d.drawString(timeString, 1, y + 10);
		}
		g2d.dispose();
		Arrays.fill(prevGate, true);
	}

	@Override
	public void playCallStarting() {
		frameNumber++;
		if (frameNumber > 0) {
			frameReady();
		}
	}

	private void frameReady() {
		int y = frameNumber - 1;
		plotOsc(0, y);
		plotOsc(1, y);
		plotOsc(2, y);
		plotGateIfNeeded(y, 0);
		plotGateIfNeeded(y, 1);
		plotGateIfNeeded(y, 2);
		plotSyncIfNeeded(y, 0);
		plotSyncIfNeeded(y, 1);
		plotSyncIfNeeded(y, 2);

		System.arraycopy(gate, 0, prevGate, 0, gate.length);

	}

	private void plotOsc(int oscIndex, int y) {
		int x = oscFreqAsXCoord(oscIndex);
		raster.setPixel(x, y, oscColor(oscIndex));
		listener.oscPlottedAt(OscName.values()[oscIndex], x, y);
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		throw new UnsupportedOperationException("TODO test and implement");
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return (v) -> cr(osc, v);
	}

	@Override
	public SidRegWriteListener freqLo(OscName osc) {
		return (v) -> freqLo[osc.ordinal()] = v;
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return (v) -> freqHi[osc.ordinal()] = v;
	}

	@Override
	public SidRegWriteListener pwLo(OscName osc) {
		return (v) -> pwLo[osc.ordinal()] = v;
	}

	@Override
	public SidRegWriteListener pwHi(OscName osc) {
		return (v) -> pwHi[osc.ordinal()] = v;
	}

	@Override
	public SidRegWriteListener ad(OscName osc) {
		return (v) -> {/* not visualized */
		};
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return (v) -> {/* not visualized */
		};
	}

	private void cr(OscName osc, UnsignedByte v) {
		ControlRegister cr = cr(v);
		wfNoise(osc, cr.isNoise());
		wfPulse(osc, cr.isPulse());
		wfSaw(osc, cr.isSaw());
		wfTriangle(osc, cr.isTriangle());
		gate(osc, cr.isGate());
		sync(osc, cr.isSync());
	}

	private static ControlRegister cr(UnsignedByte value) {
		return ControlRegister.from(value);
	}

	@Override
	public SidRegWriteListener fcHi() {
		return (v) -> {/* not visualized */
		};
	}

	@Override
	public SidRegWriteListener fcLo() {
		return (v) -> {/* not visualized */
		};
	}

	@Override
	public SidRegWriteListener resFilt() {
		return (v) -> {/* not visualized */
		};
	}

	@Override
	public SidRegWriteListener modeVol() {
		return (v) -> {/* not visualized */
		};
	}

	private void plotSyncIfNeeded(int y, int osc) {
		if (sync[osc]) {
			raster.setPixel(oscFreqAsXCoord(osc) + 1, y, SYNC);
		}
	}

	private void plotGateIfNeeded(int y, int osc) {
		if (gate[osc] && !prevGate[osc]) {
			raster.setPixel(xminus(oscFreqAsXCoord(osc), 1), y, BLACK);
		} else if (!gate[osc] && prevGate[osc]) {
			raster.setPixel(xminus(oscFreqAsXCoord(osc), 1), y, GRAY_LIGHT);
		}
	}

	private static int xminus(int x, int delta) {
		return Math.max(0, x - delta);
	}

	private int[] oscColor(int osc) {
		int[] color = new int[3];
		System.arraycopy(UNKNOWN, 0, color, 0, color.length);
		if (wfNoise[osc]) {
			color = mixed(color, NOISE_COLOR);
		}
		if (wfSaw[osc]) {
			color = mixed(color, SAW_COLOR);
		}
		if (wfTriangle[osc]) {
			color = mixed(color, TRIANGLE_COLOR);
		}
		if (wfPulse[osc]) {
			color = mixed(color, pulseColor(osc));
		}
		return color;
	}

	private static int[] mixed(int[] c1, int[] c2) {
		int[] mixed = new int[3];
		for (int i = 0; i < 3; i++) {
			mixed[i] = (c1[i] + c2[i]) / 2;
		}
		return mixed;
	}

	private int[] pulseColor(int osc) {
		int[] out = new int[3];
		double absPw = Math.abs(signedPw(osc));
		int varComponent = (int) (absPw * 255);
		out[0] = varComponent;
		out[1] = varComponent;
		out[2] = 255;
		return out;
	}

	static double log2(double v) {
		return Math.log(v) / Math.log(2);
	}

	private int oscFreqAsXCoord(int osc) {
		return freqAsXCoord(freqLo[osc], freqHi[osc]);
	}

	static int freqAsXCoord(UnsignedByte lsb, UnsignedByte msb) {
		return freqAsXCoord(intFromLsbMsb(lsb, msb));
	}

	static int freqAsXCoord(int freq) {
		if (freq <= 0) {
			return 0;
		}
		double logFreq = log2(freq + 1);
		double relFreq = logFreq / 16D;
		double x = WIDTH * relFreq;
		int xInt = (int) x;
		return Math.min(xInt, WIDTH - 1);
	}

	static int intFromLsbMsb(UnsignedByte lsb, UnsignedByte msb) {
		return msb.uInt() << 8 | lsb.uInt();
	}

	public BufferedImage resultImage() {
		return image;
	}

	private void wfNoise(OscName osc, boolean isNoise) {
		this.wfNoise[osc.ordinal()] = isNoise;
	}

	private void wfPulse(OscName osc, boolean isPulse) {
		this.wfPulse[osc.ordinal()] = isPulse;
	}

	private void wfSaw(OscName osc, boolean isSaw) {
		this.wfSaw[osc.ordinal()] = isSaw;
	}

	private void wfTriangle(OscName osc, boolean isTriangle) {
		this.wfTriangle[osc.ordinal()] = isTriangle;
	}

	private void gate(OscName osc, boolean gate) {
		this.gate[osc.ordinal()] = gate;
	}

	private void sync(OscName osc, boolean sync) {
		this.sync[osc.ordinal()] = sync;
	}

	private static final double RANGE_12BIT = 4096;

	private double signedPw(int osc) {
		return signedPw(pwLo[osc], pwHi[osc]);
	}

	static double signedPw(UnsignedByte lsb, UnsignedByte msb) {
		int pwInt = intFromLsbMsb(lsb, msb);
		double relPw = pwInt / RANGE_12BIT;
		double signedPw = 2 * relPw - 1.0D;
		return signedPw;
	}

}
