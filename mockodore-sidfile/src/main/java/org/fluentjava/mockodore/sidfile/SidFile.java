package org.fluentjava.mockodore.sidfile;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.fluentjava.joulu.unsignedbyte.UnsignedByte;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.program.MockodoreProgram;

public class SidFile {

	private final byte[] sidBytes;

	public SidFile(byte[] sidBytes) {
		this.sidBytes = sidBytes;
	}

	public static SidFileSpex with() {
		return new SidFileSpex();
	}

	public static SidFileSpex like(SidFile a) {
		SidFileSpex b = with();
		b.author(a.author());
		b.copyright(a.copyright());
		b.flags(a.flagsMsb(), a.flagsLsb());
		b.init(a.initAddress());
		b.play(a.playAddress());

		PrgBytesWithLoadAddress prg = a.prgData();
		b.prg(MockodoreProgram.with().startAddress(prg.address())
				.data(prg.justPrgBytes()).end());

		b.songs(a.songsMsb(), a.songsLsb());
		b.startSong(a.startSongMsb(), a.startSongLsb());
		b.title(a.title());
		return b;
	}

	public static class SidFileSpex {

		private MockodoreProgram prg;
		private AbsRef init;
		private AbsRef play;
		private String title = "";
		private String author = "";
		private String copyright = "";
		private UnsignedByte flagsMsb = UnsignedByte.x00;
		private UnsignedByte flagsLsb = UnsignedByte.x14; // TODO why this?
		private UnsignedByte songsMsb = UnsignedByte.x00;
		private UnsignedByte songsLsb = UnsignedByte.x01;
		private UnsignedByte startSongMsb = UnsignedByte.x00;
		private UnsignedByte startSongLsb = UnsignedByte.x01;

		public SidFileSpex prg(MockodoreProgram prg) {
			this.prg = prg;
			return this;
		}

		public SidFileSpex init(AbsRef init) {
			this.init = init;
			return this;
		}

		public SidFileSpex play(AbsRef play) {
			this.play = play;
			return this;
		}

		public SidFileSpex title(String title) {
			this.title = title;
			return this;
		}

		public SidFileSpex author(String author) {
			this.author = author;
			return this;
		}

		public SidFileSpex copyright(String copyright) {
			this.copyright = copyright;
			return this;
		}

		public SidFileSpex flags(UnsignedByte flagsMsb, UnsignedByte flagsLsb) {
			this.flagsMsb = flagsMsb;
			this.flagsLsb = flagsLsb;
			return this;
		}

		public SidFileSpex flags(int flagsMsb, int flagsLsb) {
			return flags(UnsignedByte.from(flagsMsb),
					UnsignedByte.from(flagsLsb));
		}

		public SidFileSpex songs(UnsignedByte songsMsb, UnsignedByte songsLsb) {
			this.songsMsb = songsMsb;
			this.songsLsb = songsLsb;
			return this;
		}

		public SidFileSpex songs(int songsMsb, int songsLsb) {
			return songs(UnsignedByte.from(songsMsb),
					UnsignedByte.from(songsLsb));
		}

		public SidFileSpex startSong(UnsignedByte startSongMsb,
				UnsignedByte startSongLsb) {
			this.startSongMsb = startSongMsb;
			this.startSongLsb = startSongLsb;
			return this;
		}

		public SidFileSpex startSong(int startSongMsb, int startSongLsb) {
			return startSong(UnsignedByte.from(startSongMsb),
					UnsignedByte.from(startSongLsb));
		}

		public SidFile end() {
			List<Byte> bytes = new ArrayList<>();
			bytes.add((byte) 0x50);
			bytes.add((byte) 0x53);
			bytes.add((byte) 0x49);
			bytes.add((byte) 0x44);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x02);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x7C);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);

			// bytes.add((byte)0x10);
			// bytes.add((byte)0x00);
			bytes.add(prg.addressOf(init).msb());
			bytes.add(prg.addressOf(init).lsb());

			// bytes.add((byte)0x10);
			// bytes.add((byte)0x03);
			bytes.add(prg.addressOf(play).msb());
			bytes.add(prg.addressOf(play).lsb());

			bytes.add(songsMsb.signedByte());
			bytes.add(songsLsb.signedByte());

			bytes.add(startSongMsb.signedByte());
			bytes.add(startSongLsb.signedByte());

			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);

			bytes.addAll(to32Bytes(title));
			bytes.addAll(to32Bytes(author));
			bytes.addAll(to32Bytes(copyright));

			bytes.add(flagsMsb.signedByte());
			bytes.add(flagsLsb.signedByte());
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);
			bytes.add((byte) 0x00);
			for (byte prgB : prg.asPrgBytes().allBytes()) {
				bytes.add(prgB);
			}

			byte[] arr = new byte[bytes.size()];
			for (int i = 0; i < bytes.size(); i++) {
				arr[i] = bytes.get(i);
			}

			return new SidFile(arr);
		}

		private static List<Byte> to32Bytes(String string) {
			ArrayList<Byte> bytes = new ArrayList<>();
			for (byte b : string.getBytes(Charset.forName("ISO-8859-1"))) {
				bytes.add(b);
			}
			for (int i = bytes.size(); i < 32; i++) {
				bytes.add((byte) 0);
			}
			return bytes;
		}

	}

	public byte[] asBytes() {
		return sidBytes;
	}

	private static byte[] subarray(byte[] from, int firstIndex, int length) {
		byte[] to = new byte[length];
		System.arraycopy(from, firstIndex, to, 0, length);
		return to;
	}

	public byte[] header() {
		return subarray(sidBytes, 0, 4);
	}

	public byte[] version() {
		return subarray(sidBytes, 4, 2);
	}

	public byte[] dataOffset() {
		return subarray(sidBytes, 6, 2);
	}

	public RawAddress loadAddress() {
		return RawAddress.fromMsbAndLsb(subarray(sidBytes, 8, 2));
	}

	public RawAddress initAddress() {
		return RawAddress.fromMsbAndLsb(subarray(sidBytes, 0x0A, 2));
	}

	public RawAddress playAddress() {
		return RawAddress.fromMsbAndLsb(subarray(sidBytes, 0x0C, 2));
	}

	public RawAddress realLoadAddress() {
		// TODO read the offset
		return RawAddress.fromLsbAndMsb(subarray(sidBytes, 0x7C, 2));
	}

	public PrgBytesWithLoadAddress prgData() {
		// TODO read the offset
		int dataIndex = 0xFF & 0x7C;
		return new PrgBytesWithLoadAddress(
				subarray(sidBytes, dataIndex, sidBytes.length - dataIndex));
	}

	public String title() {
		return stringAt(0x16);
	}

	public String author() {
		return stringAt(0x36);
	}

	public String copyright() {
		return stringAt(0x56);
	}

	public UnsignedByte flagsMsb() {
		return UnsignedByte.from(sidBytes[0x76]);
	}

	public UnsignedByte flagsLsb() {
		return UnsignedByte.from(sidBytes[0x77]);
	}

	public UnsignedByte songsMsb() {
		return UnsignedByte.from(sidBytes[0x0E]);
	}

	public UnsignedByte songsLsb() {
		return UnsignedByte.from(sidBytes[0x0F]);
	}

	public UnsignedByte startSongMsb() {
		return UnsignedByte.from(sidBytes[0x10]);
	}

	public UnsignedByte startSongLsb() {
		return UnsignedByte.from(sidBytes[0x11]);
	}

	private String stringAt(int offset) {
		byte[] bytes = subarray(sidBytes, offset, 32);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			if (b == 0) {
				break;
			}
			result.write(b);
		}
		try {
			return result.toString("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
