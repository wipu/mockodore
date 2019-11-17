package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class ConvenientSidWriteListener implements SidWriteListener {

	private SidWriteListener d;

	public ConvenientSidWriteListener(SidWriteListener d) {
		this.d = d;
	}

	@Override
	public void playCallStarting() {
		d.playCallStarting();
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return d.reg(reg);
	}

	@Override
	public SidRegWriteListener fcLo() {
		return d.fcLo();
	}

	@Override
	public SidRegWriteListener fcHi() {
		return d.fcHi();
	}

	@Override
	public SidRegWriteListener modeVol() {
		return d.modeVol();
	}

	@Override
	public SidRegWriteListener resFilt() {
		return d.resFilt();
	}

	@Override
	public String toString() {
		return d.toString();
	}

	public SidRegWriteListener cr1() {
		return cr(OscName.OSC_1);
	}

	public SidRegWriteListener cr2() {
		return cr(OscName.OSC_2);
	}

	public SidRegWriteListener cr3() {
		return cr(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return d.cr(osc);
	}

	@Override
	public SidRegWriteListener freqLo(OscName osc) {
		return d.freqLo(osc);
	}

	public SidRegWriteListener freqLo1() {
		return freqLo(OscName.OSC_1);
	}

	public SidRegWriteListener freqLo2() {
		return freqLo(OscName.OSC_2);
	}

	public SidRegWriteListener freqLo3() {
		return freqLo(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return d.freqHi(osc);
	}

	public SidRegWriteListener freqHi1() {
		return freqHi(OscName.OSC_1);
	}

	public SidRegWriteListener freqHi2() {
		return freqHi(OscName.OSC_2);
	}

	public SidRegWriteListener freqHi3() {
		return freqHi(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener pwLo(OscName osc) {
		return d.pwLo(osc);
	}

	public SidRegWriteListener pwLo1() {
		return pwLo(OscName.OSC_1);
	}

	public SidRegWriteListener pwLo2() {
		return pwLo(OscName.OSC_2);
	}

	public SidRegWriteListener pwLo3() {
		return pwLo(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener pwHi(OscName osc) {
		return d.pwHi(osc);
	}

	public SidRegWriteListener pwHi1() {
		return pwHi(OscName.OSC_1);
	}

	public SidRegWriteListener pwHi2() {
		return pwHi(OscName.OSC_2);
	}

	public SidRegWriteListener pwHi3() {
		return pwHi(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener ad(OscName osc) {
		return d.ad(osc);
	}

	public SidRegWriteListener ad1() {
		return ad(OscName.OSC_1);
	}

	public SidRegWriteListener ad2() {
		return ad(OscName.OSC_2);
	}

	public SidRegWriteListener ad3() {
		return ad(OscName.OSC_3);
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return d.sr(osc);
	}

	public SidRegWriteListener sr1() {
		return sr(OscName.OSC_1);
	}

	public SidRegWriteListener sr2() {
		return sr(OscName.OSC_2);
	}

	public SidRegWriteListener sr3() {
		return sr(OscName.OSC_3);
	}

}
