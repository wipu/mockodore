package org.fluentjava.mockodore.simulator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.Debug;
import org.fluentjava.mockodore.api.assylang.DebugContext;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.ImmediateByte;
import org.fluentjava.mockodore.model.addressing.ImpliedOperand;
import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.RawAddressPlusX;
import org.fluentjava.mockodore.model.addressing.RawAddressPlusY;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.addressing.ZeroPageIndirectPlusY;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusX;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Accumulator;
import org.fluentjava.mockodore.model.machine.C64SimulatorEventListener;
import org.fluentjava.mockodore.model.machine.IndexRegisterX;
import org.fluentjava.mockodore.model.machine.IndexRegisterY;
import org.fluentjava.mockodore.model.machine.InstructionAndOperand;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.machine.Op.AbsOp;
import org.fluentjava.mockodore.model.machine.Op.AbsXOp;
import org.fluentjava.mockodore.model.machine.Op.AbsYOp;
import org.fluentjava.mockodore.model.machine.Op.ImmediateOp;
import org.fluentjava.mockodore.model.machine.Op.ImpliedOp;
import org.fluentjava.mockodore.model.machine.Op.IndirectOp;
import org.fluentjava.mockodore.model.machine.Op.RelativeOp;
import org.fluentjava.mockodore.model.machine.Op.ZeroPageIndirectPlusYOp;
import org.fluentjava.mockodore.model.machine.Op.ZeropageOp;
import org.fluentjava.mockodore.model.machine.Op.ZeropageXOp;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.model.machine.Register;
import org.fluentjava.mockodore.model.machine.StackPointer;
import org.fluentjava.mockodore.model.machine.StatusRegister;
import org.fluentjava.mockodore.program.Instruction;
import org.fluentjava.mockodore.program.MockodoreProgram;

public class C64Simulator {

	private final C64SimulatorEventListener listener;
	private final InstrInstance<? extends Op, ? extends Operand>[] instrByOp = new InstrInstance<?, ?>[256];
	private final Memory mem;
	private int time;
	private InstrInstance<?, ?> instr;
	private RawAddress pc;
	private boolean isSimpleSysRunning;
	private final Register sp;
	private final StatusRegister sr;
	private final Accumulator a;
	private MockodoreProgram program;
	private final Register y;
	private final Register x;
	private Map<Integer, List<Debug>> debugsByAddr = Collections.emptyMap();

	public C64Simulator() {
		this(new C64SimulatorSexprLogger(), new RawMemory());
	}

	public C64Simulator(C64SimulatorEventListener listener) {
		this(listener, new RawMemory());
	}

	public C64Simulator(C64SimulatorEventListener listener, Memory mem) {
		this.listener = listener;
		this.mem = mem;
		this.sp = new StackPointer(listener);
		this.sr = new StatusRegister(listener);
		this.a = new Accumulator(listener);
		this.y = new IndexRegisterY(listener);
		this.x = new IndexRegisterX(listener);
		initScreenMemory(mem);
		for (int i = 0; i < instrByOp.length; i++) {
			opImpl(new UnknownInstr(Op.unknownByValue(i)));
		}
		opImpl(new AdcAbs());
		opImpl(new AdcAbsPlusX());
		opImpl(new AdcAbsPlusY());
		opImpl(new AdcImmediate());
		opImpl(new AdcZp());
		opImpl(new AdcZpIndirectPlusY());
		opImpl(new AdcZpX());
		opImpl(new AndAbs());
		opImpl(new AndAbsPlusX());
		opImpl(new AndImm());
		opImpl(new AslImplied());
		opImpl(new AslAbs());
		opImpl(new AslAbsX());
		opImpl(new AslZp());
		opImpl(new Bcc());
		opImpl(new Bcs());
		opImpl(new Beq());
		opImpl(new BitAbs());
		opImpl(new Bmi());
		opImpl(new Bne());
		opImpl(new Bpl());
		opImpl(new Brk());
		opImpl(new Bvc());
		opImpl(new Bvs());
		opImpl(new Clc());
		opImpl(new Cld());
		opImpl(new Cli());
		opImpl(new CmpAbs());
		opImpl(new CmpAbsPlusX());
		opImpl(new CmpAbsPlusY());
		opImpl(new CmpImm());
		opImpl(new CmpZp());
		opImpl(new CmpZpIndirectPlusY());
		opImpl(new CmpZpX());
		opImpl(new CpxAbs());
		opImpl(new CpxImm());
		opImpl(new CpyImm());
		opImpl(new DecAbs());
		opImpl(new DecZp());
		opImpl(new DecZpX());
		opImpl(new DecAbsPlusX());
		opImpl(new Dex());
		opImpl(new Dey());
		opImpl(new EorImm());
		opImpl(new IncAbs());
		opImpl(new IncAbsPlusX());
		opImpl(new IncZp());
		opImpl(new Inx());
		opImpl(new Iny());
		opImpl(new JmpAbs());
		opImpl(new JmpIndirect());
		opImpl(new Jsr());
		opImpl(new LdaAbs());
		opImpl(new LdaAbsPlusX());
		opImpl(new LdaAbsPlusY());
		opImpl(new LdaImm());
		opImpl(new LdaZp());
		opImpl(new LdaZpIndirectPlusY());
		opImpl(new LdaZpX());
		opImpl(new LdxAbs());
		opImpl(new LdxAbsPlusY());
		opImpl(new LdxImm());
		opImpl(new LdxZp());
		opImpl(new LdyAbs());
		opImpl(new LdyAbsPlusX());
		opImpl(new LdyImm());
		opImpl(new LdyZp());
		opImpl(new LdyZpX());
		opImpl(new Lsr());
		opImpl(new LsrAbsX());
		opImpl(new LsrZp());
		opImpl(new Nop());
		opImpl(new NopAbsPlusX());
		opImpl(new OraAbs());
		opImpl(new OraAbsPlusX());
		opImpl(new OraImm());
		opImpl(new OraZp());
		opImpl(new Pha());
		opImpl(new Pla());
		opImpl(new Rol());
		opImpl(new RolZp());
		opImpl(new Ror());
		opImpl(new RorAbs());
		opImpl(new RorAbsX());
		opImpl(new RorZp());
		opImpl(new Rts());
		opImpl(new SbcAbs());
		opImpl(new SbcAbsPlusX());
		opImpl(new SbcAbsPlusY());
		opImpl(new SbcImmediate());
		opImpl(new SbcZp());
		opImpl(new SbcZpX());
		opImpl(new Sec());
		opImpl(new Sei());
		opImpl(new SloZpIndirectPlusY());
		opImpl(new StaAbs());
		opImpl(new StaAbsPlusX());
		opImpl(new StaAbsPlusY());
		opImpl(new StaZp());
		opImpl(new StaZpIndirectPlusY());
		opImpl(new StaZpPlusX());
		opImpl(new StxAbs());
		opImpl(new StxZp());
		opImpl(new StyAbs());
		opImpl(new StyZp());
		opImpl(new Tax());
		opImpl(new Tay());
		opImpl(new Txa());
		opImpl(new Tya());
		sp.set(UnsignedByte.xFF);
	}

