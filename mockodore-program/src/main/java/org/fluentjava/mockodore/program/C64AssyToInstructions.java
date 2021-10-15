package org.fluentjava.mockodore.program;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.api.assylang.InstructionsLangOf;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusX;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusY;
import org.fluentjava.mockodore.model.addressing.ImmediateByte;
import org.fluentjava.mockodore.model.addressing.ImpliedOperand;
import org.fluentjava.mockodore.model.addressing.IndirectAbsRef;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.RelativeAddress;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.addressing.ZeroPageIndirectPlusY;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusX;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusXIndirect;
import org.fluentjava.mockodore.model.labels.LabelAddressHalfAsData;
import org.fluentjava.mockodore.model.labels.LabelLsb;
import org.fluentjava.mockodore.model.labels.LabelMsb;
import org.fluentjava.mockodore.model.labels.LabelPlusX;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.machine.Op.AbsOp;
import org.fluentjava.mockodore.model.machine.Op.AbsXOp;
import org.fluentjava.mockodore.model.machine.Op.AbsYOp;
import org.fluentjava.mockodore.model.machine.Op.ImmediateOp;
import org.fluentjava.mockodore.model.machine.Op.ImpliedOp;
import org.fluentjava.mockodore.model.machine.Op.IndirectOp;
import org.fluentjava.mockodore.model.machine.Op.ZeroPageIndirectPlusYOp;
import org.fluentjava.mockodore.model.machine.Op.ZeropageOp;
import org.fluentjava.mockodore.model.machine.Op.ZeropageXIndirectOp;
import org.fluentjava.mockodore.model.machine.Op.ZeropageXOp;

