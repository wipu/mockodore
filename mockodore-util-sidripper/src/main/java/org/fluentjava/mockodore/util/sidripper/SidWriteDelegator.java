package org.fluentjava.mockodore.util.sidripper;

import java.util.HashMap;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteDelegator extends C64SimulatorEventListenerProxy {

	private static final OscName OSC_1 = OscName.OSC_1;
	private static final OscName OSC_2 = OscName.OSC_2;
	private static final OscName OSC_3 = OscName.OSC_3;
	private final SidWriteListener sid;
	private final Map<Integer, SidRegisterWriter> sidRegsByAddr = new HashMap<>();

	public SidWriteDelegator(C64SimulatorEventListener mem,
			SidWriteListener sid) {
		super(mem);
		this.sid = sid;
		ad(OSC_1);
		ad(OSC_2);
		ad(OSC_3);
		cr(OSC_1);
		cr(OSC_2);
		cr(OSC_3);
		fcLo();
		fcHi();
		freqLo(OSC_1);
		freqLo(OSC_2);
		freqLo(OSC_3);
		freqHi(OSC_1);
		freqHi(OSC_2);
		freqHi(OSC_3);
		modeVol();
		pwLo(OSC_1);
		pwLo(OSC_2);
		pwLo(OSC_3);
		pwHi(OSC_1);
		pwHi(OSC_2);
		pwHi(OSC_3);
		resFilt();
		sr(OSC_1);
		sr(OSC_2);
		sr(OSC_3);
	}

	private void ad(OscName osc) {
		reg(osc, OscRegisterName.AD, (v) -> sid.ad(osc).write(v));
	}

	private void cr(OscName osc) {
		reg(osc, OscRegisterName.CR, (v) -> sid.cr(osc).write(v));
	}

	private void fcLo() {
		reg(SidRegisterAddress.FCLO, (v) -> sid.fcLo().write(v));
	}

	private void fcHi() {
		reg(SidRegisterAddress.FCHI, (v) -> sid.fcHi().write(v));
	}

	private void freqLo(OscName osc) {
		reg(osc, OscRegisterName.FREQ_LO, (v) -> sid.freqLo(osc).write(v));
	}

	private void freqHi(OscName osc) {
		reg(osc, OscRegisterName.FREQ_HI, (v) -> sid.freqHi(osc).write(v));
	}

	private void modeVol() {
		reg(SidRegisterAddress.MODE_VOL, (v) -> sid.modeVol().write(v));
	}

	private void pwLo(OscName osc) {
		reg(osc, OscRegisterName.PW_LO, (v) -> sid.pwLo(osc).write(v));
	}

	private void pwHi(OscName osc) {
		reg(osc, OscRegisterName.PW_HI, (v) -> sid.pwHi(osc).write(v));
	}

	private void resFilt() {
		reg(SidRegisterAddress.RES_FILT, (v) -> sid.resFilt().write(v));
	}

	private void sr(OscName osc) {
		reg(osc, OscRegisterName.SR, (v) -> sid.sr(osc).write(v));
	}

	private void reg(OscName osc, OscRegisterName oscReg,
			SidRegisterWriter writer) {
		sidRegsByAddr.put(SidRegisterAddress.of(osc, oscReg).address().value(),
				writer);
	}

	private void reg(SidRegisterAddress reg, SidRegisterWriter writer) {
		sidRegsByAddr.put(reg.address().value(), writer);
	}

	@FunctionalInterface
	private interface SidRegisterWriter {

		void write(UnsignedByte newValue);

	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		super.writeMem(address, newValue);
		SidRegisterWriter sidReg = sidRegsByAddr.get(address);
		if (sidReg != null) {
			sidReg.write(newValue);
		}
	}

}
