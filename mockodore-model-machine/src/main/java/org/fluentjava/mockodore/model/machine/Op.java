package org.fluentjava.mockodore.model.machine;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;

public abstract class Op {

	private static <T extends Op> T adc(OpcodeSpex<T> op) {
		return op.readsC().writesC().writesN().writesV().writesZ().end();
	}

	public static final AbsOp ADC_ABS = adc(
			absNamed("ADC").byteValue(0x6D).cycles(4));
	public static final AbsXOp ADC_ABS_X = adc(
			absXNamed("ADC").byteValue(0x7D).cyclesWithoutPageBoundary(4));
	public static final AbsYOp ADC_ABS_Y = adc(
			absYNamed("ADC").byteValue(0x79).cyclesWithoutPageBoundary(4));
	public static final ImmediateOp ADC_IMMEDIATE = adc(
			immediateNamed("ADC").byteValue(0x69).cycles(2));
	public static final ZeropageOp ADC_ZP = adc(
			zeropageNamed("ADC").byteValue(0x65).cycles(3));
	public static final ZeropageXOp ADC_ZP_X = adc(
			zeropageXNamed("ADC").byteValue(0x75).cycles(4));

	private static <T extends Op> T and(OpcodeSpex<T> op) {
		return op.writesN().writesZ().end();
	}

	public static final AbsOp AND_ABS = and(
			absNamed("AND").byteValue(0x2D).cycles(4));
	// TODO what is "+" in cycles according to C=lehti?
	public static final AbsXOp AND_ABS_X = and(
			absXNamed("AND").byteValue(0x3D).cycles(4));
	public static final ImmediateOp AND_IMMEDIATE = and(
			immediateNamed("AND").byteValue(0x29).cycles(2));

	private static <T extends Op> T asl(OpcodeSpex<T> op) {
		return op.writesC().writesN().writesZ().end();
	}

	public static final AbsOp ASL_ABS = asl(
			absNamed("ASL").byteValue(0x0E).cycles(6));
	public static final AbsXOp ASL_ABS_X = asl(
			absXNamed("ASL").byteValue(0x1E).cycles(7));
	public static final ImpliedOp ASL_IMPLIED = asl(
			impliedNamed("ASL").byteValue(0x0A).cycles(2));
	public static final ZeropageOp ASL_ZP = asl(
			zeropageNamed("ASL").byteValue(0x06).cycles(5));

	public static final RelativeOp BCC = relativeNamed("BCC").byteValue(0x90)
			.cyclesWithoutPageBoundary(2).readsC().writesPc().end();
	public static final RelativeOp BCS = relativeNamed("BCS").byteValue(0xB0)
			.cyclesWithoutPageBoundary(2).readsC().writesPc().end();
	public static final RelativeOp BEQ = relativeNamed("BEQ").byteValue(0xF0)
			.cyclesWithoutPageBoundary(2).readsZ().writesPc().end();
	public static final RelativeOp BMI = relativeNamed("BMI").byteValue(0x30)
			.cyclesWithoutPageBoundary(2).readsN().writesPc().end();
	public static final RelativeOp BNE = relativeNamed("BNE").byteValue(0xD0)
			.cyclesWithoutPageBoundary(2).readsZ().writesPc().end();
	public static final RelativeOp BPL = relativeNamed("BPL").byteValue(0x10)
			.cyclesWithoutPageBoundary(2).readsN().writesPc().end();

	private static <T extends Op> T bit(OpcodeSpex<T> op) {
		return op.writesN().writesV().writesZ().end();
	}

	public static final AbsOp BIT_ABS = bit(
			absNamed("BIT").byteValue(0x2C).cycles(4));

	public static final ImpliedOp BRK = impliedNamed("BRK").byteValue(0x00)
			.cycles(7).writesB().setsI().end();

	public static final RelativeOp BVC = relativeNamed("BVC").byteValue(0x50)
			.cyclesWithoutPageBoundary(2).readsV().writesPc().end();

