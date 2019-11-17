package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public interface C64SimulatorEventListener extends RegisterWriteListener {

	void timeAdvanced(int time);

	void loadPrg(PrgBytesWithLoadAddress prg);

	void simpleSys(RawAddress address);

	void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr);

	void returnSimpleSys();

	void writeMem(int address, UnsignedByte newValue);

	void readMem(int address, UnsignedByte value);

	void warning(String warning);

}
