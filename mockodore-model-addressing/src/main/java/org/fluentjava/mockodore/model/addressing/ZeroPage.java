package org.fluentjava.mockodore.model.addressing;

import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

public class ZeroPage implements Operand {

	private static final ZeroPage[] values = new ZeroPage[256];

	static {
		for (int i = 0; i < 256; i++) {
			values[i] = new ZeroPage(i);
		}
	}

	public static final ZeroPage $00 = ZeroPage.from(0);
	public static final ZeroPage $01 = ZeroPage.from(1);
	public static final ZeroPage $02 = ZeroPage.from(2);
	public static final ZeroPage $03 = ZeroPage.from(3);
	public static final ZeroPage $04 = ZeroPage.from(4);
	public static final ZeroPage $05 = ZeroPage.from(5);
	public static final ZeroPage $06 = ZeroPage.from(6);
	public static final ZeroPage $07 = ZeroPage.from(7);
	public static final ZeroPage $08 = ZeroPage.from(8);
	public static final ZeroPage $09 = ZeroPage.from(9);
	public static final ZeroPage $0A = ZeroPage.from(10);
	public static final ZeroPage $0B = ZeroPage.from(11);
	public static final ZeroPage $0C = ZeroPage.from(12);
	public static final ZeroPage $0D = ZeroPage.from(13);
	public static final ZeroPage $0E = ZeroPage.from(14);
	public static final ZeroPage $0F = ZeroPage.from(15);
	public static final ZeroPage $10 = ZeroPage.from(16);
	public static final ZeroPage $11 = ZeroPage.from(17);
	public static final ZeroPage $12 = ZeroPage.from(18);
	public static final ZeroPage $13 = ZeroPage.from(19);
	public static final ZeroPage $14 = ZeroPage.from(20);
	public static final ZeroPage $15 = ZeroPage.from(21);
	public static final ZeroPage $16 = ZeroPage.from(22);
	public static final ZeroPage $17 = ZeroPage.from(23);
	public static final ZeroPage $18 = ZeroPage.from(24);
	public static final ZeroPage $19 = ZeroPage.from(25);
	public static final ZeroPage $1A = ZeroPage.from(26);
	public static final ZeroPage $1B = ZeroPage.from(27);
	public static final ZeroPage $1C = ZeroPage.from(28);
	public static final ZeroPage $1D = ZeroPage.from(29);
	public static final ZeroPage $1E = ZeroPage.from(30);
	public static final ZeroPage $1F = ZeroPage.from(31);
	public static final ZeroPage $20 = ZeroPage.from(32);
	public static final ZeroPage $21 = ZeroPage.from(33);
	public static final ZeroPage $22 = ZeroPage.from(34);
	public static final ZeroPage $23 = ZeroPage.from(35);
	public static final ZeroPage $24 = ZeroPage.from(36);
	public static final ZeroPage $25 = ZeroPage.from(37);
	public static final ZeroPage $26 = ZeroPage.from(38);
	public static final ZeroPage $27 = ZeroPage.from(39);
	public static final ZeroPage $28 = ZeroPage.from(40);
	public static final ZeroPage $29 = ZeroPage.from(41);
	public static final ZeroPage $2A = ZeroPage.from(42);
	public static final ZeroPage $2B = ZeroPage.from(43);
	public static final ZeroPage $2C = ZeroPage.from(44);
	public static final ZeroPage $2D = ZeroPage.from(45);
	public static final ZeroPage $2E = ZeroPage.from(46);
	public static final ZeroPage $2F = ZeroPage.from(47);
	public static final ZeroPage $30 = ZeroPage.from(48);
	public static final ZeroPage $31 = ZeroPage.from(49);
	public static final ZeroPage $32 = ZeroPage.from(50);
	public static final ZeroPage $33 = ZeroPage.from(51);
	public static final ZeroPage $34 = ZeroPage.from(52);
	public static final ZeroPage $35 = ZeroPage.from(53);
	public static final ZeroPage $36 = ZeroPage.from(54);
	public static final ZeroPage $37 = ZeroPage.from(55);
	public static final ZeroPage $38 = ZeroPage.from(56);
	public static final ZeroPage $39 = ZeroPage.from(57);
	public static final ZeroPage $3A = ZeroPage.from(58);
	public static final ZeroPage $3B = ZeroPage.from(59);
	public static final ZeroPage $3C = ZeroPage.from(60);
	public static final ZeroPage $3D = ZeroPage.from(61);
	public static final ZeroPage $3E = ZeroPage.from(62);
	public static final ZeroPage $3F = ZeroPage.from(63);
	public static final ZeroPage $40 = ZeroPage.from(64);
	public static final ZeroPage $41 = ZeroPage.from(65);
	public static final ZeroPage $42 = ZeroPage.from(66);
	public static final ZeroPage $43 = ZeroPage.from(67);
	public static final ZeroPage $44 = ZeroPage.from(68);
	public static final ZeroPage $45 = ZeroPage.from(69);
	public static final ZeroPage $46 = ZeroPage.from(70);
	public static final ZeroPage $47 = ZeroPage.from(71);
	public static final ZeroPage $48 = ZeroPage.from(72);
	public static final ZeroPage $49 = ZeroPage.from(73);
	public static final ZeroPage $4A = ZeroPage.from(74);
	public static final ZeroPage $4B = ZeroPage.from(75);
	public static final ZeroPage $4C = ZeroPage.from(76);
	public static final ZeroPage $4D = ZeroPage.from(77);
	public static final ZeroPage $4E = ZeroPage.from(78);
	public static final ZeroPage $4F = ZeroPage.from(79);
	public static final ZeroPage $50 = ZeroPage.from(80);
	public static final ZeroPage $51 = ZeroPage.from(81);
	public static final ZeroPage $52 = ZeroPage.from(82);
	public static final ZeroPage $53 = ZeroPage.from(83);
	public static final ZeroPage $54 = ZeroPage.from(84);
	public static final ZeroPage $55 = ZeroPage.from(85);
	public static final ZeroPage $56 = ZeroPage.from(86);
	public static final ZeroPage $57 = ZeroPage.from(87);
	public static final ZeroPage $58 = ZeroPage.from(88);
	public static final ZeroPage $59 = ZeroPage.from(89);
	public static final ZeroPage $5A = ZeroPage.from(90);
	public static final ZeroPage $5B = ZeroPage.from(91);
	public static final ZeroPage $5C = ZeroPage.from(92);
	public static final ZeroPage $5D = ZeroPage.from(93);
	public static final ZeroPage $5E = ZeroPage.from(94);
	public static final ZeroPage $5F = ZeroPage.from(95);
	public static final ZeroPage $60 = ZeroPage.from(96);
	public static final ZeroPage $61 = ZeroPage.from(97);
	public static final ZeroPage $62 = ZeroPage.from(98);
	public static final ZeroPage $63 = ZeroPage.from(99);
	public static final ZeroPage $64 = ZeroPage.from(100);
	public static final ZeroPage $65 = ZeroPage.from(101);
	public static final ZeroPage $66 = ZeroPage.from(102);
	public static final ZeroPage $67 = ZeroPage.from(103);
	public static final ZeroPage $68 = ZeroPage.from(104);
	public static final ZeroPage $69 = ZeroPage.from(105);
	public static final ZeroPage $6A = ZeroPage.from(106);
	public static final ZeroPage $6B = ZeroPage.from(107);
	public static final ZeroPage $6C = ZeroPage.from(108);
	public static final ZeroPage $6D = ZeroPage.from(109);
	public static final ZeroPage $6E = ZeroPage.from(110);
	public static final ZeroPage $6F = ZeroPage.from(111);
	public static final ZeroPage $70 = ZeroPage.from(112);
	public static final ZeroPage $71 = ZeroPage.from(113);
	public static final ZeroPage $72 = ZeroPage.from(114);
	public static final ZeroPage $73 = ZeroPage.from(115);
	public static final ZeroPage $74 = ZeroPage.from(116);
	public static final ZeroPage $75 = ZeroPage.from(117);
	public static final ZeroPage $76 = ZeroPage.from(118);
	public static final ZeroPage $77 = ZeroPage.from(119);
	public static final ZeroPage $78 = ZeroPage.from(120);
	public static final ZeroPage $79 = ZeroPage.from(121);
	public static final ZeroPage $7A = ZeroPage.from(122);
	public static final ZeroPage $7B = ZeroPage.from(123);
	public static final ZeroPage $7C = ZeroPage.from(124);
	public static final ZeroPage $7D = ZeroPage.from(125);
	public static final ZeroPage $7E = ZeroPage.from(126);
	public static final ZeroPage $7F = ZeroPage.from(127);
	public static final ZeroPage $80 = ZeroPage.from(128);
	public static final ZeroPage $81 = ZeroPage.from(129);
	public static final ZeroPage $82 = ZeroPage.from(130);
	public static final ZeroPage $83 = ZeroPage.from(131);
	public static final ZeroPage $84 = ZeroPage.from(132);
	public static final ZeroPage $85 = ZeroPage.from(133);
	public static final ZeroPage $86 = ZeroPage.from(134);
	public static final ZeroPage $87 = ZeroPage.from(135);
	public static final ZeroPage $88 = ZeroPage.from(136);
	public static final ZeroPage $89 = ZeroPage.from(137);
	public static final ZeroPage $8A = ZeroPage.from(138);
	public static final ZeroPage $8B = ZeroPage.from(139);
	public static final ZeroPage $8C = ZeroPage.from(140);
	public static final ZeroPage $8D = ZeroPage.from(141);
	public static final ZeroPage $8E = ZeroPage.from(142);
	public static final ZeroPage $8F = ZeroPage.from(143);
	public static final ZeroPage $90 = ZeroPage.from(144);
	public static final ZeroPage $91 = ZeroPage.from(145);
	public static final ZeroPage $92 = ZeroPage.from(146);
	public static final ZeroPage $93 = ZeroPage.from(147);
	public static final ZeroPage $94 = ZeroPage.from(148);
	public static final ZeroPage $95 = ZeroPage.from(149);
	public static final ZeroPage $96 = ZeroPage.from(150);
	public static final ZeroPage $97 = ZeroPage.from(151);
	public static final ZeroPage $98 = ZeroPage.from(152);
	public static final ZeroPage $99 = ZeroPage.from(153);
	public static final ZeroPage $9A = ZeroPage.from(154);
	public static final ZeroPage $9B = ZeroPage.from(155);
	public static final ZeroPage $9C = ZeroPage.from(156);
	public static final ZeroPage $9D = ZeroPage.from(157);
	public static final ZeroPage $9E = ZeroPage.from(158);
	public static final ZeroPage $9F = ZeroPage.from(159);
	public static final ZeroPage $A0 = ZeroPage.from(160);
	public static final ZeroPage $A1 = ZeroPage.from(161);
	public static final ZeroPage $A2 = ZeroPage.from(162);
	public static final ZeroPage $A3 = ZeroPage.from(163);
	public static final ZeroPage $A4 = ZeroPage.from(164);
	public static final ZeroPage $A5 = ZeroPage.from(165);
	public static final ZeroPage $A6 = ZeroPage.from(166);
	public static final ZeroPage $A7 = ZeroPage.from(167);
	public static final ZeroPage $A8 = ZeroPage.from(168);
	public static final ZeroPage $A9 = ZeroPage.from(169);
	public static final ZeroPage $AA = ZeroPage.from(170);
	public static final ZeroPage $AB = ZeroPage.from(171);
	public static final ZeroPage $AC = ZeroPage.from(172);
	public static final ZeroPage $AD = ZeroPage.from(173);
	public static final ZeroPage $AE = ZeroPage.from(174);
	public static final ZeroPage $AF = ZeroPage.from(175);
	public static final ZeroPage $B0 = ZeroPage.from(176);
	public static final ZeroPage $B1 = ZeroPage.from(177);
	public static final ZeroPage $B2 = ZeroPage.from(178);
	public static final ZeroPage $B3 = ZeroPage.from(179);
	public static final ZeroPage $B4 = ZeroPage.from(180);
	public static final ZeroPage $B5 = ZeroPage.from(181);
	public static final ZeroPage $B6 = ZeroPage.from(182);
	public static final ZeroPage $B7 = ZeroPage.from(183);
	public static final ZeroPage $B8 = ZeroPage.from(184);
	public static final ZeroPage $B9 = ZeroPage.from(185);
	public static final ZeroPage $BA = ZeroPage.from(186);
	public static final ZeroPage $BB = ZeroPage.from(187);
	public static final ZeroPage $BC = ZeroPage.from(188);
	public static final ZeroPage $BD = ZeroPage.from(189);
	public static final ZeroPage $BE = ZeroPage.from(190);
	public static final ZeroPage $BF = ZeroPage.from(191);
	public static final ZeroPage $C0 = ZeroPage.from(192);
	public static final ZeroPage $C1 = ZeroPage.from(193);
	public static final ZeroPage $C2 = ZeroPage.from(194);
	public static final ZeroPage $C3 = ZeroPage.from(195);
	public static final ZeroPage $C4 = ZeroPage.from(196);
	public static final ZeroPage $C5 = ZeroPage.from(197);
	public static final ZeroPage $C6 = ZeroPage.from(198);
	public static final ZeroPage $C7 = ZeroPage.from(199);
	public static final ZeroPage $C8 = ZeroPage.from(200);
	public static final ZeroPage $C9 = ZeroPage.from(201);
	public static final ZeroPage $CA = ZeroPage.from(202);
	public static final ZeroPage $CB = ZeroPage.from(203);
	public static final ZeroPage $CC = ZeroPage.from(204);
	public static final ZeroPage $CD = ZeroPage.from(205);
	public static final ZeroPage $CE = ZeroPage.from(206);
	public static final ZeroPage $CF = ZeroPage.from(207);
	public static final ZeroPage $D0 = ZeroPage.from(208);
	public static final ZeroPage $D1 = ZeroPage.from(209);
	public static final ZeroPage $D2 = ZeroPage.from(210);
	public static final ZeroPage $D3 = ZeroPage.from(211);
	public static final ZeroPage $D4 = ZeroPage.from(212);
	public static final ZeroPage $D5 = ZeroPage.from(213);
	public static final ZeroPage $D6 = ZeroPage.from(214);
	public static final ZeroPage $D7 = ZeroPage.from(215);
	public static final ZeroPage $D8 = ZeroPage.from(216);
	public static final ZeroPage $D9 = ZeroPage.from(217);
	public static final ZeroPage $DA = ZeroPage.from(218);
	public static final ZeroPage $DB = ZeroPage.from(219);
	public static final ZeroPage $DC = ZeroPage.from(220);
	public static final ZeroPage $DD = ZeroPage.from(221);
	public static final ZeroPage $DE = ZeroPage.from(222);
	public static final ZeroPage $DF = ZeroPage.from(223);
	public static final ZeroPage $E0 = ZeroPage.from(224);
	public static final ZeroPage $E1 = ZeroPage.from(225);
	public static final ZeroPage $E2 = ZeroPage.from(226);
	public static final ZeroPage $E3 = ZeroPage.from(227);
	public static final ZeroPage $E4 = ZeroPage.from(228);
	public static final ZeroPage $E5 = ZeroPage.from(229);
	public static final ZeroPage $E6 = ZeroPage.from(230);
	public static final ZeroPage $E7 = ZeroPage.from(231);
	public static final ZeroPage $E8 = ZeroPage.from(232);
	public static final ZeroPage $E9 = ZeroPage.from(233);
	public static final ZeroPage $EA = ZeroPage.from(234);
	public static final ZeroPage $EB = ZeroPage.from(235);
	public static final ZeroPage $EC = ZeroPage.from(236);
	public static final ZeroPage $ED = ZeroPage.from(237);
	public static final ZeroPage $EE = ZeroPage.from(238);
	public static final ZeroPage $EF = ZeroPage.from(239);
	public static final ZeroPage $F0 = ZeroPage.from(240);
	public static final ZeroPage $F1 = ZeroPage.from(241);
	public static final ZeroPage $F2 = ZeroPage.from(242);
	public static final ZeroPage $F3 = ZeroPage.from(243);
	public static final ZeroPage $F4 = ZeroPage.from(244);
	public static final ZeroPage $F5 = ZeroPage.from(245);
	public static final ZeroPage $F6 = ZeroPage.from(246);
	public static final ZeroPage $F7 = ZeroPage.from(247);
	public static final ZeroPage $F8 = ZeroPage.from(248);
	public static final ZeroPage $F9 = ZeroPage.from(249);
	public static final ZeroPage $FA = ZeroPage.from(250);
	public static final ZeroPage $FB = ZeroPage.from(251);
	public static final ZeroPage $FC = ZeroPage.from(252);
	public static final ZeroPage $FD = ZeroPage.from(253);
	public static final ZeroPage $FE = ZeroPage.from(254);
	public static final ZeroPage $FF = ZeroPage.from(255);

