package org.fluentjava.mockodore.simulator;

import org.junit.jupiter.api.Test;

public class ListenerCoverageRaisingTest {

	@Test
	public void warnings() {
		String warning = "getting coverage";
		new EmptyC64SimulatorEventListener().warning(warning);
		new C64SimulatorSexprLogger().warning(warning);
	}

}