	public static final RelativeOp BVS = relativeNamed("BVS").byteValue(0x70)
			.cyclesWithoutPageBoundary(2).readsV().writesPc().end();

	public static final ImpliedOp CLC = impliedNamed("CLC").byteValue(0x18)
			.cycles(2).writesC().end();
	public static final ImpliedOp CLD = impliedNamed("CLD").byteValue(0xD8)
			.cycles(2).end();
	public static final ImpliedOp CLI = impliedNamed("CLI").byteValue(0x58)
			.cycles(2).end();

	private static <T extends Op> T cmp(OpcodeSpex<T> op) {
		return op.writesC().writesN().writesZ().end();
	}

	public static final AbsOp CMP_ABS = cmp(
			absNamed("CMP").byteValue(0xCD).cycles(4));
	public static final AbsXOp CMP_ABS_X = cmp(
			absXNamed("CMP").byteValue(0xDD).cyclesWithoutPageBoundary(4));
	public static final AbsYOp CMP_ABS_Y = cmp(
			absYNamed("CMP").byteValue(0xD9).cyclesWithoutPageBoundary(4));
	public static final ImmediateOp CMP_IMMEDIATE = cmp(
			immediateNamed("CMP").byteValue(0xC9).cycles(2));
	public static final ZeroPageIndirectPlusYOp CMP_ZP_IND_Y = cmp(
			zeropageIndirectPlusYNamed("CMP").byteValue(0xD1).cycles(5));
	public static final ZeropageOp CMP_ZP = cmp(
			zeropageNamed("CMP").byteValue(0xC5).cycles(3));
	public static final ZeropageXOp CMP_ZP_X = cmp(
			zeropageXNamed("CMP").byteValue(0xD5).cycles(4));

	public static final AbsOp CPX_ABS = absNamed("CPX").writesC().writesN()
			.writesZ().byteValue(0xEC).cycles(4).end();
	public static final ImmediateOp CPX_IMMEDIATE = immediateNamed("CPX")
			.writesC().writesN().writesZ().byteValue(0xE0).cycles(2).end();

	public static final ImmediateOp CPY_IMMEDIATE = immediateNamed("CPY")
			.writesC().writesN().writesZ().byteValue(0xC0).cycles(2).end();

	public static final AbsOp DEC_ABS = absNamed("DEC").byteValue(0xCE)
			.cycles(6).writesN().writesZ().end();
	public static final AbsXOp DEC_ABS_X = absXNamed("DEC").writesN().writesZ()
			.byteValue(0xDE).cycles(7).end();
	public static final ZeropageOp DEC_ZP = zeropageNamed("DEC").byteValue(0xC6)
			.cycles(5).writesN().writesZ().end();
	public static final ZeropageXOp DEC_ZP_X = zeropageXNamed("DEC")
			.byteValue(0xD6).cycles(6).writesN().writesZ().end();

	public static final ImpliedOp DEX = impliedNamed("DEX").byteValue(0xCA)
			.cycles(2).writesN().writesZ().end();

	public static final ImpliedOp DEY = impliedNamed("DEY").byteValue(0x88)
			.cycles(2).writesN().writesZ().end();

	public static final ImmediateOp EOR_IMMEDIATE = immediateNamed("EOR")
			.writesN().writesZ().byteValue(0x49).cycles(2).end();

	public static final AbsOp INC_ABS = absNamed("INC").byteValue(0xEE)
			.cycles(6).writesN().writesZ().end();
	public static final AbsXOp INC_ABS_X = absXNamed("INC").writesN().writesZ()
			.byteValue(0xFE).cyclesWithoutPageBoundary(4).end();
	public static final ZeropageOp INC_ZP = zeropageNamed("INC").byteValue(0xE6)
			.cycles(5).writesN().writesZ().end();

	public static final ImpliedOp INX = impliedNamed("INX").byteValue(0xE8)
			.cycles(2).writesN().writesZ().end();

