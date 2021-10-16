package org.fluentjava.mockodore.util.sidripper;

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

	public SidRegWriteListener fcLo() {
		return reg(SidRegisterAddress.FCLO);
	}

	public SidRegWriteListener fcHi() {
		return reg(SidRegisterAddress.FCHI);
	}

	public SidRegWriteListener modeVol() {
		return reg(SidRegisterAddress.MODE_VOL);
	}

	public SidRegWriteListener resFilt() {
		return reg(SidRegisterAddress.RES_FILT);
	}

	@Override
	public String toString() {
		return d.toString();
	}

	public SidRegWriteListener cr1() {
		return reg(SidRegisterAddress.CR_1);
	}

	public SidRegWriteListener cr2() {
		return reg(SidRegisterAddress.CR_2);
	}

	public SidRegWriteListener cr3() {
		return reg(SidRegisterAddress.CR_3);
	}

	public SidRegWriteListener freqLo1() {
		return reg(SidRegisterAddress.FREQ_LO_1);
	}

	public SidRegWriteListener freqLo2() {
		return reg(SidRegisterAddress.FREQ_LO_2);
	}

	public SidRegWriteListener freqLo3() {
		return reg(SidRegisterAddress.FREQ_LO_3);
	}

	public SidRegWriteListener freqHi1() {
		return reg(SidRegisterAddress.FREQ_HI_1);
	}

	public SidRegWriteListener freqHi2() {
		return reg(SidRegisterAddress.FREQ_HI_2);
	}

	public SidRegWriteListener freqHi3() {
		return reg(SidRegisterAddress.FREQ_HI_3);
	}

	public SidRegWriteListener pwLo1() {
		return reg(SidRegisterAddress.PW_LO_1);
	}

	public SidRegWriteListener pwLo2() {
		return reg(SidRegisterAddress.PW_LO_2);
	}

	public SidRegWriteListener pwLo3() {
		return reg(SidRegisterAddress.PW_LO_3);
	}

	public SidRegWriteListener pwHi1() {
		return reg(SidRegisterAddress.PW_HI_1);
	}

	public SidRegWriteListener pwHi2() {
		return reg(SidRegisterAddress.PW_HI_2);
	}

	public SidRegWriteListener pwHi3() {
		return reg(SidRegisterAddress.PW_HI_3);
	}

	public SidRegWriteListener ad1() {
		return reg(SidRegisterAddress.AD_1);
	}

	public SidRegWriteListener ad2() {
		return reg(SidRegisterAddress.AD_2);
	}

	public SidRegWriteListener ad3() {
		return reg(SidRegisterAddress.AD_3);
	}

	public SidRegWriteListener sr1() {
		return reg(SidRegisterAddress.SR_1);
	}

	public SidRegWriteListener sr2() {
		return reg(SidRegisterAddress.SR_2);
	}

	public SidRegWriteListener sr3() {
		return reg(SidRegisterAddress.SR_3);
	}

}
