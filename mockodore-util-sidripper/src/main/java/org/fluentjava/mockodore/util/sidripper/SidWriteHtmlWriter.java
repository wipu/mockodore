package org.fluentjava.mockodore.util.sidripper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteHtmlWriter
		implements SidWriteVisualizerListener, SidWriteListener {

	private final List<Frame> frames = new ArrayList<>();
	private Frame currentFrame;

	public SidWriteHtmlWriter() {
		playCallStarting(); // ensure currentFrame
	}

	private class Frame {

		private final Map<SidRegisterAddress, UnsignedByte> regValues = new HashMap<>();
		private final List<OscFrame> oscFrames = new ArrayList<>();

		void oscPlottedAt(OscName osc, int x, int y) {
			oscFrames.add(new OscFrame(osc, x, y));
		}

		class OscFrame {

			private final OscName osc;
			private final int x;
			private final int y;

			public OscFrame(OscName osc, int x, int y) {
				this.osc = osc;
				this.x = x;
				this.y = y;
			}

			private String asTooltip() {
				SidRegisterAddress cr = SidRegisterAddress.of(osc,
						OscRegisterName.CR);
				UnsignedByte crValue = regValues.get(cr);
				String crString = crValue == null ? "-"
						: SidWritePrettyLogger
								.sidControlRegisterString(crValue);
				return y + " " + osc.name() + ":" + crString;
			}

			public String asHtmlArea() {
				return "      <area shape=\"rect\" coords=\"" + x + "," + y
						+ "," + (x + 1) + "," + (y + 1) + "\" title=\""
						+ asTooltip() + "\" href=\".\"/>\n";
			}

		}

	}

	public String toHtml(File gifFile) {
		StringBuilder b = new StringBuilder();
		b.append("<html>\n");
		b.append("  <head>\n");
		b.append("    <meta charset=\"UTF-8\">\n");
		b.append("    <title>" + gifFile.getName() + "</title>");
		b.append("    <style>\n");
		// from
		// https://stackoverflow.com/questions/14068103/disable-antialising-when-scaling-images
		b.append("      img { \n");
		b.append("        image-rendering: optimizeSpeed;\n");
		b.append("        image-rendering: -moz-crisp-edges;\n");
		b.append("        image-rendering: -o-crisp-edges;\n");
		b.append("        image-rendering: -webkit-optimize-contrast;\n");
		b.append("        image-rendering: pixelated;\n");
		b.append("        image-rendering: optimize-contrast;\n");
		b.append("        -ms-interpolation-mode: nearest-neighbor;\n");
		b.append("      }\n");
		b.append("    </style>\n");
		b.append("  </head>\n");
		b.append("  <body>\n");
		b.append("    <img src=\"" + gifFile.getName()
				+ "\" usemap=\"#themap\" />\n");
		b.append("    <map name=\"themap\" id=\"themap\">\n");
		for (Frame frame : frames) {
			for (Frame.OscFrame oscFrame : frame.oscFrames) {
				b.append(oscFrame.asHtmlArea());
			}
		}
		// b.append(
		// " <area shape=\"rect\" coords=\"x,y,0,0\" title=\"T\" href=\".\"/>");
		b.append("    </map>\n");
		b.append("  </body>\n");
		b.append("</html>\n");
		return b.toString();
	}

	@Override
	public void oscPlottedAt(OscName osc, int x, int y) {
		currentFrame.oscPlottedAt(osc, x, y);
	}

	@Override
	public void playCallStarting() {
		currentFrame = new Frame();
		frames.add(currentFrame);
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> currentFrame.regValues.put(reg, v);
	}

}