	public static final ImpliedOp INY = impliedNamed("INY").byteValue(0xC8)
			.cycles(2).writesN().writesZ().end();

	public static final AbsOp JMP_ABS = absNamed("JMP").byteValue(0x4C)
			.writesPc().cycles(3).end();
	public static final IndirectOp JMP_INDIRECT = indirectNamed("JMP")
			.byteValue(0x6C).writesPc().cycles(5).end();

	public static final AbsOp JSR = absNamed("JSR").byteValue(0x20).writesPc()
			.cycles(6).end();

	public static final AbsOp LDA_ABS = absNamed("LDA").writesN().writesZ()
			.byteValue(0xAD).cycles(4).end();
	public static final AbsXOp LDA_ABS_X = absXNamed("LDA").writesN().writesZ()
			.byteValue(0xBD).cyclesWithoutPageBoundary(4).end();
	public static final AbsYOp LDA_ABS_Y = absYNamed("LDA").writesN().writesZ()
			.byteValue(0xB9).cyclesWithoutPageBoundary(4).end();
	public static final ImmediateOp LDA_IMMEDIATE = immediateNamed("LDA")
			.writesN().writesZ().byteValue(0xA9).cycles(2).end();
	public static final ZeropageOp LDA_ZEROPAGE = zeropageNamed("LDA").writesN()
			.writesZ().byteValue(0xA5).cycles(3).end();
	public static final ZeropageXOp LDA_ZEROPAGE_X = zeropageXNamed("LDA")
			.writesN().writesZ().byteValue(0xB5).cycles(4).end();
	public static final ZeroPageIndirectPlusYOp LDA_ZEROPAGE_IND_Y = zeropageIndirectPlusYNamed(
			"LDA").writesN().writesZ().byteValue(0xB1)
					.cyclesWithoutPageBoundary(5).end();

	public static final AbsOp LDX_ABS = absNamed("LDX").writesN().writesZ()
			.byteValue(0xAE).cycles(4).end();
	public static final AbsYOp LDX_ABS_Y = absYNamed("LDX").writesN().writesZ()
			.byteValue(0xBE).cyclesWithoutPageBoundary(4).end();
	public static final ImmediateOp LDX_IMMEDIATE = immediateNamed("LDX")
			.writesN().writesZ().byteValue(0xA2).cycles(2).end();
	public static final ZeropageOp LDX_ZEROPAGE = zeropageNamed("LDX").writesN()
			.writesZ().byteValue(0xA6).cycles(3).end();

	public static final AbsOp LDY_ABS = absNamed("LDY").writesN().writesZ()
			.byteValue(0xAC).cycles(4).end();
	public static final AbsXOp LDY_ABS_X = absXNamed("LDY").writesN().writesZ()
			.byteValue(0xBC).cyclesWithoutPageBoundary(4).end();
	public static final ImmediateOp LDY_IMMEDIATE = immediateNamed("LDY")
			.writesN().writesZ().byteValue(0xA0).cycles(2).end();
	public static final ZeropageOp LDY_ZEROPAGE = zeropageNamed("LDY").writesN()
			.writesZ().byteValue(0xA4).cycles(3).end();
	public static final ZeropageXOp LDY_ZEROPAGE_X = zeropageXNamed("LDY")
			.writesN().writesZ().byteValue(0xB4).cycles(4).end();

	public static final ImpliedOp LSR = impliedNamed("LSR").byteValue(0x4A)
			.cycles(2).writesN().writesZ().writesC().readsC().end();
	public static final AbsXOp LSR_ABS_X = absXNamed("LSR").byteValue(0x5E)
			.cycles(7).writesN().writesZ().writesC().readsC().end();
	public static final ZeropageOp LSR_ZEROPAGE = zeropageNamed("LSR").writesN()
			.writesZ().writesC().readsC().byteValue(0x46).cycles(5).end();

