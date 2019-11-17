package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class IndexRegisterX extends Register {

	public IndexRegisterX(C64SimulatorEventListener listener) {
		super("X", listener);
	}

	@Override
	protected void logChangedValue(UnsignedByte oldValue) {
		listener.writeX(this, oldValue);
	}

}
