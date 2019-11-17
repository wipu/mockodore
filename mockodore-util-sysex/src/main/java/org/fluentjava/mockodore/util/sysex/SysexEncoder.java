package org.fluentjava.mockodore.util.sysex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class SysexEncoder {

	private static final List<Integer> MASKS = Arrays.asList(0x01, 0x02, 0x04,
			0x08, 0x10, 0x20, 0x40);

	public static List<UnsignedByte> sysexEncoded(List<UnsignedByte> bytes) {
		if (bytes.size() % 7 != 0) {
			throw new IllegalArgumentException(
					"Data size must be multiple of 7");
		}
		List<UnsignedByte> result = new ArrayList<>();

		UnsignedByte highBits = UnsignedByte.$00;
		for (int i = 0; i < bytes.size(); i++) {
			int mod7 = i % 7;

			UnsignedByte b = bytes.get(i);
			result.add(b.and(UnsignedByte.$7F));

			if (b.isBit7()) {
				UnsignedByte mask = UnsignedByte.from(MASKS.get(mod7));
				highBits = highBits.or(mask);
			}

			if (mod7 == 6) {
				// end of group
				result.add(highBits);
				highBits = UnsignedByte.$00;
			}
		}

		return result;
	}

	public static List<UnsignedByte> sysexEncoded(int... bytes) {
		List<UnsignedByte> ubs = new ArrayList<>();
		for (int b : bytes) {
			ubs.add(UnsignedByte.from(b));
		}
		return sysexEncoded(ubs);
	}

}
