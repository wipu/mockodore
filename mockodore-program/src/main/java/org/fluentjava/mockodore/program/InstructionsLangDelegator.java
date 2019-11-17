package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.api.assylang.Debug;
import org.fluentjava.mockodore.api.assylang.InstructionsLangOf;
import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.RelativeAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;

public abstract class InstructionsLangDelegator<T, END>
		implements InstructionsLangOf<T, END> {

	private final InstructionsLangOf<?, END> d;

	public InstructionsLangDelegator(InstructionsLangOf<?, END> d) {
		this.d = d;
	}

	@Override
	public END end() {
		return d.end();
	}

	abstract T __(Object ignore);

	@Override
	public T bytes(ByteExtent instr) {
		return __(d.bytes(instr));
	}

	@Override
	public T instr(Op op, Operand operand) {
		return __(d.instr(op, operand));
	}

	@Override
	public T instr(Op op, RelativeAddress operand) {
		return __(d.instr(op, operand));
	}

	@Override
	public T startAddress(RawAddress startAddress) {
		return __(d.startAddress(startAddress));
	}

	@Override
	public T label(Label label) {
		return __(d.label(label));
	}

	@Override
	public T label(Labeled label) {
		return __(d.label(label));
	}

	@Override
	public T commentAfterInstr(String comment) {
		return __(d.commentAfterInstr(comment));
	}

	@Override
	public T debug(Debug debug) {
		return __(d.debug(debug));
	}

}
