package org.fluentjava.mockodore.util.sidripper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.midievents.Midievents;
import org.fluentjava.joulu.midievents.Midievents.MidieventsPlease;
import org.fluentjava.joulu.midievents.Midiseq;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.fluentjava.mockodore.model.sid.SidRegisterAddresses;
import org.fluentjava.mockodore.util.sysex.SysexEncoder;

public class SidWritesToMidiSysex implements SidWriteListener {

	private final MidieventsPlease e;
	private final Map<SidRegisterAddress, UnsignedByte> regValues = new HashMap<>();
	public static final int TICKS_PER_FRAME = 4; // TODO correct value;

	public SidWritesToMidiSysex() {
		e = Midievents.usingDefaults();
		for (SidRegisterAddress reg : SidRegisterAddress.all()) {
			regValues.put(reg, UnsignedByte.$00);
		}
	}

	@Override
	public void playCallStarting() {
		List<UnsignedByte> msg = new ArrayList<>();
		msg.add(UnsignedByte.$F0);
		msg.add(UnsignedByte.$44); // Casio

		List<UnsignedByte> rawValues = new ArrayList<>();
		for (SidRegisterAddress reg : SidRegisterAddresses
				.allInDefaultWritingOrder()) {
			rawValues.add(regValues.get(reg));
		}
		rawValues.add(UnsignedByte.$00); // padding
		rawValues.add(UnsignedByte.$00);
		rawValues.add(UnsignedByte.$00); // padding
		msg.addAll(SysexEncoder.sysexEncoded(rawValues));

		msg.add(UnsignedByte.$F7);

		e.after(TICKS_PER_FRAME);
		byte[] bytes = new byte[msg.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = msg.get(i).signedByte();
		}
		e.sysex(bytes);
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> regValues.put(reg, v);
	}

	@Override
	public SidRegWriteListener ad(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.AD))
				.write(v);
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.SR))
				.write(v);
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.CR))
				.write(v);
	}

	@Override
	public SidRegWriteListener fcLo() {
		return (v) -> reg(SidRegisterAddress.FCLO).write(v);
	}

	@Override
	public SidRegWriteListener fcHi() {
		return (v) -> reg(SidRegisterAddress.FCHI).write(v);
	}

	@Override
	public SidRegWriteListener freqLo(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_LO))
				.write(v);
	}

	@Override
	public SidRegWriteListener freqHi(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.FREQ_HI))
				.write(v);
	}

	@Override
	public SidRegWriteListener modeVol() {
		return (v) -> reg(SidRegisterAddress.MODE_VOL).write(v);
	}

	@Override
	public SidRegWriteListener pwLo(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.PW_LO))
				.write(v);
	}

	@Override
	public SidRegWriteListener pwHi(OscName osc) {
		return (v) -> reg(SidRegisterAddress.of(osc, OscRegisterName.PW_HI))
				.write(v);
	}

	@Override
	public SidRegWriteListener resFilt() {
		return (v) -> reg(SidRegisterAddress.RES_FILT).write(v);
	}

	public Midiseq toMidiseq() {
		playCallStarting();
		return Midiseq.usingDefaults().events(e.end()).end();
	}

}