public class C64AssyToInstructions<T, END> extends
		InstructionsLangDelegator<T, END> implements C64AssyLangOf<T, END> {

	C64AssyToInstructions(InstructionsLangOf<?, END> d) {
		super(d);
	}

	@SuppressWarnings("unchecked")
	@Override
	T __(Object ignore) {
		return (T) this;
	}

	private T _i(Op op, Operand operand) {
		return bytes(new Instruction(op, operand));
	}

	private T _i(Op op, RelativeAddress operand) {
		return instr(op, operand);
	}

	private T i(ImpliedOp op) {
		return _i(op, ImpliedOperand.INSTANCE);
	}

	private T i(AbsXOp op, AbsRefPlusX address) {
		return _i(op, address);
	}

	private T i(AbsYOp op, AbsRefPlusY address) {
		return _i(op, address);
	}

	private T i(AbsOp op, AbsRef address) {
		return _i(op, address);
	}

	private T i(ImmediateOp op, int value) {
		return _i(op, new ImmediateByte(value));
	}

	private T i(ImmediateOp op, UnsignedByte value) {
		return _i(op, new ImmediateByte(value));
	}

	private T i(ImmediateOp op, LabelLsb value) {
		return _i(op, value);
	}

	private T i(ImmediateOp op, LabelMsb value) {
		return _i(op, value);
	}

	private T i(ZeropageOp op, ZeroPage address) {
		return _i(op, address);
	}

	private T i(ZeropageXOp op, ZeroPagePlusX address) {
		return _i(op, address);
	}

	private T i(ZeropageXIndirectOp op, ZeroPagePlusXIndirect address) {
		return _i(op, address);
	}

	private T i(ZeroPageIndirectPlusYOp op, ZeroPageIndirectPlusY address) {
		return _i(op, address);
	}

	private T i(IndirectOp op, IndirectAbsRef address) {
		return _i(op, address);
	}

	@Override
	public T emptyLine() {
		return bytes(new EmptyLine());
	}

	@Override
	public T commentLine(String comment) {
		return bytes(new CommentLine(comment));
	}

	@Override
	public T nop() {
		return i(Op.NOP);
	}

	@Override
	public T rts() {
		return i(Op.RTS);
	}

	@Override
	public T brk() {
		return i(Op.BRK);
	}

	@Override
	public T lda(ZeroPage address) {
		return i(Op.LDA_ZEROPAGE, address);
	}

	@Override
	public T lda(ZeroPagePlusX address) {
		return i(Op.LDA_ZEROPAGE_X, address);
	}

	@Override
	public T ldy(ZeroPagePlusX address) {
		return i(Op.LDY_ZEROPAGE_X, address);
	}

	@Override
	public T lsr(ZeroPage address) {
		return i(Op.LSR_ZEROPAGE, address);
	}

	@Override
	public T ror() {
		return i(Op.ROR);
	}

	@Override
	public T rol() {
		return i(Op.ROL);
	}

	@Override
	public T ror(AbsRef address) {
		return i(Op.ROR_ABS, address);
	}

	@Override
	public T ror(AbsRefPlusX address) {
		return i(Op.ROR_ABS_X, address);
	}

	@Override
	public T ror(ZeroPage address) {
		return i(Op.ROR_ZEROPAGE, address);
	}

	@Override
	public T lda(ZeroPageIndirectPlusY address) {
		return i(Op.LDA_ZEROPAGE_IND_Y, address);
	}

	@Override
	public T ldx(ZeroPage address) {
		return i(Op.LDX_ZEROPAGE, address);
	}

	@Override
	public T ldy(ZeroPage address) {
		return i(Op.LDY_ZEROPAGE, address);
	}

	@Override
	public T lda(Labeled address) {
		return lda(address.label());
	}

	@Override
	public T lda(AbsRef address) {
		return i(Op.LDA_ABS, address);
	}

	@Override
	public T lda(LabelLsb value) {
		return i(Op.LDA_IMMEDIATE, value);
	}

	@Override
	public T bit(AbsRef address) {
		return i(Op.BIT_ABS, address);
	}

	@Override
	public T ldx(LabelLsb value) {
		return i(Op.LDX_IMMEDIATE, value);
	}

	@Override
	public T ldy(LabelLsb value) {
		return i(Op.LDY_IMMEDIATE, value);
	}

	@Override
	public T lda(LabelMsb value) {
		return i(Op.LDA_IMMEDIATE, value);
	}

	@Override
	public T ldx(LabelMsb value) {
		return i(Op.LDX_IMMEDIATE, value);
	}

	@Override
	public T ldy(LabelMsb value) {
		return i(Op.LDY_IMMEDIATE, value);
	}

	@Override
	public T ldx(AbsRef address) {
		return i(Op.LDX_ABS, address);
	}

	@Override
	public T ldy(int value) {
		return i(Op.LDY_IMMEDIATE, value);
	}

	@Override
	public T ldy(Labeled address) {
		return ldy(address.label());
	}

	@Override
	public T ldy(AbsRef address) {
		return i(Op.LDY_ABS, address);
	}

	@Override
	public T ldy(AbsRefPlusX address) {
		return i(Op.LDY_ABS_X, address);
	}

	@Override
	public T cmp(AbsRef address) {
		return i(Op.CMP_ABS, address);
	}

	@Override
	public T cmp(ZeroPage address) {
		return i(Op.CMP_ZP, address);
	}

	@Override
	public T cmp(ZeroPageIndirectPlusY address) {
		return i(Op.CMP_ZP_IND_Y, address);
	}

	@Override
	public T cmp(ZeroPagePlusX address) {
		return i(Op.CMP_ZP_X, address);
	}

	@Override
	public T cmp(AbsRefPlusX address) {
		return i(Op.CMP_ABS_X, address);
	}

	@Override
	public T inc(AbsRefPlusX address) {
		return i(Op.INC_ABS_X, address);
	}

	@Override
	public T adc(AbsRefPlusX address) {
		return i(Op.ADC_ABS_X, address);
	}

	@Override
	public T sbc(AbsRefPlusX address) {
		return i(Op.SBC_ABS_X, address);
	}

	@Override
	public T ora(LabelPlusX address) {
		return i(Op.ORA_ABS_X, address);
	}

	@Override
	public T ora(AbsRef address) {
		return i(Op.ORA_ABS, address);
	}

	@Override
	public T ora(ZeroPage address) {
		return i(Op.ORA_ZP, address);
	}

	@Override
	public T cmp(AbsRefPlusY address) {
		return i(Op.CMP_ABS_Y, address);
	}

	@Override
	public T bmi(AbsRef address) {
		return _i(Op.BMI, RelativeAddress.to(address));
	}

	@Override
	public T bpl(AbsRef address) {
		return _i(Op.BPL, RelativeAddress.to(address));
	}

	@Override
	public T bne(AbsRef address) {
		return _i(Op.BNE, RelativeAddress.to(address));
	}

	@Override
	public T beq(AbsRef address) {
		return _i(Op.BEQ, RelativeAddress.to(address));
	}

	@Override
	public T bcc(AbsRef address) {
		return _i(Op.BCC, RelativeAddress.to(address));
	}

	@Override
	public T bcs(AbsRef address) {
		return _i(Op.BCS, RelativeAddress.to(address));
	}

	@Override
	public T bvc(AbsRef address) {
		return _i(Op.BVC, RelativeAddress.to(address));
	}

	@Override
	public T bvs(AbsRef address) {
		return _i(Op.BVS, RelativeAddress.to(address));
	}

	@Override
	public T stx(AbsRef address) {
		return i(Op.STX_ABS, address);
	}

	@Override
	public T sty(AbsRef address) {
		return i(Op.STY_ABS, address);
	}

	@Override
	public T sty(ZeroPage address) {
		return i(Op.STY_ZP, address);
	}

	@Override
	public T sta(AbsRefPlusX address) {
		return _i(Op.STA_ABS_X, address);
	}

	@Override
	public T sta(AbsRefPlusY address) {
		return _i(Op.STA_ABS_Y, address);
	}

	@Override
	public T sta(ZeroPage address) {
		return i(Op.STA_ZEROPAGE, address);
	}

	@Override
	public T stx(ZeroPage address) {
		return i(Op.STX_ZEROPAGE, address);
	}

	@Override
	public T adc(ZeroPage address) {
		return i(Op.ADC_ZP, address);
	}

	@Override
	public T adc(ZeroPageIndirectPlusY address) {
		return i(Op.ADC_ZP_INDIRECT_PLUS_Y, address);
	}

	@Override
	public T adc(ZeroPagePlusX address) {
		return i(Op.ADC_ZP_X, address);
	}

	@Override
	public T sbc(ZeroPage address) {
		return i(Op.SBC_ZP, address);
	}

	@Override
	public T sbc(ZeroPagePlusX address) {
		return i(Op.SBC_ZP_X, address);
	}

	@Override
	public T rol(ZeroPage address) {
		return i(Op.ROL_ZEROPAGE, address);
	}

	@Override
	public T sta(ZeroPagePlusX address) {
		return i(Op.STA_ZEROPAGE_X, address);
	}

	@Override
	public T sta(ZeroPagePlusXIndirect address) {
		return i(Op.STA_ZEROPAGE_X_INDIRECT, address);
	}

	@Override
	public T sta(ZeroPageIndirectPlusY address) {
		return i(Op.STA_ZP_INDIRECT_PLUS_Y, address);
	}

	@Override
	public T data(byte... bytes) {
		return bytes(Data.bytes(bytes));
	}

	@Override
	public T data(int... bytes) {
		return bytes(Data.bytes(bytes));
	}

	@Override
	public T data(UnsignedByte... bytes) {
		return bytes(Data.bytes(bytes));
	}

	@Override
	public T data(Labeled address) {
		return data(address.label());
	}

	@Override
	public T data(AbsRef address) {
		return bytes(AddressAsData.from(address));
	}

	@Override
	public T data(LabelLsb lsb) {
		return bytes(LabelAddressHalfAsData.from(lsb));
	}

	@Override
	public T data(LabelMsb msb) {
		return bytes(LabelAddressHalfAsData.from(msb));
	}

	@Override
	public T data(Op op) {
		data(op.byteValue());
		return commentAfterInstr(op.toString());
	}

	@Override
	public T lda(int immediateValue) {
		return i(Op.LDA_IMMEDIATE, immediateValue);
	}

	@Override
	public T lda(Op immediateValue) {
		i(Op.LDA_IMMEDIATE, immediateValue.byteValue());
		return commentAfterInstr(immediateValue.toString());
	}

	@Override
	public T lda(UnsignedByte immediateValue) {
		return i(Op.LDA_IMMEDIATE, immediateValue);
	}

	@Override
	public T eor(int immediateValue) {
		return i(Op.EOR_IMMEDIATE, immediateValue);
	}

	@Override
	public T inx() {
		return i(Op.INX);
	}

	@Override
	public T iny() {
		return i(Op.INY);
	}

	@Override
	public T lsr() {
		return i(Op.LSR);
	}

	@Override
	public T lsr(AbsRefPlusX address) {
		return i(Op.LSR_ABS_X, address);
	}

	@Override
	public T and(int immediateValue) {
		return i(Op.AND_IMMEDIATE, immediateValue);
	}

	@Override
	public T cmp(int immediateValue) {
		return i(Op.CMP_IMMEDIATE, immediateValue);
	}

	@Override
	public T cmp(UnsignedByte immediateValue) {
		return i(Op.CMP_IMMEDIATE, immediateValue);
	}

	@Override
	public T cpx(AbsRef address) {
		return i(Op.CPX_ABS, address);
	}

	@Override
	public T cpx(UnsignedByte immediateValue) {
		return i(Op.CPX_IMMEDIATE, immediateValue);
	}

	@Override
	public T cpx(int immediateValue) {
		return i(Op.CPX_IMMEDIATE, immediateValue);
	}

	@Override
	public T cpy(int immediateValue) {
		return i(Op.CPY_IMMEDIATE, immediateValue);
	}

	@Override
	public T ldx(int immediateValue) {
		return i(Op.LDX_IMMEDIATE, immediateValue);
	}

	@Override
	public T ldx(UnsignedByte immediateValue) {
		return i(Op.LDX_IMMEDIATE, immediateValue);
	}

	@Override
	public T ora(int immediateValue) {
		return i(Op.ORA_IMMEDIATE, immediateValue);
	}

	@Override
	public T nop(AbsRefPlusX address) {
		return i(Op.NOP_ABS_X, address);
	}

	@Override
	public T lda(AbsRefPlusX address) {
		return i(Op.LDA_ABS_X, address);
	}

	@Override
	public T lda(AbsRefPlusY address) {
		return i(Op.LDA_ABS_Y, address);
	}

	@Override
	public T ldx(AbsRefPlusY address) {
		return i(Op.LDX_ABS_Y, address);
	}

	@Override
	public T slo(ZeroPageIndirectPlusY address) {
		return i(Op.SLO_ZP_INDIRECT_PLUS_Y, address);
	}

	@Override
	public T and(AbsRefPlusX address) {
		return i(Op.AND_ABS_X, address);
	}

	@Override
	public T and(AbsRef address) {
		return i(Op.AND_ABS, address);
	}

	@Override
	public T dec(AbsRefPlusX address) {
		return i(Op.DEC_ABS_X, address);
	}

	@Override
	public T sta(Labeled address) {
		return sta(address.label());
	}

	@Override
	public T sta(AbsRef address) {
		return i(Op.STA_ABS, address);
	}

	@Override
	public T sei() {
		return i(Op.SEI);
	}

	@Override
	public T cli() {
		return i(Op.CLI);
	}

	@Override
	public T jmp(AbsRef address) {
		return _i(Op.JMP_ABS, address);
	}

	@Override
	public T jmp(IndirectAbsRef address) {
		return i(Op.JMP_INDIRECT, address);
	}

	@Override
	public T jsr(AbsRef address) {
		return i(Op.JSR, address);
	}

	@Override
	public T sec() {
		return i(Op.SEC);
	}

	@Override
	public T clc() {
		return i(Op.CLC);
	}

	@Override
	public T cld() {
		return i(Op.CLD);
	}

	@Override
	public T adc(AbsRef address) {
		return i(Op.ADC_ABS, address);
	}

	@Override
	public T sbc(AbsRef address) {
		return i(Op.SBC_ABS, address);
	}

	@Override
	public T adc(AbsRefPlusY address) {
		return i(Op.ADC_ABS_Y, address);
	}

	@Override
	public T sbc(AbsRefPlusY address) {
		return i(Op.SBC_ABS_Y, address);
	}

	@Override
	public T adc(int immediateValue) {
		return i(Op.ADC_IMMEDIATE, immediateValue);
	}

	@Override
	public T sbc(int immediateValue) {
		return i(Op.SBC_IMMEDIATE, immediateValue);
	}

	@Override
	public T inc(AbsRef address) {
		return i(Op.INC_ABS, address);
	}

	@Override
	public T inc(ZeroPage address) {
		return i(Op.INC_ZP, address);
	}

	@Override
	public T dec(ZeroPage address) {
		return i(Op.DEC_ZP, address);
	}

	@Override
	public T dec(ZeroPagePlusX address) {
		return i(Op.DEC_ZP_X, address);
	}

	@Override
	public T dec(AbsRef address) {
		return i(Op.DEC_ABS, address);
	}

	@Override
	public T dex() {
		return i(Op.DEX);
	}

	@Override
	public T dey() {
		return i(Op.DEY);
	}

	@Override
	public T pha() {
		return i(Op.PHA);
	}

	@Override
	public T pla() {
		return i(Op.PLA);
	}

	@Override
	public T asl() {
		return i(Op.ASL_IMPLIED);
	}

	@Override
	public T asl(AbsRef address) {
		return i(Op.ASL_ABS, address);
	}

	@Override
	public T asl(AbsRefPlusX address) {
		return i(Op.ASL_ABS_X, address);
	}

	@Override
	public T asl(ZeroPage address) {
		return i(Op.ASL_ZP, address);
	}

	@Override
	public T tax() {
		return i(Op.TAX);
	}

	@Override
	public T tay() {
		return i(Op.TAY);
	}

	@Override
	public T tya() {
		return i(Op.TYA);
	}

	@Override
	public T txa() {
		return i(Op.TXA);
	}

}
