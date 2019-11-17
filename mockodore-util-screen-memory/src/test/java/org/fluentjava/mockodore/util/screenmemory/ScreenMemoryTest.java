package org.fluentjava.mockodore.util.screenmemory;

import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.C64SimulatorLineLogger;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.fluentjava.mockodore.simulator.C64Simulator;
import org.junit.Before;
import org.junit.Test;

public class ScreenMemoryTest {

	private static final RawAddress startAddress = RawAddress.named(0x1000);
	private static final RawAddress SCREEN = ScreenMemory.START_ADDRESS;

	protected C64AssyLangForProgram p;
	protected C64SimulatorLineLogger logger;
	private C64Simulator sim;
	private StringBuilder b;

	@Before
	public final void before() {
		p = MockodoreProgram.with();
		p.startAddress(startAddress);
		logger = null;
		sim = null;
		b = new StringBuilder();
	}

	protected C64Simulator simLoadedWithPrg() {
		if (sim != null) {
			return sim;
		}
		MockodoreProgram prg = p.end();
		logger = new C64SimulatorLineLogger(prg.labelMap());
		sim = new C64Simulator(logger);
		sim.load(prg);
		return sim;
	}

	@Test
	public void initialScreenContent() {
		p.rts();

		simLoadedWithPrg().simpleSysAndAutoTick();

		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		ScreenMemory.of(sim).asStringShallBe(b.toString());
	}

	@Test
	public void someSimpleCharacters() {
		// row 1
		p.lda(0x00).sta(SCREEN.plus(0)); // @
		p.lda(0x01).sta(SCREEN.plus(1)); // A
		// row 2
		p.lda(0x1A).sta(SCREEN.plus(42)); // Z
		// row 3
		p.lda(0x24).sta(SCREEN.plus(83)); // $
		p.rts();

		simLoadedWithPrg().simpleSysAndAutoTick();

		b.append("@A                                      \n");
		b.append("  Z                                     \n");
		b.append("   $                                    \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		b.append("                                        \n");
		ScreenMemory.of(sim).asStringShallBe(b.toString());
	}

}
