package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.model.addressing.ImpliedOperand;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.machine.Op;

public class AssemblerPrettyPrinter {

	public static void toAssy(StringBuilder b, Op op, Operand operand) {
		b.append(op.name().toLowerCase());
		if (!(operand instanceof ImpliedOperand)) {
			b.append(" ");
		}
		b.append(operand.asAssy());
	}

}