	public static final ImpliedOp NOP = impliedNamed("NOP").byteValue(0xEA)
			.cycles(2).end();
	public static final AbsXOp NOP_ABS_X = absXNamed("NOP").byteValue(0xFC)
			.cyclesWithoutPageBoundary(4).end();

	public static final AbsOp ORA_ABS = absNamed("ORA").writesN().writesZ()
			.byteValue(0x0D).cyclesWithoutPageBoundary(4).end();
	public static final AbsXOp ORA_ABS_X = absXNamed("ORA").writesN().writesZ()
			.byteValue(0x1D).cyclesWithoutPageBoundary(4).end();
	public static final ZeropageOp ORA_ZP = zeropageNamed("ORA").writesN()
			.writesZ().byteValue(0x05).cycles(3).end();
	public static final ImmediateOp ORA_IMMEDIATE = immediateNamed("ORA")
			.writesN().writesZ().byteValue(0x09).cycles(2).end();

	public static final ImpliedOp PHA = impliedNamed("PHA").byteValue(0x48)
			.cycles(3).end();

	public static final ImpliedOp PLA = impliedNamed("PLA").byteValue(0x68)
			.cycles(4).writesN().writesZ().end();

	public static final ImpliedOp ROL = impliedNamed("ROL").writesN().writesZ()
			.writesC().byteValue(0x2A).cycles(2).end();
	public static final ZeropageOp ROL_ZEROPAGE = zeropageNamed("ROL").writesN()
			.writesZ().writesC().byteValue(0x26).cycles(5).end();

	public static final ImpliedOp ROR = impliedNamed("ROR").writesN().writesZ()
			.writesC().byteValue(0x6A).cycles(2).end();
	public static final AbsOp ROR_ABS = absNamed("ROR").writesN().writesZ()
			.writesC().byteValue(0x6E).cycles(6).end();
	public static final AbsXOp ROR_ABS_X = absXNamed("ROR").writesN().writesZ()
			.writesC().byteValue(0x7E).cycles(7).end();
	public static final ZeropageOp ROR_ZEROPAGE = zeropageNamed("ROR").writesN()
			.writesZ().writesC().byteValue(0x66).cycles(5).end();

	public static final ImpliedOp RTS = impliedNamed("RTS").byteValue(0x60)
			.writesPc().cycles(6).end();

	public static final AbsOp SBC_ABS = absNamed("SBC").byteValue(0xED)
			.cycles(4).writesN().writesV().writesZ().writesC().readsC().end();
	public static final AbsXOp SBC_ABS_X = absXNamed("SBC").writesN().writesC()
			.readsC().writesV().writesZ().byteValue(0xFD)
			.cyclesWithoutPageBoundary(4).end();
	public static final AbsYOp SBC_ABS_Y = absYNamed("SBC").byteValue(0xF9)
			.cyclesWithoutPageBoundary(4).writesN().writesV().writesZ()
			.writesC().readsC().end();
	public static final ImmediateOp SBC_IMMEDIATE = immediateNamed("SBC")
			.byteValue(0xE9).cycles(2).writesN().writesV().writesZ().writesC()
			.readsC().end();
	public static final ZeropageOp SBC_ZP = zeropageNamed("SBC").byteValue(0xE5)
			.cycles(3).writesN().writesV().writesZ().writesC().readsC().end();
	public static final ZeropageXOp SBC_ZP_X = zeropageXNamed("SBC")
			.byteValue(0xF5).cycles(4).writesN().writesV().writesZ().writesC()
			.readsC().end();

	public static final ImpliedOp SEC = impliedNamed("SEC").byteValue(0x38)
			.cycles(2).writesC().end();

	public static final ImpliedOp SEI = impliedNamed("SEI").byteValue(0x78)
			.cycles(2).end();

