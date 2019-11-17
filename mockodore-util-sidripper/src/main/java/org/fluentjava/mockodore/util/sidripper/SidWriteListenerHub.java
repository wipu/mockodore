package org.fluentjava.mockodore.util.sidripper;

import java.util.Arrays;
import java.util.List;

import org.fluentjava.mockodore.model.sid.OscName;
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

	@Override
	public SidRegWriteListener ad(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.ad(osc).write(v));
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.sr(osc).write(v));
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.cr(osc).write(v));
	}

	@Override
	public SidRegWriteListener fcLo() {
		return (v) -> delegates.stream().forEach(d -> d.fcLo().write(v));
	}

	@Override
	public SidRegWriteListener fcHi() {
		return (v) -> delegates.stream().forEach(d -> d.fcHi().write(v));
	}

	@Override
	public SidRegWriteListener freqLo(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.freqLo(osc).write(v));
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.freqHi(osc).write(v));
	}

	@Override
	public SidRegWriteListener modeVol() {
		return (v) -> delegates.stream().forEach(d -> d.modeVol().write(v));
	}

	@Override
	public SidRegWriteListener pwLo(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.pwLo(osc).write(v));
	}

	@Override
	public SidRegWriteListener pwHi(OscName osc) {
		return (v) -> delegates.stream().forEach(d -> d.pwHi(osc).write(v));
	}

	@Override
	public SidRegWriteListener resFilt() {
		return (v) -> delegates.stream().forEach(d -> d.resFilt().write(v));
	}

}
