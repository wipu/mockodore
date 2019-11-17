package org.fluentjava.mockodore.model.addressing;

public interface ByteExtent {

	int length();

	void writeTo(ProgramOutputContext ctx);

	void toAssembler(StringBuilder b, String indentation);

	void toJava(StringBuilder b);

}
