package org.fluentjava.mockodore.app.sidex;

import static org.junit.Assert.assertEquals;

import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.C64SimulatorLineLogger;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.fluentjava.mockodore.simulator.C64Simulator;
import org.fluentjava.mockodore.simulator.Memory;
import org.fluentjava.mockodore.simulator.RawMemory;
import org.fluentjava.mockodore.util.sidripper.SidWriteRipper;
import org.junit.Before;

public abstract class SidexTestBase {

	protected static final RawAddress startAddress = RawAddress.named(0x1000);

	protected C64AssyLangForProgram p;
	protected C64SimulatorLineLogger logger;
	protected SidWriteRipper sidWriteLogger;
	private C64Simulator sim;

	@Before
	public final void before() {
		p = MockodoreProgram.with();
		p.startAddress(startAddress);
		logger = null;
		sidWriteLogger = null;
		sim = null;
		beforeTest();
	}

	protected C64Simulator simLoadedWithPrg() {
		if (sim != null) {
			return sim;
		}
		MockodoreProgram prg = p.end();
		logger = new C64SimulatorLineLogger(prg.labelMap());
		sidWriteLogger = SidWriteRipper.using(logger, 100);
		sim = new C64Simulator(sidWriteLogger, mem());
		sim.load(prg);
		return sim;
	}

	protected Memory mem() {
		return new RawMemory();
	}

	abstract void beforeTest();

	protected void assertCodeSize(int size) {
		int actual = p.end().asBytes().justPrgBytes().length;
		assertSizeOrPerf("size", size, actual);
	}

	protected void assertCyclesTaken(int cycles) {
		int actual = logger.time();
		assertSizeOrPerf("cycle usage", cycles, actual);
	}

	private static void assertSizeOrPerf(String type, int expected,
			int actual) {
		if (actual > expected) {
			assertEquals(type + " increased!", expected, actual);
		}
		if (actual < expected) {
			assertEquals("Happy failure: " + type + " decreased!", expected,
					actual);
		}
	}

}