	private static void initScreenMemory(Memory mem) {
		// simplified initial content:
		for (int i = 0; i < 40 * 25; i++) {
			mem.write(0x400 + i, UnsignedByte.x20); // space
		}
	}

	private void opImpl(InstrInstance<?, ?>... instrs) {
		for (InstrInstance<?, ?> instr : instrs) {
			instrByOp[instr.op().byteValue()] = instr;
		}
	}

	public void loadPrg(PrgBytesWithLoadAddress prg) {
		RawAddress startAddress = prg.address();
		int destAddress = startAddress.value();
		byte[] prgBytes = prg.justPrgBytes();
		for (int i = 0; i < prgBytes.length; i++) {
			UnsignedByte b = UnsignedByte.from(prgBytes[i]);
			mem.write(destAddress + i, b);
		}
		listener.loadPrg(prg);
	}

	public void load(MockodoreProgram program) {
		this.program = program;
		this.debugsByAddr = program.debugMap();
		loadPrg(program.asPrgBytes());
	}

	public void loadAndSimpleSysAndAutoTick(MockodoreProgram p) {
		load(p);
		simpleSysAndAutoTick();
	}

	public String hexDump(RawAddress start, int length) {
		return hexDump(start.value(), length);
	}

	public UnsignedByte valueIn(RawAddress address) {
		return readMem(address);
	}

	private String hexDump(int start, int length) {
		byte[] dump = new byte[length];
		for (int i = 0; i < length; i++) {
			dump[i] = mem.read(start + i).signedByte();
		}
		return ByteArrayPrettyPrinter.spaceSeparatedHex(dump);
	}

	public String hexDump(ZeroPage start, int length) {
		return hexDump(start.value().uInt(), length);
	}

	public String hexDump(Labeled start, int length) {
		return hexDump(start.label(), length);
	}

	public String hexDump(Label start, int length) {
		if (program == null) {
			throw new MockodoreException(
					"Cannot resolve label without program.");
		}
		return hexDump(program.addressOf(start), length);
	}

	/**
	 * Program counter
	 */
	public RawAddress pc() {
		return pc;
	}

	/**
	 * Stack pointer
	 */
	public Register sp() {
		return sp;
	}

	public StatusRegister sr() {
		return sr;
	}

	/**
	 * Accumulator
	 */
	public Accumulator a() {
		return a;
	}

	public Register y() {
		return y;
	}

	public Register x() {
		return x;
	}

	private void setPc(RawAddress address) {
		pc = address;
		UnsignedByte op = readMem(pc);
		InstrInstance<? extends Op, ? extends Operand> newInstr = instrByOp[op
				.uInt()];
		newInstr.start();
		startInstr(newInstr);
	}

	public void simpleSys(AbsRef address) {
		simpleSys(program.addressOf(address));
	}

	public void simpleSys(RawAddress address) {
		isSimpleSysRunning = true;
		listener.simpleSys(address);
		setPc(address);
	}

	public void simpleSys() {
		simpleSys(program.startAddress());
	}

	public void simpleSysAndAutoTick(RawAddress address) {
		simpleSys(address);
		while (isSimpleSysRunning()) {
			tick();
		}
	}

	public void simpleSysAndAutoTick(Label address) {
		simpleSysAndAutoTick(program.addressOf(address));
	}

	public void simpleSysAndAutoTick() {
		simpleSysAndAutoTick(program.startAddress());
	}

	private void startInstr(
			InstrInstance<? extends Op, ? extends Operand> instr) {
		this.instr = instr;
		listener.startInstr(pc, instr);
	}

	private final DebugContext debugContext = new DebugContext() {

		@Override
		public UnsignedByte readMem(AbsRef addr) {
			return C64Simulator.this.readMem(program.addressOf(addr));
		}

		@Override
		public void writeMem(AbsRef addr, UnsignedByte value) {
			spontaneouslyWrite(program.addressOf(addr), value);
		}

		@Override
		public Accumulator a() {
			return C64Simulator.this.a();
		}

		@Override
		public Register y() {
			return C64Simulator.this.y();
		}

		@Override
		public StatusRegister sr() {
			return C64Simulator.this.sr();
		}

	};

	public C64Simulator tick() {
		List<Debug> debugs = debugsByAddr.get(pc.value());
		if (debugs != null) {
			for (Debug debug : debugs) {
				debug.execute(debugContext);
			}
		}
		time++;
		listener.timeAdvanced(time);
		instr.tick();
		return this;
	}

	public C64Simulator tick(int cycles) {
		for (int i = 0; i < cycles; i++) {
			tick();
		}
		return this;
	}

	public C64Simulator tick(Op... ops) {
		for (Op op : ops) {
			tick(op.cycles());
		}
		return this;
	}

