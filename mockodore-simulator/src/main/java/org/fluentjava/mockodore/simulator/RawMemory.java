package org.fluentjava.mockodore.simulator;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class RawMemory implements Memory {

	private final UnsignedByte[] mem = new UnsignedByte[65536];

	public RawMemory() {
		for (int i = 0; i < mem.length; i++) {
			mem[i] = UnsignedByte.$00;
		}
	}

	@Override
	public void write(int addr, UnsignedByte value) {
		mem[addr] = value;
	}

	@Override
	public UnsignedByte read(int addr) {
		return mem[addr];
	}

}
