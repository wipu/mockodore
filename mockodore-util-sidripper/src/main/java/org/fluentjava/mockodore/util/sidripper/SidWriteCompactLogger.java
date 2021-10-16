package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidWriteCompactLogger implements SidWriteListener {

	private final StringBuilder log = new StringBuilder();
	private final String regWriteSeparator;

	public SidWriteCompactLogger() {
		this("\n");
	}

	public SidWriteCompactLogger(String regWriteSeparator) {
		this.regWriteSeparator = regWriteSeparator;
	}

	@Override
	public String toString() {
		return log.toString();
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		return (v) -> write(reg, v);
	}

	private void write(SidRegisterAddress reg, UnsignedByte value) {
		log.append(simpleHex(UnsignedByte.from(reg.address().lsb())));
		log.append("=");
		log.append(simpleHex(value));
		log.append(regWriteSeparator);
	}

	private static String simpleHex(UnsignedByte b) {
		return b.toString().replace("#$", "");
	}

	@Override
	public void playCallStarting() {
		log.append("\n");
	}

}
