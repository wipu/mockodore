package org.fluentjava.mockodore.model.machine;

import org.fluentjava.mockodore.model.addressing.Operand;

public interface InstructionAndOperand<OP extends Op, OPERAND extends Operand> {

	OP op();

	OPERAND operand();

}
