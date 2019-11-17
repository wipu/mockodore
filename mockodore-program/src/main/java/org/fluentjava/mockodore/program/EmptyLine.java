package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class EmptyLine implements ByteExtent {

	@Override
	public int length() {
		return 0;
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		// nothing to write
	}

	@Override
	public void toAssembler(StringBuilder b, String indentation) {
		b.append("");
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append("emptyLine()");
	}

}
