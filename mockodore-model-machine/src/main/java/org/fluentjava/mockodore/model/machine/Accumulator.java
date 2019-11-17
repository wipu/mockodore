package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class Accumulator extends Register {

	public Accumulator(C64SimulatorEventListener listener) {
		super("A", listener);
	}

	@Override
	protected void logChangedValue(UnsignedByte oldValue) {
		listener.writeA(this, oldValue);
	}

}
