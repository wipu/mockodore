package org.fluentjava.mockodore.program;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.api.assylang.Debug;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusX;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusY;
import org.fluentjava.mockodore.model.addressing.IndirectAbsRef;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.addressing.ZeroPageIndirectPlusY;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusX;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusXIndirect;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.LabelLsb;
import org.fluentjava.mockodore.model.labels.LabelMsb;
import org.fluentjava.mockodore.model.labels.LabelPlusX;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;

public class C64AssyLangProxy<T, END> implements C64AssyLangOf<T, END> {

	protected final C64AssyLangOf<?, END> out;

	public C64AssyLangProxy(C64AssyLangOf<?, END> out) {
		this.out = out;
	}

	@Override
	public END end() {
		return out.end();
	}

	protected T __(@SuppressWarnings("unused") Object ignore) {
		return __();
	}

	@SuppressWarnings("unchecked")
	protected T __() {
		return (T) this;
	}

	@Override
	public T startAddress(RawAddress startAddress) {
		return __(out.startAddress(startAddress));
	}

	@Override
	public T label(Label label) {
		return __(out.label(label));
	}

	@Override
	public T label(Labeled labeled) {
		return __(out.label(labeled));
	}

	@Override
	public T commentAfterInstr(String comment) {
		return __(out.commentAfterInstr(comment));
	}

	@Override
	public T debug(Debug debug) {
		return __(out.debug(debug));
	}

	@Override
	public T emptyLine() {
		return __(out.emptyLine());
	}

	@Override
	public T commentLine(String comment) {
		return __(out.commentLine(comment));
	}

	@Override
	public T nop() {
		return __(out.nop());
	}

	@Override
	public T rts() {
		return __(out.rts());
	}

	@Override
	public T brk() {
		return __(out.brk());
	}

	@Override
	public T lda(ZeroPage address) {
		return __(out.lda(address));
	}

	@Override
	public T lda(ZeroPagePlusX address) {
		return __(out.lda(address));
	}

	@Override
	public T ldy(ZeroPagePlusX address) {
		return __(out.ldy(address));
	}

	@Override
	public T lsr(ZeroPage address) {
		return __(out.lsr(address));
	}

	@Override
	public T ror(AbsRef address) {
		return __(out.ror(address));
	}

	@Override
	public T ror(AbsRefPlusX address) {
		return __(out.ror(address));
	}

	@Override
	public T ror() {
		return __(out.ror());
	}

	@Override
	public T rol() {
		return __(out.rol());
	}

	@Override
	public T ror(ZeroPage address) {
		return __(out.ror(address));
	}

	@Override
	public T lda(ZeroPageIndirectPlusY address) {
		return __(out.lda(address));
	}

	@Override
	public T ldx(ZeroPage address) {
		return __(out.ldx(address));
	}

	@Override
	public T ldy(ZeroPage address) {
		return __(out.ldy(address));
	}

	@Override
	public T lda(Labeled address) {
		return __(out.lda(address));
	}

	@Override
	public T lda(AbsRef address) {
		return __(out.lda(address));
	}

	@Override
	public T lda(LabelLsb value) {
		return __(out.lda(value));
	}

	@Override
	public T ldx(LabelLsb value) {
		return __(out.ldx(value));
	}

	@Override
	public T ldy(LabelLsb value) {
		return __(out.ldy(value));
	}

	@Override
	public T lda(LabelMsb value) {
		return __(out.lda(value));
	}

	@Override
	public T ldx(LabelMsb value) {
		return __(out.ldx(value));
	}

	@Override
	public T ldy(LabelMsb value) {
		return __(out.ldy(value));
	}

	@Override
	public T ldx(AbsRef address) {
		return __(out.ldx(address));
	}

	@Override
	public T ldy(int value) {
		return __(out.ldy(value));
	}

	@Override
	public T ldy(Labeled address) {
		return __(out.ldy(address));
	}

	@Override
	public T ldy(AbsRef address) {
		return __(out.ldy(address));
	}

	@Override
	public T ldy(AbsRefPlusX address) {
		return __(out.ldy(address));
	}

	@Override
	public T cmp(AbsRef address) {
		return __(out.cmp(address));
	}

	@Override
	public T cmp(AbsRefPlusX address) {
		return __(out.cmp(address));
	}

	@Override
	public T cmp(ZeroPage address) {
		return __(out.cmp(address));
	}

	@Override
	public T cmp(ZeroPageIndirectPlusY address) {
		return __(out.cmp(address));
	}

