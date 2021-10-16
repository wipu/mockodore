package org.fluentjava.mockodore.util.sidripper;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteListenerMemImpl implements SidWriteListener {

	private final List<UnsignedByte> sid;

	public SidWriteListenerMemImpl() {
		sid = new ArrayList<>();
		for (int i = 0; i < SidRegisterAddress.all().size(); i++) {
			sid.add(UnsignedByte.x00);
		}
	}

	public List<UnsignedByte> registerValues() {
		return sid;
	}

	@Override
	public void playCallStarting() {
		// nothing to do
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> sid.set(reg.offsetInSid(), v);
	}

}
