package org.fluentjava.mockodore.model.sid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fluentjava.mockodore.model.addressing.RawAddress;

public class SidRegisterAddress {

	public static final RawAddress BASE_ADDRESS = RawAddress.named(0xD400);
	private static int block = 0;
	private static int nextOffsetInBlock = 0;

	private static final List<SidRegisterAddress> all = new ArrayList<>();

	public static final SidRegisterAddress FREQ_LO_1 = reg("FreqLo1");
	public static final SidRegisterAddress FREQ_HI_1 = reg("FreqHi1");
	public static final SidRegisterAddress PW_LO_1 = reg("PwLo1");
	public static final SidRegisterAddress PW_HI_1 = reg("PwHi1");
	public static final SidRegisterAddress CR_1 = reg("CR1");
	public static final SidRegisterAddress AD_1 = reg("AD1");
	public static final SidRegisterAddress SR_1 = reg("SR1");

	static {
		block++;
		nextOffsetInBlock = 0;
	}

	public static final SidRegisterAddress FREQ_LO_2 = reg("FreqLo2");
	public static final SidRegisterAddress FREQ_HI_2 = reg("FreqHi2");
	public static final SidRegisterAddress PW_LO_2 = reg("PwLo2");
	public static final SidRegisterAddress PW_HI_2 = reg("PwHi2");
	public static final SidRegisterAddress CR_2 = reg("CR2");
	public static final SidRegisterAddress AD_2 = reg("AD2");
	public static final SidRegisterAddress SR_2 = reg("SR2");

	static {
		block++;
		nextOffsetInBlock = 0;
	}

	public static final SidRegisterAddress FREQ_LO_3 = reg("FreqLo3");
	public static final SidRegisterAddress FREQ_HI_3 = reg("FreqHi3");
	public static final SidRegisterAddress PW_LO_3 = reg("PwLo3");
	public static final SidRegisterAddress PW_HI_3 = reg("PwHi3");
	public static final SidRegisterAddress CR_3 = reg("CR3");
	public static final SidRegisterAddress AD_3 = reg("AD3");
	public static final SidRegisterAddress SR_3 = reg("SR3");

	static {
		block++;
		nextOffsetInBlock = 0;
	}

	public static final SidRegisterAddress FCLO = reg("FcLo");
	public static final SidRegisterAddress FCHI = reg("FcHi");
	public static final SidRegisterAddress RES_FILT = reg("ResFilt");
	public static final SidRegisterAddress MODE_VOL = reg("ModeVol");

	private static SidRegisterAddress reg(String name) {
		SidRegisterAddress reg = new SidRegisterAddress(name, block,
				nextOffsetInBlock++);
		all.add(reg);
		return reg;
	}

	private final String name;
	private final RawAddress value;
	private final int blockOffset;
	private final int offsetInBlock;
	private final int offsetInSid;

	private SidRegisterAddress(String name, int blockOffset,
			int offsetInBlock) {
		this.name = name;
		this.blockOffset = blockOffset;
		this.offsetInBlock = offsetInBlock;
		this.offsetInSid = oscOffset(blockOffset) + offsetInBlock;
		this.value = BASE_ADDRESS.plus(offsetInSid);
	}

	public static int offsetInBlock(OscRegisterName regName) {
		// OSC_1 is just another osc here, to get an example reg from which
		return SidRegisterAddress.of(OscName.OSC_1, regName).offsetInBlock();
	}

	public String name() {
		return name;
	}

	public RawAddress address() {
		return value;
	}

	public int blockOffset() {
		return blockOffset;
	}

	public int offsetInBlock() {
		return offsetInBlock;
	}

	public int offsetInSid() {
		return offsetInSid;
	}

	@Override
	public String toString() {
		return name;
	}

	public static int oscOffset(int zeroBasedOscNumber) {
		return zeroBasedOscNumber * 7;
	}

	public static List<SidRegisterAddress> all() {
		return Collections.unmodifiableList(all);
	}

	public static SidRegisterAddress of(OscName osc, OscRegisterName reg) {
		int index = osc.offset() + reg.ordinal();
		return all().get(index);
	}

	public static SidRegisterAddress first() {
		return all().get(0);
	}

	public static SidRegisterAddress last() {
		return all().get(all().size() - 1);
	}

	public OscName osc() {
		return OscName.values()[blockOffset];
	}

}