	public static ZeroPage from(int address) {
		return values[UnsignedByte.from(address).uInt()];
	}

	public static ZeroPage from(RawAddress address) {
		int value = address.value();
		if (value > 255) {
			throw new IllegalArgumentException(
					"Not a suitable address for zeropage: " + address);
		}
		return values[address.value()];
	}

	public static ZeroPage from(UnsignedByte address) {
		return values[address.uInt()];
	}

	private final UnsignedByte address;

	private ZeroPage(int address) {
		this(UnsignedByte.from(address));
	}

	private ZeroPage(UnsignedByte address) {
		this.address = address;
	}

	public UnsignedByte value() {
		return address;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("$");
		ByteArrayPrettyPrinter.append(b, address.signedByte());
		return b.toString();
	}

	public ZeroPagePlusX plusX() {
		return new ZeroPagePlusX(this);
	}

	public ZeroPagePlusXIndirect plusXIndirect() {
		return new ZeroPagePlusXIndirect(this);
	}

	public ZeroPage plus(int offset) {
		return from(value().plusSigned(offset));
	}

	public ZeroPageIndirectPlusY indirectPlusY() {
		return new ZeroPageIndirectPlusY(this);
	}

	@Override
	public void writeTo(ProgramOutputContext ctx) {
		ctx.write(address.signedByte());
	}

	@Override
	public String asAssy() {
		return "$" + ByteArrayPrettyPrinter
				.spaceSeparatedHex(address.signedByte());
	}

	@Override
	public String asJava() {
		return "ZeroPage." + asAssy();
	}

	@Override
	public boolean equals(Object obj) {
		// gotcha: *if* needed, behave well:
		ZeroPage o = (ZeroPage) obj;
		return address.equals(o.address);
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

}