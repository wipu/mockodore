package org.fluentjava.mockodore.api.assylang;

import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RelativeAddress;
import org.fluentjava.mockodore.model.machine.Op;

public interface InstructionsLangOf<T, END> extends LabeledBytesLang<T, END> {

	T bytes(ByteExtent instr);

	T instr(Op op, Operand operand);

	T instr(Op op, RelativeAddress operand);

}
