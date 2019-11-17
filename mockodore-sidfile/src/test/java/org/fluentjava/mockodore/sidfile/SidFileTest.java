package org.fluentjava.mockodore.sidfile;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.fluentjava.joulu.unsignedbyte.ByteArrayPrettyPrinter;
import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SidFileTest {

	private static byte[] sidBytes;

	private SidFile sidFile;

	@BeforeClass
	public static void beforeClass() throws IOException {
		sidBytes = IOUtils
				.toByteArray(SidFileTest.class.getResource("/ekakoe.sid"));
		StringBuilder b = new StringBuilder();
		int ix = 0;
		for (byte byt : sidBytes) {
			ix++;
			if (ix > 0x7C) {
				break;
			}
			b.append("bytes.add((byte)");
			b.append(UnsignedByte.from(byt).toString().replaceFirst("#\\$",
					"0x"));
			b.append(");\n");
		}
		System.err.println(b);
	}

	@Before
	public void before() {
		sidFile = new SidFile(sidBytes);
	}

	private static void assertBytes(byte[] expected, byte[] actual) {
		assertEquals(ByteArrayPrettyPrinter.spaceSeparatedHex(expected),
				ByteArrayPrettyPrinter.spaceSeparatedHex(actual));
	}

	@Test
	public void headerIsPsid() {
		assertEquals("PSID", new String(sidFile.header()));
	}

	@Test
	public void sidFileVersion() {
		assertEquals("00 02",
				ByteArrayPrettyPrinter.spaceSeparatedHex(sidFile.version()));
	}

	@Test
	public void dataOffset() {
		assertEquals("00 7C",
				ByteArrayPrettyPrinter.spaceSeparatedHex(sidFile.dataOffset()));
	}

	@Test
	public void loadAddressIsZeroSoDataIsPrefixedByLoadAddress() {
		assertEquals("$0", sidFile.loadAddress().toString());
	}

	@Test
	public void initAddress() {
		assertEquals("$1000", sidFile.initAddress().toString());
	}

	@Test
	public void playAddress() {
		assertEquals("$1003", sidFile.playAddress().toString());
	}

	@Test
	public void loadAddressFromData() {
		assertEquals("$1000", sidFile.realLoadAddress().toString());
	}

	@Test
	public void data() {
		PrgBytesWithLoadAddress data = sidFile.prgData();

		assertEquals("$1000", data.address().toString());
		assertEquals(1620, data.allBytes().length);
	}

	@Test
	public void title() {
		assertEquals("ekakoe", sidFile.title());
	}

	@Test
	public void author() {
		assertEquals("arr:wipu,comp:mya", sidFile.author());
	}

	@Test
	public void copyright() {
		assertEquals("", sidFile.copyright());
	}

	@Test
	public void flags() {
		assertEquals("#$00", sidFile.flagsMsb().toString());
		assertEquals("#$14", sidFile.flagsLsb().toString());
	}

	@Test
	public void songs() {
		assertEquals("#$00", sidFile.songsMsb().toString());
		assertEquals("#$01", sidFile.songsLsb().toString());
	}

	@Test
	public void fromProgramAndLabelsWithMostlyDefaults() {
		Label play = Label.named("play");
		Label init = Label.named("init");
		C64AssyLangForProgram p = MockodoreProgram.with();
		p.startAddress(RawAddress.named(0x1000));
		p.data(0xFF);
		p.label(init).lda(0x01).rts();
		p.label(play).lda(0x02).rts();

		sidFile = SidFile.with().prg(p.end()).init(init).play(play)
				.title("ekakoe").author("arr:wipu,comp:mya").end();

		assertEquals("$1001", sidFile.initAddress().toString());
		assertEquals("$1004", sidFile.playAddress().toString());
		assertEquals("$1000", sidFile.realLoadAddress().toString());
		PrgBytesWithLoadAddress data = sidFile.prgData();
		assertEquals("$1000", data.address().toString());
		assertEquals("ekakoe", sidFile.title());
		assertEquals("arr:wipu,comp:mya", sidFile.author());
		assertEquals("", sidFile.copyright());
		assertEquals("#$00", sidFile.flagsMsb().toString());
		assertEquals("#$14", sidFile.flagsLsb().toString());
		assertEquals("#$00", sidFile.songsMsb().toString());
		assertEquals("#$01", sidFile.songsLsb().toString());
		assertEquals("#$00", sidFile.startSongMsb().toString());
		assertEquals("#$01", sidFile.startSongLsb().toString());
		assertEquals(9, data.allBytes().length);

		assertEquals("50 53 49 44 00 02 00 7C 00 00 10 01 10 04 00 01 00 "
				+ "01 00 00 00 00 65 6B 61 6B 6F 65 00 00 00 00 00 00 00 "
				+ "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "
				+ "00 61 72 72 3A 77 69 70 75 2C 63 6F 6D 70 3A 6D 79 61 "
				+ "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "
				+ "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "
				+ "00 00 00 00 00 00 00 00 00 00 00 00 14 00 00 00 00 00 "
				+ "10 FF A9 01 60 A9 02 60",
				ByteArrayPrettyPrinter.spaceSeparatedHex(sidFile.asBytes()));
	}

	@Test
	public void fromDifferentProgramAndLabelsAndMetadataAndDefaultsOverridden() {
		Label play = Label.named("play");
		Label init = Label.named("init");
		C64AssyLangForProgram p = MockodoreProgram.with();
		p.startAddress(RawAddress.named(0x2000));
		p.data(0xFE);
		p.label(init).lda(0xE1).rts();
		p.label(play).lda(0xE2).rts();

		sidFile = SidFile.with().prg(p.end()).init(init).play(play)
				.title("Different title").author("Different author")
				.copyright("Different copyright").flags(0x13, 0x24)
				.songs(0x02, 0x03).startSong(0x01, 0x23).end();

		assertEquals("$2001", sidFile.initAddress().toString());
		assertEquals("$2004", sidFile.playAddress().toString());
		assertEquals("$2000", sidFile.realLoadAddress().toString());
		PrgBytesWithLoadAddress data = sidFile.prgData();
		assertEquals("$2000", data.address().toString());
		assertEquals("Different title", sidFile.title());
		assertEquals("Different author", sidFile.author());
		assertEquals("Different copyright", sidFile.copyright());
		assertEquals("#$13", sidFile.flagsMsb().toString());
		assertEquals("#$24", sidFile.flagsLsb().toString());
		assertEquals("#$02", sidFile.songsMsb().toString());
		assertEquals("#$03", sidFile.songsLsb().toString());
		assertEquals("#$01", sidFile.startSongMsb().toString());
		assertEquals("#$23", sidFile.startSongLsb().toString());
		assertEquals(9, data.allBytes().length);

		assertEquals(
				"50 53 49 44 00 02 00 7C 00 00 20 01 20 04 02 03 "
						+ "01 23 00 00 00 00 44 69 66 66 65 72 65 6E 74 20 "
						+ "74 69 74 6C 65 00 00 00 00 00 00 00 00 00 00 00 "
						+ "00 00 00 00 00 00 44 69 66 66 65 72 65 6E 74 20 "
						+ "61 75 74 68 6F 72 00 00 00 00 00 00 00 00 00 00 "
						+ "00 00 00 00 00 00 44 69 66 66 65 72 65 6E 74 20 "
						+ "63 6F 70 79 72 69 67 68 74 00 00 00 00 00 00 00 "
						+ "00 00 00 00 00 00 13 24 00 00 00 00 00 20 FE A9 "
						+ "E1 60 A9 E2 60",
				ByteArrayPrettyPrinter.spaceSeparatedHex(sidFile.asBytes()));
	}

	@Test
	public void fromProgramAndLabelsUsesEmptyAsDefaultString() {
		Label play = Label.named("play");
		Label init = Label.named("init");
		C64AssyLangForProgram p = MockodoreProgram.with();
		p.startAddress(RawAddress.named(0x1000));
		p.data(0xFF);
		p.label(init).lda(0x01).rts();
		p.label(play).lda(0x02).rts();

		sidFile = SidFile.with().prg(p.end()).init(init).play(play).end();

		assertEquals("", sidFile.title());
		assertEquals("", sidFile.author());
		assertEquals("", sidFile.copyright());
	}

	@Test
	public void fromProgramAndLabelsUsesSaneDefaultFlags() {
		Label play = Label.named("play");
		Label init = Label.named("init");
		C64AssyLangForProgram p = MockodoreProgram.with();
		p.startAddress(RawAddress.named(0x1000));
		p.data(0xFF);
		p.label(init).lda(0x01).rts();
		p.label(play).lda(0x02).rts();

		sidFile = SidFile.with().prg(p.end()).init(init).play(play).end();

		assertEquals("#$00", sidFile.flagsMsb().toString());
		assertEquals("#$14", sidFile.flagsLsb().toString());
	}

	private void parseAndUnparseAndCompare(String sidResource)
			throws IOException {
		byte[] bytes1 = IOUtils
				.toByteArray(getClass().getResource(sidResource));
		SidFile sid1 = new SidFile(bytes1);
		assertBytes(bytes1, sid1.asBytes());

		SidFile sid2 = SidFile.like(sid1).end();

		assertEquals(sid1.author(), sid2.author());
		assertEquals(sid1.copyright(), sid2.copyright());
		assertBytes(sid1.dataOffset(), sid2.dataOffset());
		assertEquals(sid1.flagsLsb(), sid2.flagsLsb());
		assertEquals(sid1.flagsMsb(), sid2.flagsMsb());
		assertBytes(sid1.header(), sid2.header());
		assertEquals(sid1.initAddress(), sid2.initAddress());
		assertEquals(sid1.loadAddress(), sid2.loadAddress());
		assertEquals(sid1.playAddress(), sid2.playAddress());
		assertEquals(sid1.prgData(), sid2.prgData());
		assertEquals(sid1.realLoadAddress(), sid2.realLoadAddress());
		assertEquals(sid1.title(), sid2.title());
		assertBytes(sid1.version(), sid2.version());

		assertBytes(bytes1, sid2.asBytes());
	}

	@Test
	public void parseAndUnparseAndCompareFromSomeSids() throws IOException {
		parseAndUnparseAndCompare(
				"/C64Music/MUSICIANS/H/Holt_Hein/Farting.sid");
		parseAndUnparseAndCompare(
				"/C64Music/MUSICIANS/H/Hubbard_Rob/Delta.sid");
		parseAndUnparseAndCompare("/C64Music/MUSICIANS/J/Jeff/Messy_One.sid");
	}

}
