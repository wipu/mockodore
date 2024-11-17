package org.fluentjava.mockodore.model.sid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.junit.jupiter.api.Test;

public class WaveformTest {

	@Test
	public void individualWaveformValues() {
		assertEquals(UnsignedByte.x80, Waveform.NOISE.value());
		assertEquals(UnsignedByte.x40, Waveform.PULSE.value());
		assertEquals(UnsignedByte.x20, Waveform.SAW.value());
		assertEquals(UnsignedByte.x10, Waveform.TRIANGLE.value());
	}

	@Test
	public void individualWaveformToStrings() {
		assertEquals("NOISE", Waveform.NOISE.toString());
		assertEquals("PULSE", Waveform.PULSE.toString());
		assertEquals("SAW", Waveform.SAW.toString());
		assertEquals("TRIANGLE", Waveform.TRIANGLE.toString());
	}

	@Test
	public void orValue() {
		assertEquals(UnsignedByte.x60, Waveform.PULSE.or(Waveform.SAW).value());
	}

	@Test
	public void orToStringMentionsAtomsInOrder() {
		assertEquals("PULSE|SAW", Waveform.PULSE.or(Waveform.SAW).toString());
		assertEquals("PULSE|SAW", Waveform.SAW.or(Waveform.PULSE).toString());

		assertEquals("NOISE|SAW|TRIANGLE", Waveform.SAW.or(Waveform.TRIANGLE)
				.or(Waveform.NOISE).toString());
	}

	@Test
	public void allWaveformsValue() {
		assertEquals(UnsignedByte.xF0, Waveform.ALL.value());
	}

	@Test
	public void allWaveformsToString() {
		assertEquals("NOISE|PULSE|SAW|TRIANGLE", Waveform.ALL.toString());
	}

}