	public String recentEventLog() {
		// TODO this code is feature envy
		C64SimulatorSexprLogger sexprListener = (C64SimulatorSexprLogger) listener;
		String retval = sexprListener.eventLog();
		sexprListener.clearEventLog();
		return retval;
	}

	private abstract class InstrInstance<OP extends Op, OPERAND extends Operand>
			implements InstructionAndOperand<OP, OPERAND> {

		protected final OP op;
		private int finishesAt;

		InstrInstance(OP op) {
			this.op = op;
		}

		abstract void result();

		@Override
		public final OP op() {
			return op;
		}

		public final void start() {
			finishesAt = time + op.cycles();
		}

		public final void tick() {
			if (time >= finishesAt) {
				result();
				if (!op.writesPc()) {
					advanceToNextInstr();
				}
			}
		}

		protected void advanceToNextInstr() {
			setPc(pc.plus(op.length()));
		}

		@Override
		public final String toString() {
			StringBuilder b = new StringBuilder();
			new Instruction(op, operand()).toAssembler(b, "");
			return b.toString();
		}

	}

	private abstract class ImpliedInstrInstance
			extends InstrInstance<ImpliedOp, ImpliedOperand> {

		ImpliedInstrInstance(ImpliedOp op) {
			super(op);
		}

		@Override
		public ImpliedOperand operand() {
			return ImpliedOperand.INSTANCE;
		}

	}

	private abstract class ImmediateInstrInstance
			extends InstrInstance<ImmediateOp, ImmediateByte> {

		ImmediateInstrInstance(ImmediateOp op) {
			super(op);
		}

		@Override
		public ImmediateByte operand() {
			return new ImmediateByte(readMem(pc.plus(1)));
		}

	}

	private abstract class RelativeInstrInstance
			extends InstrInstance<RelativeOp, RawAddress> {

		RelativeInstrInstance(RelativeOp op) {
			super(op);
		}

		@Override
		public RawAddress operand() {
			UnsignedByte relAddress = readMem(pc.plus(1));
			int absAddress = pc().value() + relAddress.signedByte() + 2;
			return RawAddress.named(absAddress);
		}

	}

	private abstract class ZeroPageInstr
			extends InstrInstance<ZeropageOp, ZeroPage> {

		ZeroPageInstr(ZeropageOp op) {
			super(op);
		}

		@Override
		public ZeroPage operand() {
			return ZeroPage.from(readMem(pc.plus(1)));
		}

	}

	private abstract class ZeroPagePlusXInstr
			extends InstrInstance<ZeropageXOp, ZeroPagePlusX> {

		ZeroPagePlusXInstr(ZeropageXOp op) {
			super(op);
		}

		@Override
		public ZeroPagePlusX operand() {
			return new ZeroPagePlusX(ZeroPage.from(readMem(pc.plus(1))));
		}

	}

	private abstract class ZeroPageIndirectPlusYInstr extends
			InstrInstance<ZeroPageIndirectPlusYOp, ZeroPageIndirectPlusY> {

		ZeroPageIndirectPlusYInstr(ZeroPageIndirectPlusYOp op) {
			super(op);
		}

		@Override
		public ZeroPageIndirectPlusY operand() {
			return new ZeroPageIndirectPlusY(
					ZeroPage.from(readMem(pc.plus(1))));
		}

	}

	private abstract class AbsolutePlusX
			extends InstrInstance<AbsXOp, RawAddressPlusX> {

		AbsolutePlusX(AbsXOp op) {
			super(op);
		}

		@Override
		public RawAddressPlusX operand() {
			UnsignedByte argLsb = readMem(pc.plus(1));
			UnsignedByte argMsb = readMem(pc.plus(2));
			RawAddress arg = RawAddress.fromLsbAndMsb(argLsb, argMsb);
			return arg.plusX();
		}

	}

	private abstract class AbsolutePlusY
			extends InstrInstance<AbsYOp, RawAddressPlusY> {

		AbsolutePlusY(AbsYOp op) {
			super(op);
		}

		@Override
		public RawAddressPlusY operand() {
			UnsignedByte argLsb = readMem(pc.plus(1));
			UnsignedByte argMsb = readMem(pc.plus(2));
			RawAddress arg = RawAddress.fromLsbAndMsb(argLsb, argMsb);
			return arg.plusY();
		}

	}

	private UnsignedByte readMem(RawAddress address) {
		int addressValue = address.value();
		UnsignedByte value = mem.read(addressValue);
		listener.readMem(addressValue, value);
		return value;
	}

	private UnsignedByte readMem(ZeroPage address) {
		int addressValue = address.value().uInt();
		UnsignedByte value = mem.read(addressValue);
		listener.readMem(addressValue, value);
		return value;
	}

	private UnsignedByte readMem(ZeroPagePlusX address) {
		return readMem(address.address().plus(x.uInt()));
	}

	private UnsignedByte readMem(ZeroPageIndirectPlusY address) {
		UnsignedByte arrayLsb = readMem(address.address());
		UnsignedByte arrayMsb = readMem(address.address().plus(1));
		RawAddress array = RawAddress.fromLsbAndMsb(arrayLsb, arrayMsb);
		RawAddress indexed = array.plus(y.uInt());
		return readMem(indexed);
	}

	private UnsignedByte readMem(RawAddressPlusY addressPlusY) {
		RawAddress indexedAddress = addressPlusY.address().plus(y.uInt());
		return readMem(indexedAddress);
	}

	private UnsignedByte readMem(RawAddressPlusX addressPlusX) {
		RawAddress indexedAddress = addressPlusX.address().plus(x.uInt());
		return readMem(indexedAddress);
	}

