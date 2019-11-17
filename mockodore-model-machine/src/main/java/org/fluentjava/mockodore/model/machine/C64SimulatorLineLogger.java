package org.fluentjava.mockodore.model.machine;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;

public class C64SimulatorLineLogger implements C64SimulatorEventListener {

	private StringBuilder eventLog = new StringBuilder();
	private int time;
	private final Map<Integer, List<Label>> labelMap;
	private final Map<Integer, UnsignedByte> mem = new HashMap<>();

	public C64SimulatorLineLogger() {
		this(Collections.<Integer, List<Label>> emptyMap());
	}

	public C64SimulatorLineLogger(Map<Integer, List<Label>> labelMap) {
		this.labelMap = labelMap;
	}

	private List<Label> labels(int address) {
		List<Label> labels = labelMap.get(address);
		return labels == null ? Collections.<Label> emptyList() : labels;
	}

	private void logInstrStart(int time, Object... parts) {
		eventLog.append(time).append(": ");
		for (int i = 0; i < parts.length; i++) {
			eventLog.append(parts[i]);
			if (i < parts.length - 1) {
				eventLog.append(" ");
			}
		}
		eventLog.append("\n");
	}

	private void logEffect(int time, Object... parts) {
		eventLog.append(time).append(":      (");
		for (int i = 0; i < parts.length; i++) {
			eventLog.append(parts[i]);
			if (i < parts.length - 1) {
				eventLog.append(" ");
			}
		}
		eventLog.append(")\n");
	}

	@Override
	public void timeAdvanced(int time) {
		this.time = time;
	}

	@Override
	public void loadPrg(PrgBytesWithLoadAddress prg) {
		RawAddress startAddress = prg.address();
		logEffect(time, "loadPrg", startAddress, prg.justPrgBytes().length);
	}

	@Override
	public void simpleSys(RawAddress address) {
		logEffect(time, "simpleSys", address, labels(address.value()));

	}

	@Override
	public void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr) {
		String labels = "";
		if (instr.operand() instanceof RawAddress) {
			RawAddress addr = (RawAddress) instr.operand();
			labels = labels(addr.value()).toString();
		}
		logInstrStart(time, pc + " " + labels(pc.value()), instr, labels);
	}

	@Override
	public void returnSimpleSys() {
		logEffect(time, "rtsSys");
	}

	@Override
	public void writeSr(StatusRegister sr, UnsignedByte oldValue) {
		logRegWrite(sr, oldValue);
	}

	@Override
	public void writeStatusC(StatusRegister sr, boolean newValue) {
		logStatusFlagWrite(sr.carry(), "C", newValue);
	}

	@Override
	public void writeStatusN(StatusRegister sr, boolean newValue) {
		logStatusFlagWrite(sr.negative(), "N", newValue);
	}

	@Override
	public void writeStatusV(StatusRegister sr, boolean newValue) {
		logStatusFlagWrite(sr.overflow(), "V", newValue);
	}

	@Override
	public void writeStatusZ(StatusRegister sr, boolean newValue) {
		logStatusFlagWrite(sr.zero(), "Z", newValue);
	}

	private void logStatusFlagWrite(boolean oldValue, String flagName,
			boolean newValue) {
		StringBuilder fullMsg = new StringBuilder();
		if (newValue == oldValue) {
			fullMsg.append("- ");
		} else {
			fullMsg.append("* ");
		}
		String valueName = newValue ? flagName : flagName.toLowerCase();
		fullMsg.append("SR:").append(valueName);
		logEffect(time, fullMsg);
	}

	private void logRegWrite(Register reg, UnsignedByte oldValue) {
		StringBuilder fullMsg = new StringBuilder();
		if (reg.value() == oldValue) {
			fullMsg.append("- ");
		} else {
			fullMsg.append("* ");
		}
		fullMsg.append(reg);
		logEffect(time, fullMsg);
	}

	@Override
	public void writeSp(Register sp, UnsignedByte oldValue) {
		logRegWrite(sp, oldValue);
	}

	@Override
	public void writeA(Register a, UnsignedByte oldValue) {
		logRegWrite(a, oldValue);
	}

	@Override
	public void writeX(Register x, UnsignedByte oldValue) {
		logRegWrite(x, oldValue);
	}

	@Override
	public void writeY(Register y, UnsignedByte oldValue) {
		logRegWrite(y, oldValue);
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		logEffect(time, RawAddress.named(address) + " " + labels(address)
				+ " <-" + newValue + " (old:" + mem.get(address));
		mem.put(address, newValue);
	}

	@Override
	public void readMem(int address, UnsignedByte value) {
		// logEffect(time, RawAddress.named(address) + "->" + hex(value));
	}

	public String eventLog() {
		return eventLog.toString();
	}

	public void clearEventLog() {
		eventLog = new StringBuilder();
	}

	@Override
	public void warning(String warning) {
		logEffect(time, "WARNING:", warning);
	}

	public int time() {
		return time;
	}

}