	@Override
	public T cmp(ZeroPagePlusX address) {
		return __(out.cmp(address));
	}

	@Override
	public T inc(AbsRefPlusX address) {
		return __(out.inc(address));
	}

	@Override
	public T adc(AbsRefPlusX address) {
		return __(out.adc(address));
	}

	@Override
	public T sbc(AbsRefPlusX address) {
		return __(out.sbc(address));
	}

	@Override
	public T ora(LabelPlusX address) {
		return __(out.ora(address));
	}

	@Override
	public T ora(AbsRef address) {
		return __(out.ora(address));
	}

	@Override
	public T ora(ZeroPage address) {
		return __(out.ora(address));
	}

	@Override
	public T cmp(AbsRefPlusY address) {
		return __(out.cmp(address));
	}

	@Override
	public T bmi(AbsRef address) {
		return __(out.bmi(address));
	}

	@Override
	public T bpl(AbsRef address) {
		return __(out.bpl(address));
	}

	@Override
	public T bne(AbsRef address) {
		return __(out.bne(address));
	}

	@Override
	public T beq(AbsRef address) {
		return __(out.beq(address));
	}

	@Override
	public T bcc(AbsRef address) {
		return __(out.bcc(address));
	}

	@Override
	public T bcs(AbsRef address) {
		return __(out.bcs(address));
	}

	@Override
	public T bvc(AbsRef address) {
		return __(out.bvc(address));
	}

	@Override
	public T bvs(AbsRef address) {
		return __(out.bvs(address));
	}

	@Override
	public T stx(AbsRef address) {
		return __(out.stx(address));
	}

	@Override
	public T sty(AbsRef address) {
		return __(out.sty(address));
	}

	@Override
	public T sty(ZeroPage address) {
		return __(out.sty(address));
	}

	@Override
	public T bit(AbsRef address) {
		return __(out.bit(address));
	}

	@Override
	public T sta(AbsRefPlusX address) {
		return __(out.sta(address));
	}

	@Override
	public T sta(AbsRefPlusY address) {
		return __(out.sta(address));
	}

	@Override
	public T sta(ZeroPage address) {
		return __(out.sta(address));
	}

	@Override
	public T stx(ZeroPage address) {
		return __(out.stx(address));
	}

	@Override
	public T adc(ZeroPage address) {
		return __(out.adc(address));
	}

	@Override
	public T adc(ZeroPageIndirectPlusY address) {
		return __(out.adc(address));
	}

	@Override
	public T adc(ZeroPagePlusX address) {
		return __(out.adc(address));
	}

	@Override
	public T sbc(ZeroPage address) {
		return __(out.sbc(address));
	}

	@Override
	public T sbc(ZeroPagePlusX address) {
		return __(out.sbc(address));
	}

	@Override
	public T rol(ZeroPage address) {
		return __(out.rol(address));
	}

	@Override
	public T sta(ZeroPagePlusX address) {
		return __(out.sta(address));
	}

	@Override
	public T sta(ZeroPagePlusXIndirect address) {
		return __(out.sta(address));
	}

	@Override
	public T sta(ZeroPageIndirectPlusY address) {
		return __(out.sta(address));
	}

	@Override
	public T data(byte... bytes) {
		return __(out.data(bytes));
	}

	@Override
	public T data(int... bytes) {
		return __(out.data(bytes));
	}

	@Override
	public T data(UnsignedByte... bytes) {
		return __(out.data(bytes));
	}

	@Override
	public T data(Labeled address) {
		return __(out.data(address));
	}

	@Override
	public T data(AbsRef address) {
		return __(out.data(address));
	}

	@Override
	public T data(LabelLsb lsb) {
		return __(out.data(lsb));
	}

	@Override
	public T data(LabelMsb msb) {
		return __(out.data(msb));
	}

	@Override
	public T data(Op op) {
		return __(out.data(op));
	}

	@Override
	public T lda(int immediateValue) {
		return __(out.lda(immediateValue));
	}

	@Override
	public T lda(Op immediateValue) {
		return __(out.lda(immediateValue));
	}

	@Override
	public T lda(UnsignedByte immediateValue) {
		return __(out.lda(immediateValue));
	}

	@Override
	public T eor(int immediateValue) {
		return __(out.eor(immediateValue));
	}

	@Override
	public T inx() {
		return __(out.inx());
	}

	@Override
	public T iny() {
		return __(out.iny());
	}

	@Override
	public T lsr() {
		return __(out.lsr());
	}

	@Override
	public T lsr(AbsRefPlusX address) {
		return __(out.lsr(address));
	}

