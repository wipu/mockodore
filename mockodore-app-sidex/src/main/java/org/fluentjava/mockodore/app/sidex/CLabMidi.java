package org.fluentjava.mockodore.app.sidex;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class CLabMidi {

	public static final RawAddress MIDI_CTRL = RawAddress.named(0xDE04);
	public static final RawAddress MIDI_STATUS = RawAddress.named(0xDE06);
	public static final RawAddress MIDI_RECV_DATA = RawAddress.named(0xDE07);
	public static final UnsignedByte ACIA_ENABLE_WITH_NO_IRQ = UnsignedByte.x16;
	public static final UnsignedByte ACIA_RESET = UnsignedByte.x03;

}
