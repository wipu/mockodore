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

	private SidRegWriteListener cr(OscName osc) {
		return (v) -> cr(osc, v);
	}

	private void cr(OscName osc, UnsignedByte newValue) {
		regWrite(SidRegisterAddress.of(osc, OscRegisterName.CR), newValue,
				sidControlRegisterString(newValue));
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		switch (reg.offsetInSid()) {

		case 0:
			return freqLo(OscName.OSC_1);
		case 1:
			return freqHi(OscName.OSC_1);
		case 2:
			return pwLo(OscName.OSC_1);
		case 3:
			return pwHi(OscName.OSC_1);
		case 4:
			return cr(OscName.OSC_1);
		case 5:
			return ad(OscName.OSC_1);
		case 6:
			return sr(OscName.OSC_1);

		case 7:
			return freqLo(OscName.OSC_2);
		case 8:
			return freqHi(OscName.OSC_2);
		case 9:
			return pwLo(OscName.OSC_2);
		case 10:
			return pwHi(OscName.OSC_2);
		case 11:
			return cr(OscName.OSC_2);
		case 12:
			return ad(OscName.OSC_2);
		case 13:
			return sr(OscName.OSC_2);

		case 14:
			return freqLo(OscName.OSC_3);
		case 15:
			return freqHi(OscName.OSC_3);
		case 16:
			return pwLo(OscName.OSC_3);
		case 17:
			return pwHi(OscName.OSC_3);
		case 18:
			return cr(OscName.OSC_3);
		case 19:
			return ad(OscName.OSC_3);
		case 20:
			return sr(OscName.OSC_3);

		case 21:
			return fcLo();
		case 22:
			return fcHi();
		case 23:
			return resFilt();
		case 24:
			return modeVol();

		default:
			throw new UnsupportedOperationException(
					"Unsupported register: " + reg);
		}
	}

	private SidRegWriteListener freqLo(OscName osc) {
		return (v) -> regWrite(
				SidRegisterAddress.of(osc, OscRegisterName.FREQ_LO), v);
	}

	private SidRegWriteListener freqHi(OscName osc) {
		return (v) -> regWrite(
				SidRegisterAddress.of(osc, OscRegisterName.FREQ_HI), v);
	}

	private SidRegWriteListener pwLo(OscName osc) {
		return (v) -> regWrite(
				SidRegisterAddress.of(osc, OscRegisterName.PW_LO), v);
	}

	private SidRegWriteListener pwHi(OscName osc) {
		return (v) -> regWrite(
				SidRegisterAddress.of(osc, OscRegisterName.PW_HI), v);
	}

	private SidRegWriteListener ad(OscName osc) {
		return (v) -> ad(osc, v);
	}

	private void ad(OscName osc, UnsignedByte newValue) {
		regWrite(SidRegisterAddress.of(osc, OscRegisterName.AD), newValue,
				envByteString(newValue));
	}

	private static String envByteString(UnsignedByte newValue) {
		int attack = newValue.and(UnsignedByte.xF0).uInt() >> 4;
		int decay = newValue.and(UnsignedByte.x0F).uInt();
		return ByteArrayPrettyPrinter.spaceSeparatedHex((byte) attack,
				(byte) decay);
	}

	private SidRegWriteListener sr(OscName osc) {
		return (v) -> sr(osc, v);
	}

	private void sr(OscName osc, UnsignedByte newValue) {
		regWrite(SidRegisterAddress.of(osc, OscRegisterName.SR), newValue,
				envByteString(newValue));
	}

	private void regWrite(SidRegisterAddress reg, UnsignedByte newValue,
			String newValuePrettyPrinted) {
		String sidDebugLine = indentation(reg.osc()) + reg + "="
				+ newValuePrettyPrinted + " (" + reg.address() + "=" + newValue
				+ ")\n";
		sidWriteLog.append(sidDebugLine);
	}

	private void regWrite(SidRegisterAddress reg, UnsignedByte newValue) {
		regWrite(reg, newValue, newValue.toString());
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

	private SidRegWriteListener fcLo() {
		return (v) -> regWrite(SidRegisterAddress.FCLO, v);
	}

	private SidRegWriteListener fcHi() {
		return (v) -> regWrite(SidRegisterAddress.FCHI, v);
	}

	private SidRegWriteListener modeVol() {
		return (v) -> regWrite(SidRegisterAddress.MODE_VOL, v,
				modeVolString(v));
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
		UnsignedByte vol = newValue.and(UnsignedByte.x0F);
		b.append("vol=").append(vol);

		return b.toString();
	}

	private SidRegWriteListener resFilt() {
		return (v) -> regWrite(SidRegisterAddress.RES_FILT, v,
				resFiltString(v));
	}

	private static String resFiltString(UnsignedByte newValue) {
		StringBuilder b = new StringBuilder();
		int res = newValue.and(UnsignedByte.xF0).uInt() >> 4;
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
