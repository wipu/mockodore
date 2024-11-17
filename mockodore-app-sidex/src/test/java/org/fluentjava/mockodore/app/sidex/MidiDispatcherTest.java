package org.fluentjava.mockodore.app.sidex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.util.sysex.SysexEncoder;
import org.junit.jupiter.api.Test;

public class MidiDispatcherTest extends SidexTestBase {

	private static final UnsignedByte MANUFACTURER = MidiDispatcher.MANUFACTURER;
	private static final UnsignedByte WRONG_MANUFACTURER = MANUFACTURER
			.plusSigned(1);
	private static final RawAddress DEFAULT_BUF = RawAddress.named(0x401);
	private static final RawAddress SYSEX_BUF = DEFAULT_BUF.plus(256);
	private static final Label START = Label.named("START");

	@Override
	void beforeTest() {
		Label handleDefault = Label.named("handleDefault");
		Label defaultBufOffset = Label.named("defaultBufOffset");

		MidiDispatcher dispatcher = new MidiDispatcher(p, handleDefault,
				SYSEX_BUF, 1, SysexEventListener.NONE);

		p.label(START);
		dispatcher.defRoutine();

		p.label(handleDefault);
		p.data(Op.LDX_IMMEDIATE).label(defaultBufOffset).data(0);
		p.sta(DEFAULT_BUF.plusX());
		p.inc(defaultBufOffset);
		p.rts();
	}

	private MidiDispatcherTest in(Collection<UnsignedByte> bytes) {
		for (UnsignedByte b : bytes) {
			simLoadedWithPrg().a().set(b);
			simLoadedWithPrg().simpleSysAndAutoTick(START);
		}
		return this;
	}

	private MidiDispatcherTest in(UnsignedByte... bytes) {
		return in(Arrays.asList(bytes));
	}

	private MidiDispatcherTest in(int... bytes) {
		for (int b : bytes) {
			in(UnsignedByte.from(b));
		}
		return this;
	}

	private static List<UnsignedByte> sysexEncoded(int... bytes) {
		List<UnsignedByte> ubs = new ArrayList<>();
		for (int b : bytes) {
			ubs.add(UnsignedByte.from(b));
		}
		return SysexEncoder.sysexEncoded(ubs);
	}

	private MidiDispatcherTest defaultShallHave(String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(DEFAULT_BUF, 8));
		return this;
	}

	private MidiDispatcherTest sysexShallHave(String expected) {
		return sysexShallHave(8, expected);
	}

	private MidiDispatcherTest sysexShallHave(int length, String expected) {
		assertEquals(expected, simLoadedWithPrg().hexDump(SYSEX_BUF, length));
		return this;
	}

	@Test
	public void normalMidiGoesToDefaultHandler() {
		in(0x80, 0x01, 0x40).defaultShallHave("80 01 40 20 20 20 20 20")
				.sysexShallHave("20 20 20 20 20 20 20 20");
	}

	@Test
	public void wrongManufacturerSysexIsIgnored() {
		in(0xF0).in(WRONG_MANUFACTURER);
		in(0x01, 0x02);
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20")
				.sysexShallHave("20 20 20 20 20 20 20 20");
	}

	@Test
	public void rightManufacturerSysexIsHandledEvenIfItIsTruncatedBeforeHighBitsByte() {
		in(0xF0).in(MANUFACTURER);
		in(0x01, 0x02);
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20")
				.sysexShallHave("01 02 20 20 20 20 20 20");
	}

	@Test
	public void oneGroupOfEightBitDataIsDecodedCorrectly() {
		in(0xF0).in(MANUFACTURER);
		in(sysexEncoded(0x01, 0x02, 0x7F, 0x80, 0xF1, 0xF7, 0xFF));
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20")
				.sysexShallHave("01 02 7F 80 F1 F7 FF 20");
	}

	@Test
	public void twoGroupsOfEightBitDataAreDecodedCorrectly() {
		in(0xF0).in(MANUFACTURER);
		in(sysexEncoded(0x01, 0x02, 0x11, 0x7F, 0x80, 0xF1, 0xFF));
		in(sysexEncoded(0x01, 0x02, 0x11, 0x7F, 0x80, 0xF1, 0xFF));
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20").sysexShallHave(16,
				"01 02 11 7F 80 F1 FF 01 02 11 7F 80 F1 FF 20 20");
	}

	@Test
	public void threeGroupsOfEightBitDataAreDecodedCorrectly() {
		in(0xF0).in(MANUFACTURER);
		in(sysexEncoded(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 17, 18, 19, 20, 21, 22, 23, 24, 0, 0, 0));
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20").sysexShallHave(30,
				"00 01 02 03 04 05 06 07 " + "08 09 0A 0B 0C 0D 0E 0F "
						+ "10 11 12 13 14 15 16 17 " + "18 00 00 00 20 20");
	}

	@Test
	public void normalMidiWorksAfterWrongManufacturerSysex() {
		wrongManufacturerSysexIsIgnored();
		in(0x80, 0x01, 0x40).defaultShallHave("80 01 40 20 20 20 20 20");
	}

	@Test
	public void normalMidiWorksAfterTruncatedSysex() {
		rightManufacturerSysexIsHandledEvenIfItIsTruncatedBeforeHighBitsByte();
		in(0x80, 0x01, 0x40).defaultShallHave("80 01 40 20 20 20 20 20");
	}

	@Test
	public void normalMidiWorksAfterFullSysex() {
		oneGroupOfEightBitDataIsDecodedCorrectly();
		in(0x80, 0x01, 0x40).defaultShallHave("80 01 40 20 20 20 20 20");
	}

	/**
	 * For example active sensing FE is received 3 times a second
	 */
	@Test
	public void eightBitBytesAreDiscardedWhenReceivingSysex() {
		in(0xF0).in(MANUFACTURER);
		in(1, 2, 0xFE, 3, 0xFF, 4, 5, 6, 7, 0x7F);
		in(0xF7);

		defaultShallHave("20 20 20 20 20 20 20 20")
				.sysexShallHave("81 82 83 84 85 86 87 20");
	}

}
