package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteCompactLogger implements SidWriteListener {

	private final StringBuilder log = new StringBuilder();
	private final String regWriteSeparator;

	public SidWriteCompactLogger() {
		this("\n");
	}

	public SidWriteCompactLogger(String regWriteSeparator) {
		this.regWriteSeparator = regWriteSeparator;
	}

	@Override
	public String toString() {
		return log.toString();
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> write(reg, v);
	}

	private void write(SidRegisterAddress reg, UnsignedByte value) {
		log.append(simpleHex(UnsignedByte.from(reg.address().lsb())));
		log.append("=");
		log.append(simpleHex(value));
		log.append(regWriteSeparator);
	}

	private static String simpleHex(UnsignedByte b) {
		return b.toString().replace("#$", "");
	}

	@Override
	public void playCallStarting() {
		log.append("\n");
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.CR));
	}

	@Override
	public SidRegWriteListener freqLo(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_LO));
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_HI));
	}

	@Override
	public SidRegWriteListener pwLo(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.PW_LO));
	}

	@Override
	public SidRegWriteListener pwHi(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.PW_HI));
	}

	@Override
	public SidRegWriteListener ad(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.AD));
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.SR));
	}

	@Override
	public SidRegWriteListener fcLo() {
		return reg(SidRegisterAddress.FCLO);
	}

	@Override
	public SidRegWriteListener fcHi() {
		return reg(SidRegisterAddress.FCHI);
	}

	@Override
	public SidRegWriteListener modeVol() {
		return reg(SidRegisterAddress.MODE_VOL);
	}

	@Override
	public SidRegWriteListener resFilt() {
		return reg(SidRegisterAddress.RES_FILT);
	}

}
