package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;
import org.fluentjava.mockodore.model.machine.Op;

public class Instruction implements ByteExtent {

	private final Op op;
	private final Operand operand;

	public Instruction(Op op, Operand operand) {
		this.op = op;
		this.operand = operand;
	}

	@Override
	public int length() {
		return op.length();
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		ctx.write(op.byteValue());
		operand.writeTo(ctx);
	}

	@Override
	public void toAssembler(StringBuilder b, String indentation) {
		b.append(indentation);
		AssemblerPrettyPrinter.toAssy(b, op, operand);
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append(op.name().toLowerCase());
		b.append("(");
		b.append(operand.asJava());
		b.append(")");
	}

}
