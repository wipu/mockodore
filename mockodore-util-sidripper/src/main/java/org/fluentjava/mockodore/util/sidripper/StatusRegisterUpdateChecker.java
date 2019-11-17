package org.fluentjava.mockodore.util.sidripper;

import java.util.HashSet;
import java.util.Set;

import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListenerProxy;
import org.fluentjava.mockodore.model.machine.InstructionAndOperand;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.machine.StatusRegister;

public class StatusRegisterUpdateChecker
		extends C64SimulatorEventListenerProxy {

	private final Set<String> srFlagsWrittenByInstr = new HashSet<>();
	private InstructionAndOperand<? extends Op, ? extends Operand> currentInstr;

	public StatusRegisterUpdateChecker(C64SimulatorEventListener delegate) {
		super(delegate);
	}

	@Override
	public void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr) {
		super.startInstr(pc, instr);
		checkCurrentInstrFlagWrites();
		currentInstr = instr;
		srFlagsWrittenByInstr.clear();
	}

	private void checkCurrentInstrFlagWrites() {
		if (currentInstr == null) {
			return;
		}
		Op op = currentInstr.op();
		checkCurrentInstrFlagWrite(op.writesC(), "C");
		checkCurrentInstrFlagWrite(op.writesN(), "N");
		checkCurrentInstrFlagWrite(op.writesV(), "V");
		checkCurrentInstrFlagWrite(op.writesZ(), "Z");
	}

	private void checkCurrentInstrFlagWrite(boolean shouldWrite,
			String flagName) {
		boolean didWrite = srFlagsWrittenByInstr.contains(flagName);
		if (didWrite && !shouldWrite) {
			throw new IllegalStateException("Warning: " + currentInstr
					+ " should not have written " + flagName);
		}
		if (!didWrite && shouldWrite) {
			throw new IllegalStateException("Warning: " + currentInstr
					+ " should have written " + flagName);
		}
	}

	@Override
	public void returnSimpleSys() {
		checkCurrentInstrFlagWrites();
		super.returnSimpleSys();
	}

	@Override
	public void writeStatusC(StatusRegister sr, boolean newValue) {
		super.writeStatusC(sr, newValue);
		srFlagsWrittenByInstr.add("C");
	}

	@Override
	public void writeStatusN(StatusRegister sr, boolean newValue) {
		super.writeStatusN(sr, newValue);
		srFlagsWrittenByInstr.add("N");
	}

	@Override
	public void writeStatusV(StatusRegister sr, boolean newValue) {
		super.writeStatusV(sr, newValue);
		srFlagsWrittenByInstr.add("V");
	}

	@Override
	public void writeStatusZ(StatusRegister sr, boolean newValue) {
		super.writeStatusZ(sr, newValue);
		srFlagsWrittenByInstr.add("Z");
	}

}
