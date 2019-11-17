package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class C64SimulatorEventListenerProxy
		implements C64SimulatorEventListener {

	private final C64SimulatorEventListener delegate;

	public C64SimulatorEventListenerProxy(C64SimulatorEventListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void writeSp(Register sp, UnsignedByte oldValue) {
		delegate.writeSp(sp, oldValue);
	}

	@Override
	public void writeA(Register a, UnsignedByte oldValue) {
		delegate.writeA(a, oldValue);
	}

	@Override
	public void writeX(Register x, UnsignedByte oldValue) {
		delegate.writeX(x, oldValue);
	}

	@Override
	public void writeY(Register y, UnsignedByte oldValue) {
		delegate.writeY(y, oldValue);
	}

	@Override
	public void timeAdvanced(int time) {
		delegate.timeAdvanced(time);
	}

	@Override
	public void loadPrg(PrgBytesWithLoadAddress prg) {
		delegate.loadPrg(prg);
	}

	@Override
	public void writeSr(StatusRegister sr, UnsignedByte oldValue) {
		delegate.writeSr(sr, oldValue);
	}

	@Override
	public void simpleSys(RawAddress address) {
		delegate.simpleSys(address);
	}

	@Override
	public void writeStatusN(StatusRegister sr, boolean newValue) {
		delegate.writeStatusN(sr, newValue);
	}

	@Override
	public void startInstr(RawAddress pc,
			InstructionAndOperand<? extends Op, ? extends Operand> instr) {
		delegate.startInstr(pc, instr);
	}

	@Override
	public void writeStatusV(StatusRegister sr, boolean newValue) {
		delegate.writeStatusV(sr, newValue);
	}

	@Override
	public void writeStatusC(StatusRegister sr, boolean newValue) {
		delegate.writeStatusC(sr, newValue);
	}

	@Override
	public void returnSimpleSys() {
		delegate.returnSimpleSys();
	}

	@Override
	public void writeMem(int address, UnsignedByte newValue) {
		delegate.writeMem(address, newValue);
	}

	@Override
	public void writeStatusZ(StatusRegister sr, boolean newValue) {
		delegate.writeStatusZ(sr, newValue);
	}

	@Override
	public void readMem(int address, UnsignedByte value) {
		delegate.readMem(address, value);
	}

	@Override
	public void warning(String warning) {
		delegate.warning(warning);
	}

}
