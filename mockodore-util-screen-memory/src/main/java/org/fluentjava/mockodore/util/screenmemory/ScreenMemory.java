package org.fluentjava.mockodore.util.screenmemory;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.simulator.C64Simulator;
import org.junit.Assert;

public class ScreenMemory {

	public static final RawAddress START_ADDRESS = RawAddress.named(0x0400);
	public static final int COLUMNS = 40;
	public static final int ROWS = 25;

	public static ScreenMemoryOfSimulator of(C64Simulator sim) {
		return new ScreenMemoryOfSimulator(sim);
	}

	public static class ScreenMemoryOfSimulator {

		private final C64Simulator sim;

		public ScreenMemoryOfSimulator(C64Simulator sim) {
			this.sim = sim;
		}

		public void asStringShallBe(String expected) {
			Assert.assertEquals(expected, asString());
		}

		public String asString() {
			StringBuilder b = new StringBuilder();
			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLUMNS; c++) {
					int index = COLUMNS * r + c;
					UnsignedByte value = sim.valueIn(START_ADDRESS.plus(index));
					b.append(screenCodeAsChar(value));
				}
				b.append("\n");
			}
			return b.toString();
		}

	}

	public static char screenCodeAsChar(UnsignedByte b) {
		int i = b.uInt();
		if (0 <= i && i <= 31) {
			return (char) (i + 64);
		}
		if (i == 32) {
			return ' ';
		}
		if (i == 0x24) {
			return '$';
		}
		return '\u2620'; // skull and bones for unsupported
	}

}
