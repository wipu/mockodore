package org.fluentjava.mockodore.util.sidripper;

import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class FullFrameHexDumper implements SidWriteListener {

	private final List<UnsignedByte> sid;
	private final StringBuilder log = new StringBuilder();

	public FullFrameHexDumper() {
		this.sid = new ArrayList<>();
		for (int i = 0; i < SidRegisterAddress.all().size(); i++) {
			sid.add(UnsignedByte.x00);
		}
	}

	public FullFrameHexDumper(List<UnsignedByte> sid) {
		if (sid.size() != SidRegisterAddress.all().size()) {
			throw new IllegalArgumentException(
					"Wrong size of sid register list: " + sid.size());
		}
		this.sid = sid;
	}

	@Override
	public void playCallStarting() {
		for (UnsignedByte v : sid) {
			ByteArrayPrettyPrinter.append(log, v.signedByte());
		}
		log.append("\n");
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
		sid.set(reg.offsetInSid(), value);
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
	public SidRegWriteListener cr(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.CR));
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
	public SidRegWriteListener freqLo(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_LO));
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_HI));
	}

	@Override
	public SidRegWriteListener modeVol() {
		return reg(SidRegisterAddress.MODE_VOL);
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
	public SidRegWriteListener resFilt() {
		return reg(SidRegisterAddress.RES_FILT);
	}

}
