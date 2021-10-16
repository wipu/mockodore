package org.fluentjava.mockodore.util.sidripper;

import java.util.Arrays;
import java.util.List;

import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteListenerHub implements SidWriteListener {

	private final List<SidWriteListener> delegates;

	private SidWriteListenerHub(List<SidWriteListener> delegates) {
		this.delegates = delegates;
	}

	public static SidWriteListener delegatingTo(SidWriteListener... delegates) {
		return new SidWriteListenerHub(Arrays.asList(delegates));
	}

	@Override
	public void playCallStarting() {
		delegates.stream().forEach(d -> d.playCallStarting());
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> delegates.stream().forEach(d -> d.reg(reg).write(v));
	}

}