	private abstract class IndirectInstance
			extends InstrInstance<IndirectOp, RawAddress> {

		IndirectInstance(IndirectOp op) {
			super(op);
		}

		@Override
		public RawAddress operand() {
			UnsignedByte argLsb = readMem(pc.plus(1));
			UnsignedByte argMsb = readMem(pc.plus(2));
			RawAddress arg = RawAddress.fromLsbAndMsb(argLsb, argMsb);

			UnsignedByte refLsb = readMem(arg);
			UnsignedByte refMsb = readMem(arg.plus(1));
			RawAddress ref = RawAddress.fromLsbAndMsb(refLsb, refMsb);

			return ref;
		}

	}

	private abstract class AbsInstrInstance
			extends InstrInstance<AbsOp, RawAddress> {

		AbsInstrInstance(AbsOp op) {
			super(op);
		}

		@Override
		public RawAddress operand() {
			UnsignedByte argLsb = readMem(pc.plus(1));
			UnsignedByte argMsb = readMem(pc.plus(2));
			RawAddress arg = RawAddress.fromLsbAndMsb(argLsb, argMsb);
			return arg;
		}

	}

	public boolean isSimpleSysRunning() {
		return isSimpleSysRunning;
	}

	private class UnknownInstr extends ImpliedInstrInstance {

		public UnknownInstr(ImpliedOp op) {
			super(op);
		}

		@Override
		void result() {
			throw new UnsupportedOperationException("Who called " + this);
		}

	}

	private class Nop extends ImpliedInstrInstance {

		Nop() {
			super(Op.NOP);
		}

		@Override
		void result() {
			// do nothing
		}

	}

	private void returnSimpleSys() {
		isSimpleSysRunning = false;
		listener.returnSimpleSys();
	}

	private class Rts extends ImpliedInstrInstance {

		Rts() {
			super(Op.RTS);
		}

		@Override
		void result() {
			if (0xFF == sp.uInt()) {
				returnSimpleSys();
			} else {
				setPc(popAddress().plus(1));
			}
		}

	}

	private class Brk extends ImpliedInstrInstance {

		Brk() {
			super(Op.BRK);
		}

		@Override
		void result() {
			throw new UnsupportedOperationException("TODO test and implement");
		}

	}

	private class JmpAbs extends AbsInstrInstance {

		JmpAbs() {
			super(Op.JMP_ABS);
		}

		@Override
		void result() {
			setPc(operand());
		}

	}

	private class JmpIndirect extends IndirectInstance {

		JmpIndirect() {
			super(Op.JMP_INDIRECT);
		}

		@Override
		void result() {
			setPc(operand());
		}

	}

	private class Jsr extends AbsInstrInstance {

		Jsr() {
			super(Op.JSR);
		}

		@Override
		void result() {
			RawAddress ret = pc.plus(2);
			push(ret);
			setPc(operand());
		}

	}

	public void push(RawAddress address) {
		push(UnsignedByte.from(address.msb()));
		push(UnsignedByte.from(address.lsb()));
	}

	public RawAddress popAddress() {
		UnsignedByte lsb = popByte();
		UnsignedByte msb = popByte();
		return RawAddress.fromLsbAndMsb(lsb, msb);
	}

	public void push(UnsignedByte b) {
		RawAddress to = RawAddress.fromLsbAndMsb(sp.signedByte(), (byte) 0x01);
		writeMem(to, b);
		// TODO how is stack full handled?
		writeSp((byte) (sp.uInt() - 1));
	}

	private void writeSp(byte newValue) {
		sp.setSignedByte(newValue);
	}

	public UnsignedByte popByte() {
		writeSp((byte) (sp.uInt() + 1));
		RawAddress from = RawAddress.fromLsbAndMsb(sp.signedByte(),
				(byte) 0x01);
		UnsignedByte value = readMem(from);
		return value;
	}

	private class Sec extends ImpliedInstrInstance {

		Sec() {
			super(Op.SEC);
		}

		@Override
		void result() {
			sr().setCarry(true);
		}

	}

	private class Sei extends ImpliedInstrInstance {

		Sei() {
			super(Op.SEI);
		}

		@Override
		void result() {
			System.err.println("Warning: ignoring SEI");
		}

	}

	private class Clc extends ImpliedInstrInstance {

		Clc() {
			super(Op.CLC);
		}

		@Override
		void result() {
			sr().setCarry(false);
		}

	}

	private class Cld extends ImpliedInstrInstance {

		Cld() {
			super(Op.CLD);
		}

		@Override
		void result() {
			System.err.println("Warning: ignoring CLD");
		}

	}

	private class Cli extends ImpliedInstrInstance {

		Cli() {
			super(Op.CLI);
		}

		@Override
		void result() {
			System.err.println("Warning: ignoring CLI");
		}

	}

	private class AdcAbs extends AbsInstrInstance {

		AdcAbs() {
			super(Op.ADC_ABS);
		}

		@Override
		void result() {
			UnsignedByte termValue = readMem(operand());
			addToAcc(termValue, true);
		}

	}

	private void addToAcc(UnsignedByte value, boolean updateV) {
		addToReg(value, a(), updateV);
	}

	private void addToReg(UnsignedByte value, Register reg, boolean updateV) {
		byte oldByte = reg.signedByte();
		int sum = reg.uInt() + value.uInt() + sr.carryAsZeroOrOne();
		sr().setCarry(sum > 255);
		writeReg(reg, UnsignedByte.fromLsbOf(sum));
		byte newByte = reg.signedByte();
		if (updateV) {
			sr().updateV(oldByte, newByte);
		}
	}

	private class AdcImmediate extends ImmediateInstrInstance {

		AdcImmediate() {
			super(Op.ADC_IMMEDIATE);
		}

		@Override
		void result() {
			UnsignedByte value = operand().value();
			addToAcc(value, true);
		}

	}

	private class SbcImmediate extends ImmediateInstrInstance {

		SbcImmediate() {
			super(Op.SBC_IMMEDIATE);
		}

		@Override
		void result() {
			UnsignedByte value = operand().value();
			sbc(value, true);
		}

	}

	private class LdaAbs extends AbsInstrInstance {

		LdaAbs() {
			super(Op.LDA_ABS);
		}

