package org.fluentjava.mockodore.lib.misc;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.program.C64AssyLangProxy;

public class Vbi {

	public static <LANG extends C64AssyLangOf<LANG, END>, END> LANG registerVbi(
			LANG out, int vbiRasterLine, Label vbiHandler) {
		return new Code<>(out).registerVbi(vbiRasterLine, vbiHandler);
	}

	private static class Code<LANG extends C64AssyLangOf<LANG, END>, END>
			extends C64AssyLangProxy<LANG, END> {

		public Code(LANG out) {
			super(out);
		}

		protected LANG registerVbi(int vbiRasterLine, Label vbiHandler) {
			sei();
			lda(0x7F);
			sta(raw(0xDC0D));

			and(raw(0xD011));
			sta(raw(0xd011));

			lda(vbiHandler.lsb()).sta(raw(0x0314));
			lda(vbiHandler.msb()).sta(raw(0x0315));
			lda(vbiRasterLine).sta(raw(0xd012)).sta(ZeroPage.$02).lda(0x01)
					.sta(raw(0xd01a)).cli();
			return __();
		}

	}

	private static RawAddress raw(int value) {
		return RawAddress.named(value);
	}

}
