package org.fluentjava.mockodore.program;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.junit.jupiter.api.Test;

public class CustomLanguageDemonstrationTest {

	static class MyLang<T extends MyLang<T>>
			extends C64AssyLangProxy<T, MockodoreProgram> {

		public MyLang(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}

		@Override
		public MockodoreProgram end() {
			return out.end();
		}

		T ldaSta(int value, AbsRef addr) {
			return lda(value).sta(addr);
		}

	}

	static class MyCodeGeneratingObject extends MyLang<MyCodeGeneratingObject> {

		private final int value;

		public MyCodeGeneratingObject(C64AssyLangOf<?, MockodoreProgram> out,
				int value) {
			super(out);
			this.value = value;
		}

		MyCodeGeneratingObject inlinedCode() {
			return label(Label.named("code_from_object"))
					.ldaSta(value, RawAddress.named(value)).rts();
		}

	}

	static class MyPrg extends MyLang<MyPrg> {

		public MyPrg(C64AssyLangOf<?, MockodoreProgram> out) {
			super(out);
		}

		MyPrg content() {
			MyCodeGeneratingObject obj = new MyCodeGeneratingObject(out, 5);

			MyPrg sentence = label(Label.named("all"))
					.ldaSta(01, RawAddress.named(0x1000)).snippet()
					.ldaSta(3, RawAddress.named(0x3000));
			obj.inlinedCode();
			return sentence;
		}

		MyPrg snippet() {
			return label(Label.named("snippet")).nop();
		}

	}

	@Test
	public void assyFromCustomPrg() {
		C64AssyLangForProgram out = MockodoreProgram.with();

		new MyPrg(out).content();

		assertEquals(
				"all:\n" + "lda #$01\n" + "sta $1000\n" + "snippet:\n" + "nop\n"
						+ "lda #$03\n" + "sta $3000\n" + "code_from_object:\n"
						+ "lda #$05\n" + "sta $5\n" + "rts\n" + "",
				out.end().asAssy());
	}

}
