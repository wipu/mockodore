package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class IndexRegisterY extends Register {

	public IndexRegisterY(C64SimulatorEventListener listener) {
		super("Y", listener);
	}

	@Override
	protected void logChangedValue(UnsignedByte oldValue) {
		listener.writeY(this, oldValue);
	}

}
