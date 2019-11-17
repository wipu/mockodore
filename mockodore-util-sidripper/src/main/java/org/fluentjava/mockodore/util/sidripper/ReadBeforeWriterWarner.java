package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;

public class ReadBeforeWriterWarner extends C64SimulatorEventListenerProxy {

	private final boolean[] addrsWrittenByUser = new boolean[65536];

	public ReadBeforeWriterWarner(C64SimulatorEventListener delegate) {
		super(delegate);
	}

	@Override
	public void loadPrg(PrgBytesWithLoadAddress prg) {
		super.loadPrg(prg);
		registerWritesByPrgLoad(prg);
	}

	private void registerWritesByPrgLoad(PrgBytesWithLoadAddress prg) {
		RawAddress startAddress = prg.address();
		int destAddress = startAddress.value();
		for (int i = 0; i < prg.justPrgBytes().length; i++) {
			addrsWrittenByUser[destAddress + i] = true;
		}
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		super.writeMem(address, newValue);
		addrsWrittenByUser[address] = true;
	}

	@Override
	public void readMem(int address, UnsignedByte value) {
		super.readMem(address, value);
		if (!addrsWrittenByUser[address]) {
			warning("address " + RawAddress.named(address)
					+ " was read before writing.");
		}
	}

}
