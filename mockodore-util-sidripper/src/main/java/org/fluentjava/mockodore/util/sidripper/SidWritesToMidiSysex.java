package org.fluentjava.mockodore.util.sidripper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.midievents.Midievents;
import org.fluentjava.joulu.midievents.Midievents.MidieventsPlease;
import org.fluentjava.joulu.midievents.Midiseq;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
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
			regValues.put(reg, UnsignedByte.x00);
		}
	}

	@Override
	public void playCallStarting() {
		List<UnsignedByte> msg = new ArrayList<>();
		msg.add(UnsignedByte.xF0);
		msg.add(UnsignedByte.x44); // Casio

		List<UnsignedByte> rawValues = new ArrayList<>();
		for (SidRegisterAddress reg : SidRegisterAddresses
				.allInDefaultWritingOrder()) {
			rawValues.add(regValues.get(reg));
		}
		rawValues.add(UnsignedByte.x00); // padding
		rawValues.add(UnsignedByte.x00);
		rawValues.add(UnsignedByte.x00); // padding
		msg.addAll(SysexEncoder.sysexEncoded(rawValues));

		msg.add(UnsignedByte.xF7);

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

	public Midiseq toMidiseq() {
		playCallStarting();
		return Midiseq.usingDefaults().events(e.end()).end();
	}

}
