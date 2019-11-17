package org.fluentjava.mockodore.model.sid;

/**
 * TODO maybe 2 more concepts needed, global is a sid block, not really an
 * oscillator. And trigger is not even a sid block, it's a 'tickable' or
 * something.
 */
public enum OscName {

	OSC_1(0), OSC_2(7), OSC_3(14), GLOBAL(21), TRIGGER(null),

	;

	private final Integer offset;

	OscName(Integer offset) {
		this.offset = offset;
	}

	public Integer offset() {
		return offset;
	}

}
