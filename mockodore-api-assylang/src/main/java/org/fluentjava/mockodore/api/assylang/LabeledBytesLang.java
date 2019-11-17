package org.fluentjava.mockodore.api.assylang;

import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;

public interface LabeledBytesLang<T, END> {

	END end();

	T startAddress(RawAddress startAddress);

	T label(Label label);

	T label(Labeled labeled);

	T commentAfterInstr(String comment);

	T debug(Debug debug);

}