	public static final ZeroPageIndirectPlusYOp SLO_ZP_INDIRECT_PLUS_Y = zeropageIndirectPlusYNamed(
			"SLO").byteValue(0x13).cycles(8).writesN().writesZ().writesC()
					.undocumented().end();

	public static final AbsOp STA_ABS = absNamed("STA").byteValue(0x8D)
			.cycles(4).end();
	public static final AbsXOp STA_ABS_X = absXNamed("STA").byteValue(0x9D)
			.cycles(5).end();
	public static final AbsYOp STA_ABS_Y = absYNamed("STA").byteValue(0x99)
			.cycles(5).end();
	public static final ZeropageOp STA_ZEROPAGE = zeropageNamed("STA")
			.byteValue(0x85).cycles(3).end();
	public static final ZeroPageIndirectPlusYOp STA_ZP_INDIRECT_PLUS_Y = zeropageIndirectPlusYNamed(
			"STA").byteValue(0x91).cycles(6).end();
	public static final ZeropageXOp STA_ZEROPAGE_X = zeropageXNamed("STA")
			.byteValue(0x95).cycles(4).end();
	public static final ZeropageXIndirectOp STA_ZEROPAGE_X_INDIRECT = zeropageXIndirectNamed(
			"STA").byteValue(0x81).cycles(6).end();

	public static final AbsOp STX_ABS = absNamed("STX").byteValue(0x8E)
			.cycles(4).end();
	public static final ZeropageOp STX_ZEROPAGE = zeropageNamed("STX")
			.byteValue(0x86).cycles(3).end();

	public static final AbsOp STY_ABS = absNamed("STY").byteValue(0x8C)
			.cycles(4).end();
	public static final ZeropageOp STY_ZP = zeropageNamed("STY").byteValue(0x84)
			.cycles(3).end();

	public static final ImpliedOp TAX = impliedNamed("TAX").byteValue(0xAA)
			.cycles(2).writesN().writesZ().end();
	public static final ImpliedOp TAY = impliedNamed("TAY").byteValue(0xA8)
			.cycles(2).writesN().writesZ().end();
	public static final ImpliedOp TXA = impliedNamed("TXA").byteValue(0x8A)
			.cycles(2).writesN().writesZ().end();
	public static final ImpliedOp TYA = impliedNamed("TYA").byteValue(0x98)
			.cycles(2).writesN().writesZ().end();

	private final String name;
	private final int byteValue;
	private final Integer cycles;
	private final ReadAndWrite rw;

	public Op(String name, int byteValue, Integer cycles, ReadAndWrite rw) {
		this.name = name;
		this.byteValue = byteValue;
		this.cycles = cycles;
		this.rw = rw;
	}

	public String name() {
		return name;
	}

	public int byteValue() {
		return byteValue;
	}

	public abstract int length();

	public Integer cycles() {
		return cycles;
	}

	public boolean writesB() {
		return rw.writesB;
	}

	public boolean writesI() {
		return rw.writesI;
	}

	public boolean writesN() {
		return rw.writesN;
	}

	public boolean writesZ() {
		return rw.writesZ;
	}

	public boolean writesV() {
		return rw.writesV;
	}

	public boolean writesC() {
		return rw.writesC;
	}

	public boolean writesPc() {
		return rw.writesPc;
	}

	public boolean readsC() {
		return rw.readsC;
	}

	public boolean readsZ() {
		return rw.readsZ;
	}

	private static ImmediateOpSpex immediateNamed(String name) {
		return new ImmediateOpSpex(name);
	}

	private static ImpliedOpSpex impliedNamed(String name) {
		return new ImpliedOpSpex(name);
	}

	private static AbsOpSpex absNamed(String name) {
		return new AbsOpSpex(name);
	}

	private static AbsXOpSpex absXNamed(String name) {
		return new AbsXOpSpex(name);
	}

	private static AbsYOpSpex absYNamed(String name) {
		return new AbsYOpSpex(name);
	}

	private static IndirectOpSpex indirectNamed(String name) {
		return new IndirectOpSpex(name);
	}

