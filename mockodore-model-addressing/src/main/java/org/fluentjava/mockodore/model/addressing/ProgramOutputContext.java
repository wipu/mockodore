package org.fluentjava.mockodore.model.addressing;

public interface ProgramOutputContext {

	RawAddress resolve(AbsRef address);

	/**
	 * TODO proper strong type
	 */
	RawAddress resolve(int programRelativeAddress);

	void write(int... bytes);

}
