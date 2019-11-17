package org.fluentjava.mockodore.model.addressing;

public interface Operand {

	void writeTo(ProgramOutputContext ctx);

	String asAssy();

	String asJava();

}
