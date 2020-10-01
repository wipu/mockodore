package org.fluentjava.mockodore.app.sidex;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.simulator.Memory;

public class MemoryWithAutoZeroMidiStatus implements Memory {

	private final Memory m;

	MemoryWithAutoZeroMidiStatus(Memory m) {
		this.m = m;
	}

	@Override
	public void write(int addr, UnsignedByte value) {
		m.write(addr, value);
	}

	@Override
	public UnsignedByte read(int addr) {
		UnsignedByte v = m.read(addr);
		if (addr == CLabMidi.MIDI_STATUS.value()) {
			m.write(addr, UnsignedByte.x00);
		}
		return v;
	}

}