		@Override
		void result() {
			UnsignedByte fromValue = readMem(operand());
			writeA(fromValue);
		}

	}

	private class LdaZpX extends ZeroPagePlusXInstr {

		LdaZpX() {
			super(Op.LDA_ZEROPAGE_X);
		}

		@Override
		void result() {
			UnsignedByte fromValue = readMem(operand());
			writeA(fromValue);
		}

	}

	private class LdyZpX extends ZeroPagePlusXInstr {

		LdyZpX() {
			super(Op.LDY_ZEROPAGE_X);
		}

		@Override
		void result() {
			UnsignedByte fromValue = readMem(operand());
			writeY(fromValue);
		}

	}

	private class LdxAbs extends AbsInstrInstance {

		LdxAbs() {
			super(Op.LDX_ABS);
		}

		@Override
		void result() {
			UnsignedByte fromValue = readMem(operand());
			writeX(fromValue);
		}

	}

	private class LdaZp extends ZeroPageInstr {

		LdaZp() {
			super(Op.LDA_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			writeA(value);
		}

	}

	private class LdxZp extends ZeroPageInstr {

		LdxZp() {
			super(Op.LDX_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			writeX(value);
		}

	}

	private class LdyZp extends ZeroPageInstr {

		LdyZp() {
			super(Op.LDY_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			writeY(value);
		}

	}

	private class LdaZpIndirectPlusY extends ZeroPageIndirectPlusYInstr {

		LdaZpIndirectPlusY() {
			super(Op.LDA_ZEROPAGE_IND_Y);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			writeA(value);
		}

	}

	private class LdyAbs extends AbsInstrInstance {

		LdyAbs() {
			super(Op.LDY_ABS);
		}

		@Override
		void result() {
			UnsignedByte fromValue = readMem(operand());
			writeY(fromValue);
		}

	}

	private class LdaImm extends ImmediateInstrInstance {

		LdaImm() {
			super(Op.LDA_IMMEDIATE);
		}

		@Override
		void result() {
			writeA(operand().value());
		}

	}

	private class Inx extends ImpliedInstrInstance {

		Inx() {
			super(Op.INX);
		}

		@Override
		void result() {
			UnsignedByte result = x().value().plusSigned(1);
			writeX(result);
		}
	}

	private class Iny extends ImpliedInstrInstance {

		Iny() {
			super(Op.INY);
		}

		@Override
		void result() {
			UnsignedByte result = y().value().plusSigned(1);
			writeY(result);
		}
	}

	private class AndImm extends ImmediateInstrInstance {

		AndImm() {
			super(Op.AND_IMMEDIATE);
		}

		@Override
		void result() {
			UnsignedByte anded = a.value().and(operand().value());
			writeA(anded);
		}

	}

	private class CmpImm extends ImmediateInstrInstance {

		CmpImm() {
			super(Op.CMP_IMMEDIATE);
		}

		@Override
		void result() {
			compare(a(), operand().value());
		}

	}

	private class CpxAbs extends AbsInstrInstance {

		CpxAbs() {
			super(Op.CPX_ABS);
		}

		@Override
		void result() {
			UnsignedByte termValue = readMem(operand());
			compare(x(), termValue);
		}

	}

	private class CpxImm extends ImmediateInstrInstance {

		CpxImm() {
			super(Op.CPX_IMMEDIATE);
		}

		@Override
		void result() {
			compare(x(), operand().value());
		}

	}

	private class CpyImm extends ImmediateInstrInstance {

		CpyImm() {
			super(Op.CPY_IMMEDIATE);
		}

		@Override
		void result() {
			compare(y(), operand().value());
		}

	}

	private class LdxImm extends ImmediateInstrInstance {

		LdxImm() {
			super(Op.LDX_IMMEDIATE);
		}

		@Override
		void result() {
			writeX(operand().value());
		}

	}

	private class LdyImm extends ImmediateInstrInstance {

		LdyImm() {
			super(Op.LDY_IMMEDIATE);
		}

		@Override
		void result() {
			writeY(operand().value());
		}

	}

	private class OraImm extends ImmediateInstrInstance {

		OraImm() {
			super(Op.ORA_IMMEDIATE);
		}

		@Override
		void result() {
			UnsignedByte ored = a().value().or(operand().value());
			writeA(ored);
		}

	}

	private class EorImm extends ImmediateInstrInstance {

		EorImm() {
			super(Op.EOR_IMMEDIATE);
		}

		@Override
		void result() {
			UnsignedByte ored = a().value().eor(operand().value());
			writeA(ored);
		}

	}

	private void writeA(UnsignedByte newValue) {
		writeReg(a(), newValue);
	}

	private void writeX(UnsignedByte newValue) {
		writeReg(x(), newValue);
	}

	private void writeY(UnsignedByte newValue) {
		writeReg(y(), newValue);
	}

	private void writeReg(Register reg, UnsignedByte newValue) {
		reg.set(newValue);
		sr.setZero(newValue.isZero());
		sr.setNegative(newValue.isNegative());
	}

	public void spontaneouslyWrite(RawAddress address, UnsignedByte newValue) {
		writeMem(address.value(), newValue);
	}

	private void writeMem(RawAddress address, UnsignedByte newValue) {
		writeMem(address.value(), newValue);
	}

	private void writeMem(RawAddressPlusX address, UnsignedByte newValue) {
		writeMem(address.address().plus(x.uInt()), newValue);
	}

	private void writeMem(ZeroPagePlusX address, UnsignedByte newValue) {
		writeMem(address.address().plus(x.uInt()), newValue);
	}

	private void writeMem(RawAddressPlusY address, UnsignedByte newValue) {
		writeMem(address.address().plus(y.uInt()), newValue);
	}

	private void writeMem(ZeroPageIndirectPlusY address,
			UnsignedByte newValue) {
		UnsignedByte arrayLsb = readMem(address.address());
		UnsignedByte arrayMsb = readMem(address.address().plus(1));
		RawAddress array = RawAddress.fromLsbAndMsb(arrayLsb, arrayMsb);
		RawAddress indexed = array.plus(y.uInt());
		writeMem(indexed, newValue);
	}

