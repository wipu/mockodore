package org.fluentjava.mockodore.api.assylang;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusX;
import org.fluentjava.mockodore.model.addressing.AbsRefPlusY;
import org.fluentjava.mockodore.model.addressing.IndirectAbsRef;
import org.fluentjava.mockodore.model.addressing.ZeroPage;
import org.fluentjava.mockodore.model.addressing.ZeroPageIndirectPlusY;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusX;
import org.fluentjava.mockodore.model.addressing.ZeroPagePlusXIndirect;
import org.fluentjava.mockodore.model.labels.LabelLsb;
import org.fluentjava.mockodore.model.labels.LabelMsb;
import org.fluentjava.mockodore.model.labels.LabelPlusX;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;

public interface C64AssyLangOf<T, END> extends LabeledBytesLang<T, END> {

	T adc(AbsRef address);

	T adc(int immediateValue);

	T adc(AbsRefPlusX address);

	T adc(AbsRefPlusY address);

	T adc(ZeroPage address);

	T adc(ZeroPagePlusX address);

	T and(AbsRef address);

	T and(AbsRefPlusX address);

	T and(int immediateValue);

	T asl();

	T asl(AbsRef address);

	T asl(AbsRefPlusX address);

	T asl(ZeroPage address);

	T bcc(AbsRef address);

	T bcs(AbsRef address);

	T beq(AbsRef address);

	T bit(AbsRef address);

	T bmi(AbsRef address);

	T bne(AbsRef address);

	T bpl(AbsRef address);

	T brk();

	T bvc(AbsRef address);

	T bvs(AbsRef address);

	T clc();

	T cld();

	T cli();

	T cmp(AbsRef address);

	T cmp(int immediateValue);

	T cmp(AbsRefPlusX address);

	T cmp(AbsRefPlusY address);

	T cmp(UnsignedByte immediateValue);

	T cmp(ZeroPagePlusX address);

	T commentLine(String comment);

	T cpx(UnsignedByte immediateValue);

	T cpx(int immediateValue);

	T cpy(int immediateValue);

	T data(AbsRef address);

	T data(byte... bytes);

	T data(int... bytes);

	T data(Labeled address);

	T data(LabelLsb lsb);

	T data(LabelMsb msb);

	T data(Op op);

	T data(UnsignedByte... bytes);

	T dec(AbsRefPlusX address);

	T dec(AbsRef address);

	T dec(ZeroPage address);

	T dec(ZeroPagePlusX address);

	T dex();

	T dey();

	T emptyLine();

	T eor(int immediateValue);

	T inc(AbsRef address);

	T inc(AbsRefPlusX address);

	T inc(ZeroPage address);

	T inx();

	T iny();

	T jmp(AbsRef address);

	T jmp(IndirectAbsRef address);

	T jsr(AbsRef address);

	T lda(AbsRef address);

	T lda(AbsRefPlusX address);

	T lda(AbsRefPlusY address);

	T lda(int immediateValue);

	T lda(Labeled address);

	T lda(LabelLsb value);

	T lda(LabelMsb value);

	T lda(Op immediateValue);

	T lda(UnsignedByte immediateValue);

	T lda(ZeroPage address);

	T lda(ZeroPageIndirectPlusY address);

	T lda(ZeroPagePlusX address);

	T ldx(AbsRefPlusY address);

	T ldx(int immediateValue);

	T ldx(UnsignedByte immediateValue);

	T ldx(AbsRef address);

	T ldx(LabelLsb value);

	T ldx(LabelMsb value);

	T ldx(ZeroPage address);

	T ldy(int value);

	T ldy(AbsRef address);

	T ldy(Labeled address);

	T ldy(LabelLsb value);

	T ldy(LabelMsb value);

	T ldy(AbsRefPlusX address);

	T ldy(ZeroPage address);

	T ldy(ZeroPagePlusX address);

	T lsr();

	T lsr(AbsRefPlusX address);

	T lsr(ZeroPage address);

	T nop();

	T nop(AbsRefPlusX address);

	T ora(int immediateValue);

	T ora(AbsRef address);

	T ora(ZeroPage address);

	T ora(LabelPlusX address);

	T pha();

	T pla();

	T rol();

	T rol(ZeroPage address);

	T ror();

	T ror(AbsRef address);

	T ror(AbsRefPlusX address);

	T ror(ZeroPage address);

	T rts();

	T sbc(AbsRef address);

	T sbc(int immediateValue);

	T sbc(AbsRefPlusX address);

	T sbc(AbsRefPlusY address);

	T sbc(ZeroPage address);

	T sbc(ZeroPagePlusX address);

	T sec();

	T sei();

	T slo(ZeroPageIndirectPlusY address);

	T sta(AbsRef address);

	T sta(AbsRefPlusX address);

	T sta(AbsRefPlusY address);

	T sta(Labeled address);

	T sta(ZeroPage address);

	T sta(ZeroPageIndirectPlusY address);

	T sta(ZeroPagePlusX address);

	T sta(ZeroPagePlusXIndirect address);

	T stx(AbsRef address);

	T stx(ZeroPage address);

	T sty(AbsRef address);

	T sty(ZeroPage address);

	T tax();

	T tay();

	T txa();

	T tya();

}
