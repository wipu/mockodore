package org.fluentjava.mockodore.util.sidripper;

import static org.junit.Assert.assertEquals;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.C64SimulatorLineLogger;
import org.fluentjava.mockodore.model.sid.SidRegisterAddress;
import org.junit.Before;
import org.junit.Test;

public class SidWriteDelegatorTest {

	private C64SimulatorLineLogger mem;
	private C64SimulatorEventListener delegator;
	private SidWriteCompactLogger sid;

	@Before
	public void before() {
		mem = new C64SimulatorLineLogger();
		sid = new SidWriteCompactLogger(" ");
		delegator = new SidWriteDelegator(mem, sid);
	}

	@Test
	public void sidAndNonSidWritesAreDelegatedAsMemWrites() {
		delegator.writeMem(SidRegisterAddress.first().address().value() - 1,
				UnsignedByte.x01);
		delegator.writeMem(SidRegisterAddress.first().address().value(),
				UnsignedByte.x02);
		delegator.writeMem(SidRegisterAddress.last().address().value(),
				UnsignedByte.x03);
		delegator.writeMem(SidRegisterAddress.last().address().value() + 1,
				UnsignedByte.x04);

		assertEquals(
				"0:      ($D3FF [] <-#$01 (old:null)\n"
						+ "0:      ($D400 [] <-#$02 (old:null)\n"
						+ "0:      ($D418 [] <-#$03 (old:null)\n"
						+ "0:      ($D419 [] <-#$04 (old:null)\n" + "",
				mem.eventLog());
	}

	@Test
	public void sidWritesAreDelegatedToSidWriteListener() {
		// these don't affect the output:
		delegator.writeMem(SidRegisterAddress.first().address().value() - 1,
				UnsignedByte.x01);
		delegator.writeMem(SidRegisterAddress.last().address().value() + 1,
				UnsignedByte.x01);

		int v = 10;
		for (int a = SidRegisterAddress.first().address()
				.value(); a <= SidRegisterAddress.last().address()
						.value(); a++) {
			delegator.writeMem(a, UnsignedByte.from(v++));
		}

		assertEquals("00=0A 01=0B 02=0C 03=0D 04=0E 05=0F 06=10 "
				+ "07=11 08=12 09=13 0A=14 0B=15 0C=16 0D=17 "
				+ "0E=18 0F=19 10=1A 11=1B 12=1C 13=1D "
				+ "14=1E 15=1F 16=20 17=21 18=22 ", sid.toString());
	}

}