	private void writeMem(ZeroPage address, UnsignedByte newValue) {
		writeMem(address.value().uInt(), newValue);
	}

	private void writeMem(int address, UnsignedByte newValue) {
		mem.write(address, newValue);
		listener.writeMem(address, newValue);
	}

	private class StaAbs extends AbsInstrInstance {

		StaAbs() {
			super(Op.STA_ABS);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class DecAbs extends AbsInstrInstance {

		DecAbs() {
			super(Op.DEC_ABS);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(-1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class IncAbs extends AbsInstrInstance {

		IncAbs() {
			super(Op.INC_ABS);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class IncZp extends ZeroPageInstr {

		IncZp() {
			super(Op.INC_ZP);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class DecZp extends ZeroPageInstr {

		DecZp() {
			super(Op.DEC_ZP);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(-1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class DecZpX extends ZeroPagePlusXInstr {

		DecZpX() {
			super(Op.DEC_ZP_X);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(-1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class StxAbs extends AbsInstrInstance {

		StxAbs() {
			super(Op.STX_ABS);
		}

		@Override
		void result() {
			writeMem(operand(), x.value());
		}

	}

	private class StyAbs extends AbsInstrInstance {

		StyAbs() {
			super(Op.STY_ABS);
		}

		@Override
		void result() {
			writeMem(operand(), y.value());
		}

	}

	private class StyZp extends ZeroPageInstr {

		StyZp() {
			super(Op.STY_ZP);
		}

		@Override
		void result() {
			writeMem(operand(), y.value());
		}

	}

	private class LdaAbsPlusX extends AbsolutePlusX {

		LdaAbsPlusX() {
			super(Op.LDA_ABS_X);
		}

		@Override
		void result() {
			writeA(readMem(operand()));
		}

	}

	private class NopAbsPlusX extends AbsolutePlusX {

		NopAbsPlusX() {
			super(Op.NOP_ABS_X);
		}

		@Override
		void result() {
			// nop
		}

	}

	private class CmpAbs extends AbsInstrInstance {

		CmpAbs() {
			super(Op.CMP_ABS);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private class CmpZp extends ZeroPageInstr {

		CmpZp() {
			super(Op.CMP_ZP);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private class CmpZpIndirectPlusY extends ZeroPageIndirectPlusYInstr {

		CmpZpIndirectPlusY() {
			super(Op.CMP_ZP_IND_Y);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private class CmpZpX extends ZeroPagePlusXInstr {

		CmpZpX() {
			super(Op.CMP_ZP_X);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private class CmpAbsPlusX extends AbsolutePlusX {

		CmpAbsPlusX() {
			super(Op.CMP_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private class CmpAbsPlusY extends AbsolutePlusY {

		CmpAbsPlusY() {
			super(Op.CMP_ABS_Y);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			compare(a(), value);
		}

	}

	private void compare(Register reg, UnsignedByte value) {
		UnsignedByte originalRegValue = reg.value();
		sr().setCarry(true);
		sbc(value, reg, false);
		reg.set(originalRegValue);
	}

	private class AndAbs extends AbsInstrInstance {

		AndAbs() {
			super(Op.AND_ABS);
		}

		@Override
		void result() {
			UnsignedByte memValue = readMem(operand());
			UnsignedByte anded = a().value().and(memValue);
			writeA(anded);
		}

	}

	private class AndAbsPlusX extends AbsolutePlusX {

		AndAbsPlusX() {
			super(Op.AND_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte memValue = readMem(operand());
			UnsignedByte anded = a().value().and(memValue);
			writeA(anded);
		}

	}

	private class BitAbs extends AbsInstrInstance {

		BitAbs() {
			super(Op.BIT_ABS);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			sr.setNegative(value.isBit7());
			sr.setOverflow(value.isBit6());
			sr.setZero(a().value().and(value).isZero());
		}

	}

	private class StaAbsPlusX extends AbsolutePlusX {

		StaAbsPlusX() {
			super(Op.STA_ABS_X);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class StaAbsPlusY extends AbsolutePlusY {

		StaAbsPlusY() {
			super(Op.STA_ABS_Y);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class IncAbsPlusX extends AbsolutePlusX {

		IncAbsPlusX() {
			super(Op.INC_ABS_X);
		}

		@Override
		void result() {
			// TODO remove redundancy
			UnsignedByte value = readMem(operand());
			value = value.plusSigned(1);
			writeMem(operand(), value);
			sr().setZero(value.isZero());
			sr().setNegative(value.isNegative());
		}

	}

	private class LdyAbsPlusX extends AbsolutePlusX {

		LdyAbsPlusX() {
			super(Op.LDY_ABS_X);
		}

		@Override
		void result() {
			writeY(readMem(operand()));
		}

	}

	private class AdcAbsPlusX extends AbsolutePlusX {

		AdcAbsPlusX() {
			super(Op.ADC_ABS_X);
		}

		@Override
		void result() {
			addToAcc(readMem(operand()), true);
		}

	}

	private class OraAbsPlusX extends AbsolutePlusX {

		OraAbsPlusX() {
			super(Op.ORA_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte result = a().value().or(readMem(operand()));
			writeA(result);
		}

	}

	private class OraAbs extends AbsInstrInstance {

		OraAbs() {
			super(Op.ORA_ABS);
		}

		@Override
		void result() {
			UnsignedByte result = a().value().or(readMem(operand()));
			writeA(result);
		}

	}

	private class OraZp extends ZeroPageInstr {

		OraZp() {
			super(Op.ORA_ZP);
		}

		@Override
		void result() {
			UnsignedByte result = a().value().or(readMem(operand()));
			writeA(result);
		}

	}

	private class LdaAbsPlusY extends AbsolutePlusY {

		LdaAbsPlusY() {
			super(Op.LDA_ABS_Y);
		}

		@Override
		void result() {
			writeA(readMem(operand()));
		}

	}

	private class LdxAbsPlusY extends AbsolutePlusY {

		LdxAbsPlusY() {
			super(Op.LDX_ABS_Y);
		}

		@Override
		void result() {
			writeX(readMem(operand()));
		}

	}

	private class DecAbsPlusX extends AbsolutePlusX {

		DecAbsPlusX() {
			super(Op.DEC_ABS_X);
		}

		@Override
		void result() {
			// TODO remove redundancy btw this and other addressing modes
			UnsignedByte oldValue = readMem(operand());
			UnsignedByte newValue = oldValue.plusSigned(-1);
			writeMem(operand(), newValue);
			sr().setZero(newValue.isZero());
			sr().setNegative(newValue.isNegative());
		}

	}

	private class AdcAbsPlusY extends AbsolutePlusY {

		AdcAbsPlusY() {
			super(Op.ADC_ABS_Y);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			addToAcc(value, true);
		}

	}

	private class SbcAbsPlusX extends AbsolutePlusX {

		SbcAbsPlusX() {
			super(Op.SBC_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			sbc(value, a(), true);
		}

	}

	private class SbcAbsPlusY extends AbsolutePlusY {

		SbcAbsPlusY() {
			super(Op.SBC_ABS_Y);
		}

		@Override
		void result() {
			UnsignedByte value = readMem(operand());
			sbc(value, a(), true);
		}

	}

	private class StaZp extends ZeroPageInstr {

		StaZp() {
			super(Op.STA_ZEROPAGE);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class StxZp extends ZeroPageInstr {

		StxZp() {
			super(Op.STX_ZEROPAGE);
		}

		@Override
		void result() {
			writeMem(operand(), x.value());
		}

	}

	private class Lsr extends ImpliedInstrInstance {

		Lsr() {
			super(Op.LSR);
		}

		@Override
		void result() {
			UnsignedByte value = lsr(a().value());
			writeA(value);
		}
	}

	private UnsignedByte lsr(UnsignedByte orig) {
		UnsignedByte result = UnsignedByte.from(orig.uInt() / 2);
		sr().setCarry(!orig.and(UnsignedByte.x01).isZero());
		sr().setNegative(result.isNegative());
		sr().setZero(result.isZero());
		return result;
	}

	private class LsrZp extends ZeroPageInstr {

		LsrZp() {
			super(Op.LSR_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = lsr(readMem(operand()));
			writeMem(operand(), value);
		}

	}

	private class LsrAbsX extends AbsolutePlusX {

		LsrAbsX() {
			super(Op.LSR_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte value = lsr(readMem(operand()));
			writeMem(operand(), value);
		}
	}

	private class AdcZp extends ZeroPageInstr {

		AdcZp() {
			super(Op.ADC_ZP);
		}

		@Override
		void result() {
			UnsignedByte term = readMem(operand());
			addToAcc(term, true);
		}

	}

	private class AdcZpIndirectPlusY extends ZeroPageIndirectPlusYInstr {

		AdcZpIndirectPlusY() {
			super(Op.ADC_ZP_INDIRECT_PLUS_Y);
		}

		@Override
		void result() {
			UnsignedByte term = readMem(operand());
			addToAcc(term, true);
		}

	}

	private class AdcZpX extends ZeroPagePlusXInstr {

		AdcZpX() {
			super(Op.ADC_ZP_X);
		}

		@Override
		void result() {
			UnsignedByte term = readMem(operand());
			addToAcc(term, true);
		}

	}

	private class SbcZp extends ZeroPageInstr {

		SbcZp() {
			super(Op.SBC_ZP);
		}

		@Override
		void result() {
			UnsignedByte term = readMem(operand());
			sbc(term, true);
		}

	}

	private class SbcZpX extends ZeroPagePlusXInstr {

		SbcZpX() {
			super(Op.SBC_ZP_X);
		}

		@Override
		void result() {
			UnsignedByte term = readMem(operand());
			sbc(term, true);
		}

	}

	private class Rol extends ImpliedInstrInstance {

		Rol() {
			super(Op.ROL);
		}

		@Override
		void result() {
			UnsignedByte value = rol(a().value());
			writeA(value);
		}

	}

	private UnsignedByte rol(UnsignedByte value) {
		int newValue = value.uInt() * 2 + sr().carryAsZeroOrOne();
		UnsignedByte newByte = UnsignedByte.fromLsbOf(newValue);
		sr().setZero(newByte.isZero());
		sr().setNegative(newByte.isNegative());
		sr().setCarry(newValue > 255);
		return newByte;
	}

	private class RolZp extends ZeroPageInstr {

		RolZp() {
			super(Op.ROL_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = rol(readMem(operand()));
			writeMem(operand(), value);
		}

	}

	private class Ror extends ImpliedInstrInstance {

		Ror() {
			super(Op.ROR);
		}

		@Override
		void result() {
			UnsignedByte value = ror(a().value());
			writeA(value);
		}

	}

	private UnsignedByte ror(UnsignedByte value) {
		int newValue = value.uInt() / 2 + 128 * sr().carryAsZeroOrOne();
		UnsignedByte newByte = UnsignedByte.fromLsbOf(newValue);
		sr().setZero(newByte.isZero());
		sr().setNegative(newByte.isNegative());
		sr().setCarry(!value.and(UnsignedByte.x01).isZero());
		return newByte;
	}

	private class RorZp extends ZeroPageInstr {

		RorZp() {
			super(Op.ROR_ZEROPAGE);
		}

		@Override
		void result() {
			UnsignedByte value = ror(readMem(operand()));
			writeMem(operand(), value);
		}

	}

	private class RorAbsX extends AbsolutePlusX {

		RorAbsX() {
			super(Op.ROR_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte value = ror(readMem(operand()));
			writeMem(operand(), value);
		}

	}

	private class RorAbs extends AbsInstrInstance {

		RorAbs() {
			super(Op.ROR_ABS);
		}

		@Override
		void result() {
			UnsignedByte value = ror(readMem(operand()));
			writeMem(operand(), value);
		}

	}

	private class StaZpIndirectPlusY extends ZeroPageIndirectPlusYInstr {

		StaZpIndirectPlusY() {
			super(Op.STA_ZP_INDIRECT_PLUS_Y);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class SloZpIndirectPlusY extends ZeroPageIndirectPlusYInstr {

		SloZpIndirectPlusY() {
			super(Op.SLO_ZP_INDIRECT_PLUS_Y);
		}

		@Override
		void result() {
			// TODO use MyByte for shift and track flags
			UnsignedByte newValue = UnsignedByte
					.fromLsbOf(readMem(operand()).signedByte() * 2);
			writeMem(operand(), newValue);
			writeA(a.value().or(newValue));
		}

	}

	private class StaZpPlusX extends ZeroPagePlusXInstr {

		public StaZpPlusX() {
			super(Op.STA_ZEROPAGE_X);
		}

		@Override
		void result() {
			writeMem(operand(), a.value());
		}

	}

	private class AslImplied extends ImpliedInstrInstance {

		AslImplied() {
			super(Op.ASL_IMPLIED);
		}

		@Override
		void result() {
			UnsignedByte src = a().value();
			writeA(aslUpdatingSr(src));
		}

	}

	private UnsignedByte aslUpdatingSr(UnsignedByte src) {
		UnsignedByte shifted = UnsignedByte.fromLsbOf(src.uInt() << 1);
		sr().setCarry(src.isBit7());
		sr().setNegative(shifted.isBit7());
		sr().setZero(shifted.equals(UnsignedByte.x00));
		return shifted;
	}

	private class AslAbsX extends AbsolutePlusX {

		AslAbsX() {
			super(Op.ASL_ABS_X);
		}

		@Override
		void result() {
			UnsignedByte src = readMem(operand());
			writeMem(operand(), aslUpdatingSr(src));
		}

	}

	private class AslAbs extends AbsInstrInstance {

		AslAbs() {
			super(Op.ASL_ABS);
		}

		@Override
		void result() {
			UnsignedByte src = readMem(operand());
			writeMem(operand(), aslUpdatingSr(src));
		}

	}

	private class AslZp extends ZeroPageInstr {

		AslZp() {
			super(Op.ASL_ZP);
		}

		@Override
		void result() {
			UnsignedByte src = readMem(operand());
			writeMem(operand(), aslUpdatingSr(src));
		}

	}

	private class Tax extends ImpliedInstrInstance {

		Tax() {
			super(Op.TAX);
		}

		@Override
		void result() {
			writeX(a.value());
		}

	}

	private class Tay extends ImpliedInstrInstance {

		Tay() {
			super(Op.TAY);
		}

		@Override
		void result() {
			writeY(a.value());
		}

	}

	private class Txa extends ImpliedInstrInstance {

		Txa() {
			super(Op.TXA);
		}

		@Override
		void result() {
			writeA(x.value());
		}

	}

	private class Tya extends ImpliedInstrInstance {

		Tya() {
			super(Op.TYA);
		}

		@Override
		void result() {
			writeA(y.value());
		}

	}

	private class Dex extends ImpliedInstrInstance {

		Dex() {
			super(Op.DEX);
		}

		@Override
		void result() {
			UnsignedByte newValue = x.value().plusSigned(-1);
			writeX(newValue);
		}

	}

	private class Dey extends ImpliedInstrInstance {

		Dey() {
			super(Op.DEY);
		}

		@Override
		void result() {
			UnsignedByte newValue = y.value().plusSigned(-1);
			writeY(newValue);
		}

	}

	private class Pha extends ImpliedInstrInstance {

		Pha() {
			super(Op.PHA);
		}

		@Override
		void result() {
			push(a().value());
		}

	}

	private class Pla extends ImpliedInstrInstance {

		Pla() {
			super(Op.PLA);
		}

		@Override
		void result() {
			writeA(popByte());
		}

	}

	private class Bmi extends RelativeInstrInstance {

		Bmi() {
			super(Op.BMI);
		}

		@Override
		void result() {
			if (sr.negative()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bpl extends RelativeInstrInstance {

		Bpl() {
			super(Op.BPL);
		}

		@Override
		void result() {
			if (!sr.negative()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bcc extends RelativeInstrInstance {

		Bcc() {
			super(Op.BCC);
		}

		@Override
		void result() {
			if (!sr.carry()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bcs extends RelativeInstrInstance {

		Bcs() {
			super(Op.BCS);
		}

		@Override
		void result() {
			if (sr.carry()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bvc extends RelativeInstrInstance {

		Bvc() {
			super(Op.BVC);
		}

		@Override
		void result() {
			if (!sr.overflow()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bvs extends RelativeInstrInstance {

		Bvs() {
			super(Op.BVS);
		}

		@Override
		void result() {
			if (sr.overflow()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Beq extends RelativeInstrInstance {

		Beq() {
			super(Op.BEQ);
		}

		@Override
		void result() {
			if (sr.zero()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class Bne extends RelativeInstrInstance {

		Bne() {
			super(Op.BNE);
		}

		@Override
		void result() {
			if (!sr.zero()) {
				setPc(operand());
			} else {
				advanceToNextInstr();
			}
		}

	}

	private class SbcAbs extends AbsInstrInstance {

		SbcAbs() {
			super(Op.SBC_ABS);
		}

		@Override
		void result() {
			UnsignedByte termValue = readMem(operand());
			sbc(termValue, true);
		}

	}

	private void sbc(UnsignedByte termValue, boolean updateV) {
		sbc(termValue, a(), updateV);
	}

	private void sbc(UnsignedByte termValue, Register reg, boolean updateV) {
		addToReg(termValue.eor(UnsignedByte.xFF), reg, updateV);
	}

}
