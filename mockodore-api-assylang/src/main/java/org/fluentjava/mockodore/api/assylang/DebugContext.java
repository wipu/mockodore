package org.fluentjava.mockodore.api.assylang;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.machine.Accumulator;
import org.fluentjava.mockodore.model.machine.Register;
import org.fluentjava.mockodore.model.machine.StatusRegister;

public interface DebugContext {

	UnsignedByte readMem(AbsRef addr);

	void writeMem(AbsRef addr, UnsignedByte value);

	Accumulator a();

	Register y();

	StatusRegister sr();

}
