package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class StackPointer extends Register {

	public StackPointer(C64SimulatorEventListener listener) {
		super("SP", listener);
	}

	@Override
	protected void logChangedValue(UnsignedByte oldValue) {
		listener.writeSp(this, oldValue);
	}

}
