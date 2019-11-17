package org.fluentjava.mockodore.model.machine;

import java.util.Arrays;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class PrgBytesWithLoadAddress {

	private final byte[] bytes;

	public PrgBytesWithLoadAddress(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] allBytes() {
		return bytes;
	}

	public RawAddress address() {
		return RawAddress.fromLsbAndMsb(bytes[0], bytes[1]);
	}

	public byte[] justPrgBytes() {
		byte[] prg = new byte[bytes.length - 2];
		System.arraycopy(bytes, 2, prg, 0, prg.length);
		return prg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PrgBytesWithLoadAddress other = (PrgBytesWithLoadAddress) obj;
		if (!Arrays.equals(bytes, other.bytes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ByteArrayPrettyPrinter.spaceSeparatedHex(bytes);
	}

}
