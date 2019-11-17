package org.fluentjava.mockodore.lib.basicloader;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.RawAddress;

public class BasicLoader<LANG extends C64AssyLangOf<LANG, ?>> {

	private final LANG p;

	public BasicLoader(LANG p) {
		this.p = p;
	}

	public static <LANG extends C64AssyLangOf<LANG, ?>> LANG def(LANG p) {
		return new BasicLoader<>(p).def();
	}

	private LANG def() {
		p.startAddress(RawAddress.named(0x0801));
		// next line is at 0x080C
		p.data(0x0C).data(0x08);
		// line number is 10
		p.data(0x0A).data(0x00);
		// SYS " 2062"
		p.data(0x9E).data(0x20).data(0x32).data(0x30).data(0x36).data(0x32);
		// end of line
		p.data(0x00);
		// end of program
		p.data(0x00).data(0x00);

		return p;
	}

}