	@Override
	public T and(int immediateValue) {
		return __(out.and(immediateValue));
	}

	@Override
	public T cmp(int immediateValue) {
		return __(out.cmp(immediateValue));
	}

	@Override
	public T cmp(UnsignedByte immediateValue) {
		return __(out.cmp(immediateValue));
	}

	@Override
	public T cpx(AbsRef address) {
		return __(out.cpx(address));
	}

	@Override
	public T cpx(UnsignedByte immediateValue) {
		return __(out.cpx(immediateValue));
	}

	@Override
	public T cpx(int immediateValue) {
		return __(out.cpx(immediateValue));
	}

	@Override
	public T cpy(int immediateValue) {
		return __(out.cpy(immediateValue));
	}

	@Override
	public T ldx(int immediateValue) {
		return __(out.ldx(immediateValue));
	}

	@Override
	public T ldx(UnsignedByte immediateValue) {
		return __(out.ldx(immediateValue));
	}

	@Override
	public T ora(int immediateValue) {
		return __(out.ora(immediateValue));
	}

	@Override
	public T nop(AbsRefPlusX address) {
		return __(out.nop(address));
	}

	@Override
	public T lda(AbsRefPlusX address) {
		return __(out.lda(address));
	}

	@Override
	public T lda(AbsRefPlusY address) {
		return __(out.lda(address));
	}

	@Override
	public T ldx(AbsRefPlusY address) {
		return __(out.ldx(address));
	}

	@Override
	public T slo(ZeroPageIndirectPlusY address) {
		return __(out.slo(address));
	}

	@Override
	public T and(AbsRefPlusX address) {
		return __(out.and(address));
	}

	@Override
	public T and(AbsRef address) {
		return __(out.and(address));
	}

	@Override
	public T dec(AbsRefPlusX address) {
		return __(out.dec(address));
	}

	@Override
	public T sta(Labeled address) {
		return __(out.sta(address));
	}

	@Override
	public T sta(AbsRef address) {
		return __(out.sta(address));
	}

	@Override
	public T sei() {
		return __(out.sei());
	}

	@Override
	public T cli() {
		return __(out.cli());
	}

	@Override
	public T jmp(AbsRef address) {
		return __(out.jmp(address));
	}

	@Override
	public T jmp(IndirectAbsRef address) {
		return __(out.jmp(address));
	}

	@Override
	public T jsr(AbsRef address) {
		return __(out.jsr(address));
	}

	@Override
	public T sec() {
		return __(out.sec());
	}

	@Override
	public T clc() {
		return __(out.clc());
	}

	@Override
	public T cld() {
		return __(out.cld());
	}

	@Override
	public T adc(AbsRef address) {
		return __(out.adc(address));
	}

	@Override
	public T sbc(AbsRef address) {
		return __(out.sbc(address));
	}

	@Override
	public T adc(AbsRefPlusY address) {
		return __(out.adc(address));
	}

	@Override
	public T sbc(AbsRefPlusY address) {
		return __(out.sbc(address));
	}

	@Override
	public T adc(int immediateValue) {
		return __(out.adc(immediateValue));
	}

	@Override
	public T sbc(int immediateValue) {
		return __(out.sbc(immediateValue));
	}

	@Override
	public T inc(AbsRef address) {
		return __(out.inc(address));
	}

	@Override
	public T inc(ZeroPage address) {
		return __(out.inc(address));
	}

	@Override
	public T dec(ZeroPage address) {
		return __(out.dec(address));
	}

	@Override
	public T dec(ZeroPagePlusX address) {
		return __(out.dec(address));
	}

	@Override
	public T dec(AbsRef address) {
		return __(out.dec(address));
	}

	@Override
	public T dex() {
		return __(out.dex());
	}

	@Override
	public T dey() {
		return __(out.dey());
	}

	@Override
	public T pha() {
		return __(out.pha());
	}

	@Override
	public T pla() {
		return __(out.pla());
	}

	@Override
	public T asl() {
		return __(out.asl());
	}

	@Override
	public T asl(AbsRef address) {
		return __(out.asl(address));
	}

	@Override
	public T asl(AbsRefPlusX address) {
		return __(out.asl(address));
	}

	@Override
	public T asl(ZeroPage address) {
		return __(out.asl(address));
	}

	@Override
	public T tax() {
		return __(out.tax());
	}

	@Override
	public T tay() {
		return __(out.tay());
	}

	@Override
	public T tya() {
		return __(out.tya());
	}

	@Override
	public T txa() {
		return __(out.txa());
	}

}
