package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

@FunctionalInterface
public interface SidRegWriteListener {

	void write(UnsignedByte newValue);

}
