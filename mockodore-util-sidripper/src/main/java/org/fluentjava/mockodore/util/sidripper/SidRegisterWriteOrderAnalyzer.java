package org.fluentjava.mockodore.util.sidripper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fluentjava.mockodore.model.sid.SidRegisterAddress;

public class SidRegisterWriteOrderAnalyzer implements SidWriteListener {

	private final int numberOfInitialFramesToIgnore;
	private final List<Frame> playFrames = new ArrayList<>();
	private int playCallsReceived = 0;
	private final Map<String, HappensBefore> happensBeforeByToString = new HashMap<>();
	private final SortedSet<HappensBefore> allPlayFrameHappensBefores = new TreeSet<>();
	private Frame currentFrame;

	public SidRegisterWriteOrderAnalyzer(int numberOfInitialFramesToIgnore) {
		this.numberOfInitialFramesToIgnore = numberOfInitialFramesToIgnore;
	}

	@Override
	public void playCallStarting() {
		playCallsReceived++;
		if (playCallsReceived > numberOfInitialFramesToIgnore) {
			startFrame();
		}
	}

	@Override
	public SidRegWriteListener reg(SidRegisterAddress reg) {
		if (currentFrame == null) {
			// init phase, waiting for play
			return (v) -> {
				/* ignoring */};
		}
		return (v) -> currentFrame.write(reg);
	}

	private void startFrame() {
		currentFrame = new Frame(playCallsReceived - 1);
		playFrames.add(currentFrame);
	}

	public List<Frame> playFrames() {
		return Collections.unmodifiableList(playFrames);
	}

	public SortedSet<HappensBefore> allPlayFrameHappensBefores() {
		return Collections.unmodifiableSortedSet(allPlayFrameHappensBefores);
	}

	public boolean hasContradictingWriteOrderFrames() {
		SortedSet<HappensBefore> prevs = new TreeSet<>();
		for (HappensBefore curr : allPlayFrameHappensBefores) {
			for (HappensBefore prev : prevs) {
				if (curr.contradicts(prev)) {
					return true;
				}
			}
			prevs.add(curr);
		}
		return false;
	}

	public class Frame {

		private final List<SidRegisterAddress> written = new ArrayList<>();
		private final Set<HappensBefore> happensBefores = new HashSet<>();
		private final int number;

		private Frame(int number) {
			this.number = number;
		}

		public void write(SidRegisterAddress reg) {
			for (SidRegisterAddress before : written) {
				HappensBefore happensBefore = happensBefore(before, reg);
				happensBefores.add(happensBefore);
				allPlayFrameHappensBefores.add(happensBefore);
			}
			written.add(reg);
		}

		@Override
		public String toString() {
			return number + ":" + written.toString();
		}

	}

	public static class HappensBefore implements Comparable<HappensBefore> {

		private final SidRegisterAddress before;
		private final SidRegisterAddress after;

		HappensBefore(SidRegisterAddress before, SidRegisterAddress after) {
			if (before.offsetInSid() == after.offsetInSid()) {
				throw new IllegalArgumentException(
						"Register happens before itself: " + before);
			}
			this.before = before;
			this.after = after;
		}

		public boolean contradicts(HappensBefore other) {
			if (before == other.after && after == other.before) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return happensBeforeString(before, after);
		}

		@Override
		public int compareTo(HappensBefore o) {
			int diff = before.offsetInSid() - o.before.offsetInSid();
			if (diff != 0) {
				return diff;
			}
			diff = after.offsetInSid() - o.after.offsetInSid();
			if (diff != 0) {
				return diff;
			}
			return 0;
		}

	}

	private static String happensBeforeString(SidRegisterAddress before,
			SidRegisterAddress after) {
		return before + " < " + after;
	}

	private HappensBefore happensBefore(SidRegisterAddress before,
			SidRegisterAddress after) {
		String key = happensBeforeString(before, after);
		HappensBefore o = happensBeforeByToString.get(key);
		if (o == null) {
			o = new HappensBefore(before, after);
			happensBeforeByToString.put(key, o);
		}
		return o;
	}

	public List<SidRegisterAddress> commonOrderOfRegisters() {
		if (hasContradictingWriteOrderFrames()) {
			throw new IllegalStateException(
					"No common order of register writes");
		}
		Set<SidRegisterAddress> regs = new LinkedHashSet<>();
		for (HappensBefore hb : allPlayFrameHappensBefores) {
			regs.add(hb.before);
		}
		// we are not just adding above loop afters, here we make sure all
		// "leftover" afters are added, too:
		for (HappensBefore hb : allPlayFrameHappensBefores) {
			regs.add(hb.after);
		}
		return new ArrayList<>(regs);
	}

}
