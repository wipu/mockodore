package org.fluentjava.mockodore.model.sid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;

/**
 * byte values from http://codebase64.org/doku.php?id=base:pal_frequency_table
 */
public class Codebase64PalNote {

	private static final List<Codebase64PalNote> all = new ArrayList<>();
	private static final List<Codebase64PalNote> immutableAll;
	private static final Map<Integer, Codebase64PalNote> byValue = new HashMap<>();

	public static final Codebase64PalNote C__1 = instance(0, "C__1");
	public static final Codebase64PalNote Cis1 = instance(1, "Cis1");
	public static final Codebase64PalNote D__1 = instance(2, "D__1");
	public static final Codebase64PalNote Dis1 = instance(3, "Dis1");
	public static final Codebase64PalNote E__1 = instance(4, "E__1");
	public static final Codebase64PalNote F__1 = instance(5, "F__1");
	public static final Codebase64PalNote Fis1 = instance(6, "Fis1");
	public static final Codebase64PalNote G__1 = instance(7, "G__1");
	public static final Codebase64PalNote Gis1 = instance(8, "Gis1");
	public static final Codebase64PalNote A__1 = instance(9, "A__1");
	public static final Codebase64PalNote Ais1 = instance(10, "Ais1");
	public static final Codebase64PalNote B__1 = instance(11, "B__1");
	public static final Codebase64PalNote C__2 = instance(12, "C__2");
	public static final Codebase64PalNote Cis2 = instance(13, "Cis2");
	public static final Codebase64PalNote D__2 = instance(14, "D__2");
	public static final Codebase64PalNote Dis2 = instance(15, "Dis2");
	public static final Codebase64PalNote E__2 = instance(16, "E__2");
	public static final Codebase64PalNote F__2 = instance(17, "F__2");
	public static final Codebase64PalNote Fis2 = instance(18, "Fis2");
	public static final Codebase64PalNote G__2 = instance(19, "G__2");
	public static final Codebase64PalNote Gis2 = instance(20, "Gis2");
	public static final Codebase64PalNote A__2 = instance(21, "A__2");
	public static final Codebase64PalNote Ais2 = instance(22, "Ais2");
	public static final Codebase64PalNote B__2 = instance(23, "B__2");
	public static final Codebase64PalNote C__3 = instance(24, "C__3");
	public static final Codebase64PalNote Cis3 = instance(25, "Cis3");
	public static final Codebase64PalNote D__3 = instance(26, "D__3");
	public static final Codebase64PalNote Dis3 = instance(27, "Dis3");
	public static final Codebase64PalNote E__3 = instance(28, "E__3");
	public static final Codebase64PalNote F__3 = instance(29, "F__3");
	public static final Codebase64PalNote Fis3 = instance(30, "Fis3");
	public static final Codebase64PalNote G__3 = instance(31, "G__3");
	public static final Codebase64PalNote Gis3 = instance(32, "Gis3");
	public static final Codebase64PalNote A__3 = instance(33, "A__3");
	public static final Codebase64PalNote Ais3 = instance(34, "Ais3");
	public static final Codebase64PalNote B__3 = instance(35, "B__3");
	public static final Codebase64PalNote C__4 = instance(36, "C__4");
	public static final Codebase64PalNote Cis4 = instance(37, "Cis4");
	public static final Codebase64PalNote D__4 = instance(38, "D__4");
	public static final Codebase64PalNote Dis4 = instance(39, "Dis4");
	public static final Codebase64PalNote E__4 = instance(40, "E__4");
	public static final Codebase64PalNote F__4 = instance(41, "F__4");
	public static final Codebase64PalNote Fis4 = instance(42, "Fis4");
	public static final Codebase64PalNote G__4 = instance(43, "G__4");
	public static final Codebase64PalNote Gis4 = instance(44, "Gis4");
	public static final Codebase64PalNote A__4 = instance(45, "A__4");
	public static final Codebase64PalNote Ais4 = instance(46, "Ais4");
	public static final Codebase64PalNote B__4 = instance(47, "B__4");
	public static final Codebase64PalNote C__5 = instance(48, "C__5");
	public static final Codebase64PalNote Cis5 = instance(49, "Cis5");
	public static final Codebase64PalNote D__5 = instance(50, "D__5");
	public static final Codebase64PalNote Dis5 = instance(51, "Dis5");
	public static final Codebase64PalNote E__5 = instance(52, "E__5");
	public static final Codebase64PalNote F__5 = instance(53, "F__5");
	public static final Codebase64PalNote Fis5 = instance(54, "Fis5");
	public static final Codebase64PalNote G__5 = instance(55, "G__5");
	public static final Codebase64PalNote Gis5 = instance(56, "Gis5");
	public static final Codebase64PalNote A__5 = instance(57, "A__5");
	public static final Codebase64PalNote Ais5 = instance(58, "Ais5");
	public static final Codebase64PalNote B__5 = instance(59, "B__5");
	public static final Codebase64PalNote C__6 = instance(60, "C__6");
	public static final Codebase64PalNote Cis6 = instance(61, "Cis6");
	public static final Codebase64PalNote D__6 = instance(62, "D__6");
	public static final Codebase64PalNote Dis6 = instance(63, "Dis6");
	public static final Codebase64PalNote E__6 = instance(64, "E__6");
	public static final Codebase64PalNote F__6 = instance(65, "F__6");
	public static final Codebase64PalNote Fis6 = instance(66, "Fis6");
	public static final Codebase64PalNote G__6 = instance(67, "G__6");
	public static final Codebase64PalNote Gis6 = instance(68, "Gis6");
	public static final Codebase64PalNote A__6 = instance(69, "A__6");
	public static final Codebase64PalNote Ais6 = instance(70, "Ais6");
	public static final Codebase64PalNote B__6 = instance(71, "B__6");
	public static final Codebase64PalNote C__7 = instance(72, "C__7");
	public static final Codebase64PalNote Cis7 = instance(73, "Cis7");
	public static final Codebase64PalNote D__7 = instance(74, "D__7");
	public static final Codebase64PalNote Dis7 = instance(75, "Dis7");
	public static final Codebase64PalNote E__7 = instance(76, "E__7");
	public static final Codebase64PalNote F__7 = instance(77, "F__7");
	public static final Codebase64PalNote Fis7 = instance(78, "Fis7");
	public static final Codebase64PalNote G__7 = instance(79, "G__7");
	public static final Codebase64PalNote Gis7 = instance(80, "Gis7");
	public static final Codebase64PalNote A__7 = instance(81, "A__7");
	public static final Codebase64PalNote Ais7 = instance(82, "Ais7");
	public static final Codebase64PalNote B__7 = instance(83, "B__7");
	public static final Codebase64PalNote C__8 = instance(84, "C__8");
	public static final Codebase64PalNote Cis8 = instance(85, "Cis8");
	public static final Codebase64PalNote D__8 = instance(86, "D__8");
	public static final Codebase64PalNote Dis8 = instance(87, "Dis8");
	public static final Codebase64PalNote E__8 = instance(88, "E__8");
	public static final Codebase64PalNote F__8 = instance(89, "F__8");
	public static final Codebase64PalNote Fis8 = instance(90, "Fis8");
	public static final Codebase64PalNote G__8 = instance(91, "G__8");
	public static final Codebase64PalNote Gis8 = instance(92, "Gis8");
	public static final Codebase64PalNote A__8 = instance(93, "A__8");
	public static final Codebase64PalNote Ais8 = instance(94, "Ais8");
	public static final Codebase64PalNote B__8 = instance(95, "B__8");

	static {
		immutableAll = Collections.unmodifiableList(all);
	}

	private final UnsignedByte value;
	private final String name;

	private Codebase64PalNote(UnsignedByte value, String name) {
		this.value = value;
		this.name = name;
	}

	private static Codebase64PalNote instance(int value, String name) {
		Codebase64PalNote note = new Codebase64PalNote(UnsignedByte.from(value),
				name);
		all.add(note);
		byValue.put(value, note);
		return note;
	}

	public static Codebase64PalNote from(UnsignedByte value) {
		return byValue.get(value.uInt());
	}

	public UnsignedByte value() {
		return value;
	}

	public static List<Codebase64PalNote> all() {
		return immutableAll;
	}

	@Override
	public String toString() {
		return name;
	}

}
