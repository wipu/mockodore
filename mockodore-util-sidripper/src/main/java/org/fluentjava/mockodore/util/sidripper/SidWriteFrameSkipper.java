package org.fluentjava.mockodore.util.sidripper;

import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteFrameSkipper implements SidWriteListener {

	private final SidWriteListener finalDelegate;
	private final SidWriteListenerMemImpl initialDelegate;
	private SidWriteListener d;
	private int framesToSkip;

	public SidWriteFrameSkipper(SidWriteListener d, int framesToSkip) {
		this.finalDelegate = d;
		if (framesToSkip > 0) {
			// initial delegate is gathering register values
			this.initialDelegate = new SidWriteListenerMemImpl();
			this.d = initialDelegate;
		} else {
			this.initialDelegate = null;
			this.d = d;
		}
		this.framesToSkip = framesToSkip;
	}

	@Override
	public void playCallStarting() {
		if (d != finalDelegate) {
			if (framesToSkip-- <= 0) {
				d = finalDelegate;
				d.playCallStarting();
				// TODO should we write before play call?
				// (meaning listener should have a init call, too like sid
				// routines
				// have)
				List<UnsignedByte> initialValues = initialDelegate
						.registerValues();
				for (SidRegisterAddress reg : SidRegisterAddress.all()) {
					finalDelegate.reg(reg)
							.write(initialValues.get(reg.offsetInSid()));
				}
				return;
			}
		}
		d.playCallStarting();
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return d.reg(reg);
	}

}
