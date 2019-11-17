package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public interface RegisterWriteListener {

	void writeSp(Register sp, UnsignedByte oldValue);

	void writeA(Register a, UnsignedByte oldValue);

	void writeX(Register x, UnsignedByte oldValue);

	void writeY(Register y, UnsignedByte oldValue);

	void writeSr(StatusRegister sr, UnsignedByte oldValue);

	void writeStatusN(StatusRegister sr, boolean newValue);

	void writeStatusV(StatusRegister sr, boolean newValue);

	void writeStatusC(StatusRegister sr, boolean newValue);

	void writeStatusZ(StatusRegister sr, boolean newValue);

}
