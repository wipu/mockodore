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

	public static final ZeroPage x00 = ZeroPage.from(0);
	public static final ZeroPage x01 = ZeroPage.from(1);
	public static final ZeroPage x02 = ZeroPage.from(2);
	public static final ZeroPage x03 = ZeroPage.from(3);
	public static final ZeroPage x04 = ZeroPage.from(4);
	public static final ZeroPage x05 = ZeroPage.from(5);
	public static final ZeroPage x06 = ZeroPage.from(6);
	public static final ZeroPage x07 = ZeroPage.from(7);
	public static final ZeroPage x08 = ZeroPage.from(8);
	public static final ZeroPage x09 = ZeroPage.from(9);
	public static final ZeroPage x0A = ZeroPage.from(10);
	public static final ZeroPage x0B = ZeroPage.from(11);
	public static final ZeroPage x0C = ZeroPage.from(12);
	public static final ZeroPage x0D = ZeroPage.from(13);
	public static final ZeroPage x0E = ZeroPage.from(14);
	public static final ZeroPage x0F = ZeroPage.from(15);
	public static final ZeroPage x10 = ZeroPage.from(16);
	public static final ZeroPage x11 = ZeroPage.from(17);
	public static final ZeroPage x12 = ZeroPage.from(18);
	public static final ZeroPage x13 = ZeroPage.from(19);
	public static final ZeroPage x14 = ZeroPage.from(20);
	public static final ZeroPage x15 = ZeroPage.from(21);
	public static final ZeroPage x16 = ZeroPage.from(22);
	public static final ZeroPage x17 = ZeroPage.from(23);
	public static final ZeroPage x18 = ZeroPage.from(24);
	public static final ZeroPage x19 = ZeroPage.from(25);
	public static final ZeroPage x1A = ZeroPage.from(26);
	public static final ZeroPage x1B = ZeroPage.from(27);
	public static final ZeroPage x1C = ZeroPage.from(28);
	public static final ZeroPage x1D = ZeroPage.from(29);
	public static final ZeroPage x1E = ZeroPage.from(30);
	public static final ZeroPage x1F = ZeroPage.from(31);
	public static final ZeroPage x20 = ZeroPage.from(32);
	public static final ZeroPage x21 = ZeroPage.from(33);
	public static final ZeroPage x22 = ZeroPage.from(34);
	public static final ZeroPage x23 = ZeroPage.from(35);
	public static final ZeroPage x24 = ZeroPage.from(36);
	public static final ZeroPage x25 = ZeroPage.from(37);
	public static final ZeroPage x26 = ZeroPage.from(38);
	public static final ZeroPage x27 = ZeroPage.from(39);
	public static final ZeroPage x28 = ZeroPage.from(40);
	public static final ZeroPage x29 = ZeroPage.from(41);
	public static final ZeroPage x2A = ZeroPage.from(42);
	public static final ZeroPage x2B = ZeroPage.from(43);
	public static final ZeroPage x2C = ZeroPage.from(44);
	public static final ZeroPage x2D = ZeroPage.from(45);
	public static final ZeroPage x2E = ZeroPage.from(46);
	public static final ZeroPage x2F = ZeroPage.from(47);
	public static final ZeroPage x30 = ZeroPage.from(48);
	public static final ZeroPage x31 = ZeroPage.from(49);
	public static final ZeroPage x32 = ZeroPage.from(50);
	public static final ZeroPage x33 = ZeroPage.from(51);
	public static final ZeroPage x34 = ZeroPage.from(52);
	public static final ZeroPage x35 = ZeroPage.from(53);
	public static final ZeroPage x36 = ZeroPage.from(54);
	public static final ZeroPage x37 = ZeroPage.from(55);
	public static final ZeroPage x38 = ZeroPage.from(56);
	public static final ZeroPage x39 = ZeroPage.from(57);
	public static final ZeroPage x3A = ZeroPage.from(58);
	public static final ZeroPage x3B = ZeroPage.from(59);
	public static final ZeroPage x3C = ZeroPage.from(60);
	public static final ZeroPage x3D = ZeroPage.from(61);
	public static final ZeroPage x3E = ZeroPage.from(62);
	public static final ZeroPage x3F = ZeroPage.from(63);
	public static final ZeroPage x40 = ZeroPage.from(64);
	public static final ZeroPage x41 = ZeroPage.from(65);
	public static final ZeroPage x42 = ZeroPage.from(66);
	public static final ZeroPage x43 = ZeroPage.from(67);
	public static final ZeroPage x44 = ZeroPage.from(68);
	public static final ZeroPage x45 = ZeroPage.from(69);
	public static final ZeroPage x46 = ZeroPage.from(70);
	public static final ZeroPage x47 = ZeroPage.from(71);
	public static final ZeroPage x48 = ZeroPage.from(72);
	public static final ZeroPage x49 = ZeroPage.from(73);
	public static final ZeroPage x4A = ZeroPage.from(74);
	public static final ZeroPage x4B = ZeroPage.from(75);
	public static final ZeroPage x4C = ZeroPage.from(76);
	public static final ZeroPage x4D = ZeroPage.from(77);
	public static final ZeroPage x4E = ZeroPage.from(78);
	public static final ZeroPage x4F = ZeroPage.from(79);
	public static final ZeroPage x50 = ZeroPage.from(80);
	public static final ZeroPage x51 = ZeroPage.from(81);
	public static final ZeroPage x52 = ZeroPage.from(82);
	public static final ZeroPage x53 = ZeroPage.from(83);
	public static final ZeroPage x54 = ZeroPage.from(84);
	public static final ZeroPage x55 = ZeroPage.from(85);
	public static final ZeroPage x56 = ZeroPage.from(86);
	public static final ZeroPage x57 = ZeroPage.from(87);
	public static final ZeroPage x58 = ZeroPage.from(88);
	public static final ZeroPage x59 = ZeroPage.from(89);
	public static final ZeroPage x5A = ZeroPage.from(90);
	public static final ZeroPage x5B = ZeroPage.from(91);
	public static final ZeroPage x5C = ZeroPage.from(92);
	public static final ZeroPage x5D = ZeroPage.from(93);
	public static final ZeroPage x5E = ZeroPage.from(94);
	public static final ZeroPage x5F = ZeroPage.from(95);
	public static final ZeroPage x60 = ZeroPage.from(96);
	public static final ZeroPage x61 = ZeroPage.from(97);
	public static final ZeroPage x62 = ZeroPage.from(98);
	public static final ZeroPage x63 = ZeroPage.from(99);
	public static final ZeroPage x64 = ZeroPage.from(100);
	public static final ZeroPage x65 = ZeroPage.from(101);
	public static final ZeroPage x66 = ZeroPage.from(102);
	public static final ZeroPage x67 = ZeroPage.from(103);
	public static final ZeroPage x68 = ZeroPage.from(104);
	public static final ZeroPage x69 = ZeroPage.from(105);
	public static final ZeroPage x6A = ZeroPage.from(106);
	public static final ZeroPage x6B = ZeroPage.from(107);
	public static final ZeroPage x6C = ZeroPage.from(108);
	public static final ZeroPage x6D = ZeroPage.from(109);
	public static final ZeroPage x6E = ZeroPage.from(110);
	public static final ZeroPage x6F = ZeroPage.from(111);
	public static final ZeroPage x70 = ZeroPage.from(112);
	public static final ZeroPage x71 = ZeroPage.from(113);
	public static final ZeroPage x72 = ZeroPage.from(114);
	public static final ZeroPage x73 = ZeroPage.from(115);
	public static final ZeroPage x74 = ZeroPage.from(116);
	public static final ZeroPage x75 = ZeroPage.from(117);
	public static final ZeroPage x76 = ZeroPage.from(118);
	public static final ZeroPage x77 = ZeroPage.from(119);
	public static final ZeroPage x78 = ZeroPage.from(120);
	public static final ZeroPage x79 = ZeroPage.from(121);
	public static final ZeroPage x7A = ZeroPage.from(122);
	public static final ZeroPage x7B = ZeroPage.from(123);
	public static final ZeroPage x7C = ZeroPage.from(124);
	public static final ZeroPage x7D = ZeroPage.from(125);
	public static final ZeroPage x7E = ZeroPage.from(126);
	public static final ZeroPage x7F = ZeroPage.from(127);
	public static final ZeroPage x80 = ZeroPage.from(128);
	public static final ZeroPage x81 = ZeroPage.from(129);
	public static final ZeroPage x82 = ZeroPage.from(130);
	public static final ZeroPage x83 = ZeroPage.from(131);
	public static final ZeroPage x84 = ZeroPage.from(132);
	public static final ZeroPage x85 = ZeroPage.from(133);
	public static final ZeroPage x86 = ZeroPage.from(134);
	public static final ZeroPage x87 = ZeroPage.from(135);
	public static final ZeroPage x88 = ZeroPage.from(136);
	public static final ZeroPage x89 = ZeroPage.from(137);
	public static final ZeroPage x8A = ZeroPage.from(138);
	public static final ZeroPage x8B = ZeroPage.from(139);
	public static final ZeroPage x8C = ZeroPage.from(140);
	public static final ZeroPage x8D = ZeroPage.from(141);
	public static final ZeroPage x8E = ZeroPage.from(142);
	public static final ZeroPage x8F = ZeroPage.from(143);
	public static final ZeroPage x90 = ZeroPage.from(144);
	public static final ZeroPage x91 = ZeroPage.from(145);
	public static final ZeroPage x92 = ZeroPage.from(146);
	public static final ZeroPage x93 = ZeroPage.from(147);
	public static final ZeroPage x94 = ZeroPage.from(148);
	public static final ZeroPage x95 = ZeroPage.from(149);
	public static final ZeroPage x96 = ZeroPage.from(150);
	public static final ZeroPage x97 = ZeroPage.from(151);
	public static final ZeroPage x98 = ZeroPage.from(152);
	public static final ZeroPage x99 = ZeroPage.from(153);
	public static final ZeroPage x9A = ZeroPage.from(154);
	public static final ZeroPage x9B = ZeroPage.from(155);
	public static final ZeroPage x9C = ZeroPage.from(156);
	public static final ZeroPage x9D = ZeroPage.from(157);
	public static final ZeroPage x9E = ZeroPage.from(158);
	public static final ZeroPage x9F = ZeroPage.from(159);
	public static final ZeroPage xA0 = ZeroPage.from(160);
	public static final ZeroPage xA1 = ZeroPage.from(161);
	public static final ZeroPage xA2 = ZeroPage.from(162);
	public static final ZeroPage xA3 = ZeroPage.from(163);
	public static final ZeroPage xA4 = ZeroPage.from(164);
	public static final ZeroPage xA5 = ZeroPage.from(165);
	public static final ZeroPage xA6 = ZeroPage.from(166);
	public static final ZeroPage xA7 = ZeroPage.from(167);
	public static final ZeroPage xA8 = ZeroPage.from(168);
	public static final ZeroPage xA9 = ZeroPage.from(169);
	public static final ZeroPage xAA = ZeroPage.from(170);
	public static final ZeroPage xAB = ZeroPage.from(171);
	public static final ZeroPage xAC = ZeroPage.from(172);
	public static final ZeroPage xAD = ZeroPage.from(173);
	public static final ZeroPage xAE = ZeroPage.from(174);
	public static final ZeroPage xAF = ZeroPage.from(175);
	public static final ZeroPage xB0 = ZeroPage.from(176);
	public static final ZeroPage xB1 = ZeroPage.from(177);
	public static final ZeroPage xB2 = ZeroPage.from(178);
	public static final ZeroPage xB3 = ZeroPage.from(179);
	public static final ZeroPage xB4 = ZeroPage.from(180);
	public static final ZeroPage xB5 = ZeroPage.from(181);
	public static final ZeroPage xB6 = ZeroPage.from(182);
	public static final ZeroPage xB7 = ZeroPage.from(183);
	public static final ZeroPage xB8 = ZeroPage.from(184);
	public static final ZeroPage xB9 = ZeroPage.from(185);
	public static final ZeroPage xBA = ZeroPage.from(186);
	public static final ZeroPage xBB = ZeroPage.from(187);
	public static final ZeroPage xBC = ZeroPage.from(188);
	public static final ZeroPage xBD = ZeroPage.from(189);
	public static final ZeroPage xBE = ZeroPage.from(190);
	public static final ZeroPage xBF = ZeroPage.from(191);
	public static final ZeroPage xC0 = ZeroPage.from(192);
	public static final ZeroPage xC1 = ZeroPage.from(193);
	public static final ZeroPage xC2 = ZeroPage.from(194);
	public static final ZeroPage xC3 = ZeroPage.from(195);
	public static final ZeroPage xC4 = ZeroPage.from(196);
	public static final ZeroPage xC5 = ZeroPage.from(197);
	public static final ZeroPage xC6 = ZeroPage.from(198);
	public static final ZeroPage xC7 = ZeroPage.from(199);
	public static final ZeroPage xC8 = ZeroPage.from(200);
	public static final ZeroPage xC9 = ZeroPage.from(201);
	public static final ZeroPage xCA = ZeroPage.from(202);
	public static final ZeroPage xCB = ZeroPage.from(203);
	public static final ZeroPage xCC = ZeroPage.from(204);
	public static final ZeroPage xCD = ZeroPage.from(205);
	public static final ZeroPage xCE = ZeroPage.from(206);
	public static final ZeroPage xCF = ZeroPage.from(207);
	public static final ZeroPage xD0 = ZeroPage.from(208);
	public static final ZeroPage xD1 = ZeroPage.from(209);
	public static final ZeroPage xD2 = ZeroPage.from(210);
	public static final ZeroPage xD3 = ZeroPage.from(211);
	public static final ZeroPage xD4 = ZeroPage.from(212);
	public static final ZeroPage xD5 = ZeroPage.from(213);
	public static final ZeroPage xD6 = ZeroPage.from(214);
	public static final ZeroPage xD7 = ZeroPage.from(215);
	public static final ZeroPage xD8 = ZeroPage.from(216);
	public static final ZeroPage xD9 = ZeroPage.from(217);
	public static final ZeroPage xDA = ZeroPage.from(218);
	public static final ZeroPage xDB = ZeroPage.from(219);
	public static final ZeroPage xDC = ZeroPage.from(220);
	public static final ZeroPage xDD = ZeroPage.from(221);
	public static final ZeroPage xDE = ZeroPage.from(222);
	public static final ZeroPage xDF = ZeroPage.from(223);
	public static final ZeroPage xE0 = ZeroPage.from(224);
	public static final ZeroPage xE1 = ZeroPage.from(225);
	public static final ZeroPage xE2 = ZeroPage.from(226);
	public static final ZeroPage xE3 = ZeroPage.from(227);
	public static final ZeroPage xE4 = ZeroPage.from(228);
	public static final ZeroPage xE5 = ZeroPage.from(229);
	public static final ZeroPage xE6 = ZeroPage.from(230);
	public static final ZeroPage xE7 = ZeroPage.from(231);
	public static final ZeroPage xE8 = ZeroPage.from(232);
	public static final ZeroPage xE9 = ZeroPage.from(233);
	public static final ZeroPage xEA = ZeroPage.from(234);
	public static final ZeroPage xEB = ZeroPage.from(235);
	public static final ZeroPage xEC = ZeroPage.from(236);
	public static final ZeroPage xED = ZeroPage.from(237);
	public static final ZeroPage xEE = ZeroPage.from(238);
	public static final ZeroPage xEF = ZeroPage.from(239);
	public static final ZeroPage xF0 = ZeroPage.from(240);
	public static final ZeroPage xF1 = ZeroPage.from(241);
	public static final ZeroPage xF2 = ZeroPage.from(242);
	public static final ZeroPage xF3 = ZeroPage.from(243);
	public static final ZeroPage xF4 = ZeroPage.from(244);
	public static final ZeroPage xF5 = ZeroPage.from(245);
	public static final ZeroPage xF6 = ZeroPage.from(246);
	public static final ZeroPage xF7 = ZeroPage.from(247);
	public static final ZeroPage xF8 = ZeroPage.from(248);
	public static final ZeroPage xF9 = ZeroPage.from(249);
	public static final ZeroPage xFA = ZeroPage.from(250);
	public static final ZeroPage xFB = ZeroPage.from(251);
	public static final ZeroPage xFC = ZeroPage.from(252);
	public static final ZeroPage xFD = ZeroPage.from(253);
	public static final ZeroPage xFE = ZeroPage.from(254);
	public static final ZeroPage xFF = ZeroPage.from(255);

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
		return "ZeroPage.x" + ByteArrayPrettyPrinter
				.spaceSeparatedHex(address.signedByte());
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