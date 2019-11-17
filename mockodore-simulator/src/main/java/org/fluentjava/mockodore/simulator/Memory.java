package org.fluentjava.mockodore.simulator;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public interface Memory {

	void write(int addr, UnsignedByte value);

	UnsignedByte read(int addr);

}
