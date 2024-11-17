package org.fluentjava.mockodore.lib.sidplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;
import org.fluentjava.mockodore.program.MockodoreProgram;
import org.fluentjava.mockodore.sidfile.SidFile;
import org.junit.jupiter.api.Test;

public class SidPlayerTest {

	@Test
	public void startOfJeffMessyOneAssy() throws IOException {
		byte[] sidBytes = IOUtils.toByteArray(getClass()
				.getResource("/C64Music/MUSICIANS/J/Jeff/Messy_One.sid"));
		SidFile sidFile = new SidFile(sidBytes);
		SidPlayer player = SidPlayer.forSidFile(sidFile);

		MockodoreProgram prg = player.asPrg();

		assertEquals(0x1000, prg.addressOf(SidPlayer.SID).value());
		assertEquals(".b $0C\n" + ".b $08\n" + ".b $0A\n" + ".b $00\n"
				+ ".b $9E\n" + ".b $20\n" + ".b $32\n" + ".b $30\n" + ".b $36\n"
				+ ".b $32\n" + ".b $00\n" + ".b $00\n" + ".b $00\n"
				+ "jsr $1000\n" + "sei\n" + "lda #$7F\n" + "sta $DC0D\n"
				+ "and $D011\n" + "sta $D011\n" + "lda #<vbi\n" + "sta $314\n"
				+ "lda #>vbi\n" + "sta $315\n" + "lda #$20\n" + "sta $D012\n"
				+ "sta $02\n" + "lda #$01\n" + "sta $D01A\n" + "cli\n" + "rts\n"
				+ "vbi:\n" + "inc $D020\n" + "jsr $1003\n" + "dec $D020\n"
				+ "asl $D019\n" + "jmp $EA31\n" + "SidPlayer_padding:\n"
				+ "; padding until load address:\n" + ".b $00\n" + ".b $00\n"
				+ ".b $00\n", prg.asAssy().substring(0, 361));
		assertEquals(
				".b $00\n" + ".b $00\n" + ".b $00\n" + ".b $00\n" + ".b $00\n"
						+ ".b $00\n" + ".b $00\n" + "SidPlayer_sid:\n"
						+ ".b $4C\n" + ".b $72\n" + ".b $11\n" + ".b $4C\n"
						+ ".b $B0\n" + ".b $11\n" + "",
				prg.asAssy().substring(14151, 14257));
	}

	@Test
	public void assyOfPlayerWithMockedSid() {
		PrgBytesWithLoadAddress sidBytes = new PrgBytesWithLoadAddress(
				new byte[] { 0x46, 0x08, 1, 2, 3, 4, });
		SidPlayer player = SidPlayer.forSid(sidBytes, 0x846, 2118, 2121);

		MockodoreProgram prg = player.asPrg();

		assertEquals(0x846, prg.addressOf(SidPlayer.SID).value());
		assertEquals(".b $0C\n" + ".b $08\n" + ".b $0A\n" + ".b $00\n"
				+ ".b $9E\n" + ".b $20\n" + ".b $32\n" + ".b $30\n" + ".b $36\n"
				+ ".b $32\n" + ".b $00\n" + ".b $00\n" + ".b $00\n"
				+ "jsr $846\n" + "sei\n" + "lda #$7F\n" + "sta $DC0D\n"
				+ "and $D011\n" + "sta $D011\n" + "lda #<vbi\n" + "sta $314\n"
				+ "lda #>vbi\n" + "sta $315\n" + "lda #$20\n" + "sta $D012\n"
				+ "sta $02\n" + "lda #$01\n" + "sta $D01A\n" + "cli\n" + "rts\n"
				+ "vbi:\n" + "inc $D020\n" + "jsr $849\n" + "dec $D020\n"
				+ "asl $D019\n" + "jmp $EA31\n" + "SidPlayer_padding:\n"
				+ "; padding until load address:\n" + ".b $00\n" + ".b $00\n"
				+ "SidPlayer_sid:\n" + ".b $01\n" + ".b $02\n" + ".b $03\n"
				+ ".b $04\n" + "", prg.asAssy());
	}

	@Test
	public void optimalSidLoadAddressMeansNoPaddingIsNeeded() {
		PrgBytesWithLoadAddress sidBytes = new PrgBytesWithLoadAddress(
				new byte[] { 0x46, 0x08, 1, 2, 3, 4, });
		SidPlayer player = SidPlayer.forSid(sidBytes,
				SidPlayer.OPTIMAL_SID_LOAD_ADDRESS, RawAddress.named(2118),
				RawAddress.named(2121));

		MockodoreProgram prg = player.asPrg();

		assertEquals(prg.addressOf(SidPlayer.PADDING),
				prg.addressOf(SidPlayer.SID));
		assertEquals(0x844, prg.addressOf(SidPlayer.SID).value());

	}

}
