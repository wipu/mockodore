package org.fluentjava.mockodore.program;

import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;

public class CommentLine implements ByteExtent {

	private final String comment;

	public CommentLine(String comment) {
		this.comment = comment;
	}

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
		b.append(indentation);
		b.append("; ").append(comment);
	}

	@Override
	public void toJava(StringBuilder b) {
		b.append("commentLine(\"" + comment + "\")");
	}

}
