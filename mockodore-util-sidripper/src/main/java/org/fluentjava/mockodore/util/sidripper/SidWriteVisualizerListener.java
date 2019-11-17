package org.fluentjava.mockodore.util.sidripper;

import org.fluentjava.mockodore.model.sid.OscName;

public interface SidWriteVisualizerListener {

	SidWriteVisualizerListener EMPTY = new SidWriteVisualizerListener() {
		@Override
		public void oscPlottedAt(OscName osc, int x, int y) {
			// empty
		}
	};

	void oscPlottedAt(OscName osc, int x, int y);

}