	private static ZeropageOpSpex zeropageNamed(String name) {
		return new ZeropageOpSpex(name);
	}

	private static ZeropageXOpSpex zeropageXNamed(String name) {
		return new ZeropageXOpSpex(name);
	}

	private static ZeropageXIndirectOpSpex zeropageXIndirectNamed(String name) {
		return new ZeropageXIndirectOpSpex(name);
	}

	private static ZeropageIndirectPlusYOpSpex zeropageIndirectPlusYNamed(
			String name) {
		return new ZeropageIndirectPlusYOpSpex(name);
	}

	private static RelativeOpSpex relativeNamed(String name) {
		return new RelativeOpSpex(name);
	}

	public static ImpliedOp unknownByValue(int value) {
		return impliedNamed(
				"?:" + ByteArrayPrettyPrinter.spaceSeparatedHex((byte) value))
						.byteValue(value).cycles(1).end();
	}

	public static class ImmediateOp extends Op {

		public ImmediateOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

		@Override
		public String toString() {
			return name() + " i";
		}

	}

	public static class ImpliedOp extends Op {

		public ImpliedOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 1;
		}

		@Override
		public String toString() {
			return name();
		}

	}

	public static class AbsOp extends Op {

		public AbsOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 3;
		}

		@Override
		public String toString() {
			return name() + " abs";
		}

	}

	public static class AbsXOp extends Op {

		public AbsXOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 3;
		}

		@Override
		public String toString() {
			return name() + " abs,X";
		}

	}

	public static class AbsYOp extends Op {

		public AbsYOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 3;
		}

		@Override
		public String toString() {
			return name() + " abs,Y";
		}

	}

	public static class IndirectOp extends Op {

		public IndirectOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 3;
		}

		@Override
		public String toString() {
			return name() + " (ind)";
		}

	}

	public static class ZeropageOp extends Op {

		public ZeropageOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

	}

	public static class ZeropageXOp extends Op {

		public ZeropageXOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

	}

	public static class ZeropageXIndirectOp extends Op {

		public ZeropageXIndirectOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

	}

	public static class ZeroPageIndirectPlusYOp extends Op {

		public ZeroPageIndirectPlusYOp(String name, int byteValue,
				Integer cycles, ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

	}

	public static class RelativeOp extends Op {

		public RelativeOp(String name, int byteValue, Integer cycles,
				ReadAndWrite rw) {
			super(name, byteValue, cycles, rw);
		}

		@Override
		public int length() {
			return 2;
		}

	}

	private static class ReadAndWrite {

		boolean writesB;
		boolean writesI;
		boolean writesZ;
		boolean writesN;
		boolean writesV;
		boolean writesC;
		boolean writesPc;
		boolean readsC;
		public boolean readsZ;
	}

	private static abstract class OpcodeSpex<T extends Op> {

		protected String name;
		protected int byteValue;
		protected Integer cycles;
		protected final ReadAndWrite rw = new ReadAndWrite();

		public OpcodeSpex(String name) {
			this.name = name;
		}

		OpcodeSpex<T> byteValue(int byteValue) {
			this.byteValue = byteValue;
			return this;
		}

		public OpcodeSpex<T> cycles(int cycles) {
			this.cycles = cycles;
			return this;
		}

		public OpcodeSpex<T> cyclesWithoutPageBoundary(int cycles) {
			this.cycles = cycles;
			// TODO mark page boundary +1
			return this;
		}

		public OpcodeSpex<T> undocumented() {
			// TODO save
			return this;
		}

		public OpcodeSpex<T> writesZ() {
			rw.writesZ = true;
			return this;
		}

		public OpcodeSpex<T> writesN() {
			rw.writesN = true;
			return this;
		}

		public OpcodeSpex<T> writesV() {
			rw.writesV = true;
			return this;
		}

		public OpcodeSpex<T> writesC() {
			rw.writesC = true;
			return this;
		}

		public OpcodeSpex<T> writesPc() {
			rw.writesPc = true;
			return this;
		}

		public OpcodeSpex<T> readsC() {
			rw.readsC = true;
			return this;
		}

		public OpcodeSpex<T> writesB() {
			rw.writesB = true;
			return this;
		}

		public OpcodeSpex<T> readsZ() {
			rw.readsZ = true;
			return this;
		}

		public OpcodeSpex<T> readsN() {
			// TODO save
			return this;
		}

		public OpcodeSpex<T> readsV() {
			// TODO save
			return this;
		}

		public OpcodeSpex<T> setsI() {
			// TODO save
			return this;
		}

		abstract T end();

	}

	private static class ImmediateOpSpex extends OpcodeSpex<ImmediateOp> {

		public ImmediateOpSpex(String name) {
			super(name);
		}

		@Override
		ImmediateOp end() {
			return new ImmediateOp(name, byteValue, cycles, rw);
		}

	}

	private static class ImpliedOpSpex extends OpcodeSpex<ImpliedOp> {

		public ImpliedOpSpex(String name) {
			super(name);
		}

		@Override
		ImpliedOp end() {
			return new ImpliedOp(name, byteValue, cycles, rw);
		}

	}

	private static class AbsOpSpex extends OpcodeSpex<AbsOp> {

		public AbsOpSpex(String name) {
			super(name);
		}

		@Override
		AbsOp end() {
			return new AbsOp(name, byteValue, cycles, rw);
		}

	}

	private static class AbsXOpSpex extends OpcodeSpex<AbsXOp> {

		public AbsXOpSpex(String name) {
			super(name);
		}

		@Override
		AbsXOp end() {
			return new AbsXOp(name, byteValue, cycles, rw);
		}

	}

	private static class AbsYOpSpex extends OpcodeSpex<AbsYOp> {

		public AbsYOpSpex(String name) {
			super(name);
		}

		@Override
		AbsYOp end() {
			return new AbsYOp(name, byteValue, cycles, rw);
		}

	}

	private static class IndirectOpSpex extends OpcodeSpex<IndirectOp> {

		public IndirectOpSpex(String name) {
			super(name);
		}

		@Override
		IndirectOp end() {
			return new IndirectOp(name, byteValue, cycles, rw);
		}

	}

	private static class ZeropageOpSpex extends OpcodeSpex<ZeropageOp> {

		public ZeropageOpSpex(String name) {
			super(name);
		}

		@Override
		ZeropageOp end() {
			return new ZeropageOp(name, byteValue, cycles, rw);
		}

	}

	private static class ZeropageXOpSpex extends OpcodeSpex<ZeropageXOp> {

		public ZeropageXOpSpex(String name) {
			super(name);
		}

		@Override
		ZeropageXOp end() {
			return new ZeropageXOp(name, byteValue, cycles, rw);
		}

	}

	private static class ZeropageXIndirectOpSpex
			extends OpcodeSpex<ZeropageXIndirectOp> {

		public ZeropageXIndirectOpSpex(String name) {
			super(name);
		}

		@Override
		ZeropageXIndirectOp end() {
			return new ZeropageXIndirectOp(name, byteValue, cycles, rw);
		}

	}

	private static class ZeropageIndirectPlusYOpSpex
			extends OpcodeSpex<ZeroPageIndirectPlusYOp> {

		public ZeropageIndirectPlusYOpSpex(String name) {
			super(name);
		}

		@Override
		ZeroPageIndirectPlusYOp end() {
			return new ZeroPageIndirectPlusYOp(name, byteValue, cycles, rw);
		}

	}

	private static class RelativeOpSpex extends OpcodeSpex<RelativeOp> {

		public RelativeOpSpex(String name) {
			super(name);
		}

		@Override
		RelativeOp end() {
			return new RelativeOp(name, byteValue, cycles, rw);
		}

	}

}
