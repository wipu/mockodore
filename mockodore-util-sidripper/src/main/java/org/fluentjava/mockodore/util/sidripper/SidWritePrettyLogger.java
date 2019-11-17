package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.ControlRegister;
import org.fluentjava.mockodore.model.sid.OscName;
import org.fluentjava.mockodore.model.sid.OscRegisterName;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWritePrettyLogger implements SidWriteListener {

	private final StringBuilder sidWriteLog;
	private int frameNumber = -1;

	public SidWritePrettyLogger() {
		this(new StringBuilder());
	}

	public SidWritePrettyLogger(StringBuilder sidWriteLog) {
		this.sidWriteLog = sidWriteLog;
	}

	@Override
	public String toString() {
		return sidWriteLog.toString();
	}

	@Override
	public void playCallStarting() {
		frameNumber++;
		sidWriteLog.append("=== Frame " + frameNumber + "\n");
	}

	@Override
	public SidRegWriteListener cr(OscName osc) {
		return (v) -> cr(osc, v);
	}

	private void cr(OscName osc, UnsignedByte newValue) {
		reg(SidRegisterAddress.of(osc, OscRegisterName.CR), newValue,
				sidControlRegisterString(newValue));
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> reg(reg, v);
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
		return (v) -> ad(osc, v);
	}

	private void ad(OscName osc, UnsignedByte newValue) {
		reg(SidRegisterAddress.of(osc, OscRegisterName.AD), newValue,
				envByteString(newValue));
	}

	private static String envByteString(UnsignedByte newValue) {
		int attack = newValue.and(UnsignedByte.$F0).uInt() >> 4;
		int decay = newValue.and(UnsignedByte.$0F).uInt();
		return ByteArrayPrettyPrinter.spaceSeparatedHex((byte) attack,
				(byte) decay);
	}

	@Override
	public SidRegWriteListener sr(OscName osc) {
		return (v) -> sr(osc, v);
	}

	private void sr(OscName osc, UnsignedByte newValue) {
		reg(SidRegisterAddress.of(osc, OscRegisterName.SR), newValue,
				envByteString(newValue));
	}

	private void reg(SidRegisterAddress reg, UnsignedByte newValue,
			String newValuePrettyPrinted) {
		String sidDebugLine = indentation(reg.osc()) + reg + "="
				+ newValuePrettyPrinted + " (" + reg.address() + "=" + newValue
				+ ")\n";
		sidWriteLog.append(sidDebugLine);
	}

	private void reg(SidRegisterAddress reg, UnsignedByte newValue) {
		reg(reg, newValue, newValue.toString());
	}

	public static String sidControlRegisterString(UnsignedByte newValue) {
		ControlRegister cr = ControlRegister.from(newValue);
		StringBuilder b = new StringBuilder();
		if (cr.isNoise()) {
			b.append("NOISE");
		}
		b.append("|");
		if (cr.isPulse()) {
			b.append("PULSE");
		}
		b.append("|");
		if (cr.isSaw()) {
			b.append("SAW");
		}
		b.append("|");
		if (cr.isTriangle()) {
			b.append("TRI");
		}
		b.append("|");
		if (newValue.isBit3()) {
			b.append("TEST");
		}
		b.append("|");
		if (newValue.isBit2()) {
			b.append("RING");
		}
		b.append("|");
		if (cr.isSync()) {
			b.append("SYNC");
		}
		b.append("|");
		if (cr.isGate()) {
			b.append("GATE");
		}
		return b.toString();
	}

	private static String indentation(OscName osc) {
		StringBuilder b = new StringBuilder();
		int column = column(osc);
		for (int i = 0; i < column; i++) {
			b.append(" ");
		}
		b.append(column == 0 ? " " : column);
		for (int i = 0; i < 3 - column; i++) {
			b.append(" ");
		}
		b.append(" | ");
		return b.toString();
	}

	private static int column(OscName osc) {
		return oscColumn(osc);
	}

	private static int oscColumn(OscName osc) {
		switch (osc) {
		case GLOBAL:
			return 0;
		case OSC_1:
			return 1;
		case OSC_2:
			return 2;
		case OSC_3:
			return 3;
		case TRIGGER:
			return 4;
		default:
			throw new IllegalArgumentException("Unsupported: " + osc);
		}
	}

	@Override
	public SidRegWriteListener fcLo() {
		return (v) -> reg(SidRegisterAddress.FCLO, v);
	}

	@Override
	public SidRegWriteListener fcHi() {
		return (v) -> reg(SidRegisterAddress.FCHI, v);
	}

	@Override
	public SidRegWriteListener modeVol() {
		return (v) -> reg(SidRegisterAddress.MODE_VOL, v, modeVolString(v));
	}

	private static String modeVolString(UnsignedByte newValue) {
		StringBuilder b = new StringBuilder();

		if (newValue.isBit7()) {
			b.append("CHAN_3_OFF");
		}
		b.append("|");
		if (newValue.isBit6()) {
			b.append("HIGHPASS");
		}
		b.append("|");
		if (newValue.isBit5()) {
			b.append("BANDPASS");
		}
		b.append("|");
		if (newValue.isBit4()) {
			b.append("LOWPASS");
		}
		b.append("|");
		UnsignedByte vol = newValue.and(UnsignedByte.$0F);
		b.append("vol=").append(vol);

		return b.toString();
	}

	@Override
	public SidRegWriteListener resFilt() {
		return (v) -> reg(SidRegisterAddress.RES_FILT, v, resFiltString(v));
	}

	private static String resFiltString(UnsignedByte newValue) {
		StringBuilder b = new StringBuilder();
		int res = newValue.and(UnsignedByte.$F0).uInt() >> 4;
		b.append("res=").append(UnsignedByte.fromLsbOf(res));
		b.append("|");

		if (newValue.isBit3()) {
			b.append("FILT_EX");
		}
		b.append("|");
		if (newValue.isBit2()) {
			b.append("FILT_3");
		}
		b.append("|");
		if (newValue.isBit1()) {
			b.append("FILT_2");
		}
		b.append("|");
		if (newValue.isBit0()) {
			b.append("FILT_1");
		}

		return b.toString();
	}

}
