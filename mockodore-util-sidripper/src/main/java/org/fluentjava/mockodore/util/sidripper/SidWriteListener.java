package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

/**
 * TODO minimal interface: reg and its value. If needed, make this a convenience
 * for human callers.
 */
public interface SidWriteListener {

	void playCallStarting();

	SidRegWriteListener reg(SidRegisterAddress reg);

	SidRegWriteListener ad(OscName osc);

	SidRegWriteListener sr(OscName osc);

	SidRegWriteListener cr(OscName osc);

	SidRegWriteListener fcLo();

	SidRegWriteListener fcHi();

	SidRegWriteListener freqLo(OscName osc);

	SidRegWriteListener freqHi(OscName osc);

	SidRegWriteListener modeVol();

	SidRegWriteListener pwLo(OscName osc);

	SidRegWriteListener pwHi(OscName osc);

	SidRegWriteListener resFilt();

}
