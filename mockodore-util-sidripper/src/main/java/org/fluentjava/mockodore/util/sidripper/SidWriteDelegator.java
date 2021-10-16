package org.fluentjava.mockodore.util.sidripper;

import java.util.HashMap;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteDelegator extends C64SimulatorEventListenerProxy {

	private final Map<Integer, SidRegWriteListener> sidRegsByAddr = new HashMap<>();

	public SidWriteDelegator(C64SimulatorEventListener mem,
			SidWriteListener sid) {
		super(mem);
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			sidRegsByAddr.put(reg.address().value(),
					(v) -> sid.reg(reg).write(v));
		}
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		super.writeMem(address, newValue);
		SidRegWriteListener sidReg = sidRegsByAddr.get(address);
		if (sidReg != null) {
			sidReg.write(newValue);
		}
	}

}
