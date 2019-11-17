package org.fluentjava.mockodore.lib.sidplayer;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.lib.basicloader.BasicLoader;
import org.fluentjava.mockodore.lib.misc.Vbi;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.program.C64AssyLangProxy;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.program.MockodoreProgram.C64AssyLangForProgram;
import org.fluentjava.mockodore.sidfile.SidFile;

public class SidPlayer {

	static final Label SID = Label.named("SidPlayer_sid");
	static final Label PADDING = Label.named("SidPlayer_padding");
	public static final RawAddress OPTIMAL_SID_LOAD_ADDRESS = RawAddress
			.named(0x844);
	private final PrgBytesWithLoadAddress sidBytes;
	private final RawAddress loadAddress;
	private final AbsRef initAddress;
	private final AbsRef playAddress;

	public SidPlayer(PrgBytesWithLoadAddress sidBytes, RawAddress loadAddress,
			AbsRef initAddress, AbsRef playAddress) {
		this.sidBytes = sidBytes;
		this.loadAddress = loadAddress;
		this.initAddress = initAddress;
		this.playAddress = playAddress;
	}

	public static SidPlayer forSidFile(SidFile sidFile) {
		PrgBytesWithLoadAddress prg = sidFile.prgData();
		RawAddress loadAddress = sidFile.realLoadAddress();
		RawAddress initAddress = sidFile.initAddress();
		RawAddress playAddress = sidFile.playAddress();
		return forSid(prg, loadAddress, initAddress, playAddress);
	}

	public static SidPlayer forSid(PrgBytesWithLoadAddress sidBytes,
			int loadAddress, int initAddress, int playAddress) {
		return forSid(sidBytes, RawAddress.named(loadAddress),
				RawAddress.named(initAddress), RawAddress.named(playAddress));
	}

	public static SidPlayer forSid(PrgBytesWithLoadAddress sidBytes,
			RawAddress loadAddress, AbsRef initAddress, AbsRef playAddress) {
		return new SidPlayer(sidBytes, loadAddress, initAddress, playAddress);
	}

	public MockodoreProgram asPrg() {
		C64AssyLangForProgram out = MockodoreProgram.with();
		def(out);
		return out.end();
	}

	public <LANG extends C64AssyLangOf<LANG, END>, END> LANG def(LANG p) {
		return new Code<>(p).def();
	}

	private class Code<LANG extends C64AssyLangOf<LANG, END>, END>
			extends C64AssyLangProxy<LANG, END> {

		public Code(LANG out) {
			super(out);
		}

		public LANG def() {
			Label VBI = Label.named("vbi");

			BasicLoader.def(__());
			jsr(initAddress);
			int vbiRasterLine = 0x20;
			Vbi.registerVbi(__(), vbiRasterLine, VBI);
			rts();

			label(VBI);
			vbiColorsOn();
			jsr(playAddress);
			vbiColorsOff();
			asl(raw(0xD019));
			jmp(raw(0xEA31));

			label(PADDING);
			commentLine("padding until load address:");
			for (int i = 2116; i < loadAddress.value(); i++) {
				data(0x00);
			}

			label(SID);
			byte[] sidPrg = sidBytes.justPrgBytes();
			for (int i = 0; i < sidPrg.length; i++) {
				data(sidPrg[i]);
			}
			return __();
		}

		private void vbiColorsOff() {
			dec(raw(0xd020));
		}

		private void vbiColorsOn() {
			inc(raw(0xd020));
		}

	}

	private static RawAddress raw(int value) {
		return RawAddress.named(value);
	}

}
