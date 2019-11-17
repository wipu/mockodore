package org.fluentjava.mockodore.model.labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.junit.Test;

public class LabelTest {

	private static void assertInvalidLabel(String label) {
		try {
			Label.named(label);
			fail();
		} catch (MockodoreException e) {
			assertEquals("Invalid label syntax: '" + label + "'",
					e.getMessage());
		}
	}

	private static void assertValidLabel(String label) {
		assertEquals(label, Label.named(label).value());
	}

	@Test
	public void invalidBecauseLookLikeCompileTimeIndexing() {
		assertInvalidLabel("a+(1)");
		assertInvalidLabel("a+");
		assertInvalidLabel("a(");
	}

	@Test
	public void invalidBecauseNumeric() {
		assertInvalidLabel("1");
		assertInvalidLabel("$1");
		assertInvalidLabel("0a");
	}

	@Test
	public void invalidBecauseOfAssemblerSyntax() {
		assertInvalidLabel("");
		assertInvalidLabel("a:");
		assertInvalidLabel(".b");
	}

	@Test
	public void invalidBecauseIDontKnowHowStrictAssemblerSyntaxIs() {
		assertInvalidLabel("aä");
	}

	@Test
	public void validExamples() {
		assertValidLabel("a");
		assertValidLabel("z");
		assertValidLabel("a1");
		assertValidLabel("Z3");
		assertValidLabel("a-Z");
		assertValidLabel("_a");
		assertValidLabel("a_");
		assertValidLabel("A_B");
	}

	@Test
	public void subName() {
		Label sub = Label.named("main").subLabel("sub");
		assertEquals("mainsub", sub.value());
	}

}
