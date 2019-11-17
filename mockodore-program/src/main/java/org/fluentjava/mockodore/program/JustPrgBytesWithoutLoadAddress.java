package org.fluentjava.mockodore.program;

public class JustPrgBytesWithoutLoadAddress {

	private final byte[] bytes;

	public JustPrgBytesWithoutLoadAddress(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] justPrgBytes() {
		return bytes;
	}

}
