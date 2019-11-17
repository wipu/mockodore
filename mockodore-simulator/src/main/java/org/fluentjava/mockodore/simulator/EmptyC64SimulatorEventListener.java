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

public class EmptyC64SimulatorEventListener
		implements C64SimulatorEventListener {

	@Override
	public void timeAdvanced(int time) {
		// nothing to do
	}

	@Override
	public void loadPrg(PrgBytesWithLoadAddress prg) {
		// nothing to do
	}

	@Override
	public void simpleSys(RawAddress address) {
		// nothing to do
	}

	@Override
	public void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr) {
		// nothing to do
	}

	@Override
	public void returnSimpleSys() {
		// nothing to do
	}

	@Override
	public void writeSr(StatusRegister sr, UnsignedByte oldValue) {
		// nothing to do
	}

	@Override
	public void writeStatusN(StatusRegister sr, boolean newValue) {
		// nothing to do
	}

	@Override
	public void writeStatusV(StatusRegister sr, boolean newValue) {
		// nothing to do
	}

	@Override
	public void writeStatusC(StatusRegister sr, boolean newValue) {
		// nothing to do
	}

	@Override
	public void writeStatusZ(StatusRegister sr, boolean newValue) {
		// nothing to do
	}

	@Override
	public void writeSp(Register sp, UnsignedByte oldValue) {
		// nothing to do
	}

	@Override
	public void writeA(Register a, UnsignedByte oldValue) {
		// nothing to do
	}

	@Override
	public void writeX(Register x, UnsignedByte oldValue) {
		// nothing to do
	}

	@Override
	public void writeY(Register y, UnsignedByte oldValue) {
		// nothing to do
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		// nothing to do
	}

	@Override
	public void readMem(int address, UnsignedByte value) {
		// nothing to do
	}

	@Override
	public void warning(String warning) {
		// nothing to do
	}

}
