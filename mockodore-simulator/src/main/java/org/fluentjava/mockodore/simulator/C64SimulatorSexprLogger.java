package org.fluentjava.mockodore.simulator;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.InstructionAndOperand;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.model.machine.Register;
import org.fluentjava.mockodore.model.machine.StatusRegister;

public class C64SimulatorSexprLogger implements C64SimulatorEventListener {

	private StringBuilder eventLog = new StringBuilder();
	private int time;

	private void logEvent(int time, Object... parts) {
		eventLog.append("(").append(time).append(" ");
		for (int i = 0; i < parts.length; i++) {
			eventLog.append(parts[i]);
			if (i < parts.length - 1) {
				eventLog.append(" ");
			}
		}
		eventLog.append(")");
	}

	private static String hex(byte value) {
		return "$" + Integer.toHexString(0xFF & value).toUpperCase();
	}

	@Override
	public void timeAdvanced(int time) {
		this.time = time;
	}

	@Override
	public void loadPrg(PrgBytesWithLoadAddress prg) {
		RawAddress startAddress = prg.address();
		logEvent(time, "loadPrg", startAddress, prg.justPrgBytes().length);
	}

	@Override
	public void simpleSys(RawAddress address) {
		logEvent(time, "simpleSys", address);

	}

	@Override
	public void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr) {
		logEvent(time, pc, instr);
	}

	@Override
	public void returnSimpleSys() {
		logEvent(time, "rtsSys");
	}

	@Override
	public void writeSr(StatusRegister sr, UnsignedByte oldValue) {
		logEvent(time, "SR:=" + hex(sr.value().signedByte()) + " [" + sr + "]");
	}

	@Override
	public void writeStatusC(StatusRegister sr, boolean newValue) {
		// we log the whole byte
	}

	@Override
	public void writeStatusN(StatusRegister sr, boolean newValue) {
		// we log the whole byte
	}

	@Override
	public void writeStatusV(StatusRegister sr, boolean newValue) {
		// we log the whole byte
	}

	@Override
	public void writeStatusZ(StatusRegister sr, boolean newValue) {
		// we log the whole byte
	}

	@Override
	public void writeSp(Register sp, UnsignedByte oldValue) {
		logEvent(time, sp);
	}

	@Override
	public void writeA(Register a, UnsignedByte oldValue) {
		logEvent(time, a);
	}

	@Override
	public void writeX(Register x, UnsignedByte oldValue) {
		logEvent(time, x);
	}

	@Override
	public void writeY(Register y, UnsignedByte oldValue) {
		logEvent(time, y);
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		logEvent(time, RawAddress.named(address) + ":=" + newValue);
	}

	@Override
	public void readMem(int address, UnsignedByte value) {
		// not logging, at least for now
	}

	public String eventLog() {
		return eventLog.toString();
	}

	public void clearEventLog() {
		eventLog = new StringBuilder();
	}

	@Override
	public void warning(String warning) {
		logEvent(time, "WARNING:", warning);
	}

}
