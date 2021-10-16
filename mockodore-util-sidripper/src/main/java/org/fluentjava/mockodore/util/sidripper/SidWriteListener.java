package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public interface SidWriteListener {

	void playCallStarting();

	SidRegWriteListener reg(SidRegisterAddress reg);

}
