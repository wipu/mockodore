package org.fluentjava.mockodore.program;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fluentjava.mockodore.api.assylang.C64AssyLangOf;
import org.fluentjava.mockodore.api.assylang.Debug;
import org.fluentjava.mockodore.api.assylang.InstructionsLang;
import org.fluentjava.mockodore.model.addressing.AbsRef;
import org.fluentjava.mockodore.model.addressing.ByteExtent;
import org.fluentjava.mockodore.model.addressing.MockodoreException;
import org.fluentjava.mockodore.model.addressing.Operand;
import org.fluentjava.mockodore.model.addressing.ProgramOutputContext;
import org.fluentjava.mockodore.model.addressing.RawAddress;
import org.fluentjava.mockodore.model.addressing.RelativeAddress;
import org.fluentjava.mockodore.model.addressing.ResolvedRelativeAddress;
import org.fluentjava.mockodore.model.labels.AddressRelativeToLabel;
import org.fluentjava.mockodore.model.labels.Label;
import org.fluentjava.mockodore.model.labels.Labeled;
import org.fluentjava.mockodore.model.machine.Op;
import org.fluentjava.mockodore.model.machine.PrgBytesWithLoadAddress;

public class MockodoreProgram {

	private final RawAddress startAddress;
	private final List<ByteExtent> instructions;
	private final Map<Label, Integer> labelToRelativeLocation;
	private final Map<ByteExtent, List<Label>> instructionToLabels;
	private final Map<Integer, List<Label>> relativeLocationToLabels;
	private final Map<Integer, List<Debug>> relativeLocationToDebugs;
	private final Map<ByteExtent, List<String>> commentsAfterInstr;

	public MockodoreProgram(RawAddress startAddress,
			List<ByteExtent> instructions,
			Map<Label, Integer> labelToRelativeLocation,
			Map<ByteExtent, List<Label>> instructionToLabels,
			Map<Integer, List<Label>> relativeLocationToLabels,
			Map<Integer, List<Debug>> relativeLocationToDebugs,
			Map<ByteExtent, List<String>> commentsAfterInstr) {
		this.startAddress = startAddress;
		this.instructions = instructions;
		this.labelToRelativeLocation = labelToRelativeLocation;
		this.instructionToLabels = instructionToLabels;
		this.relativeLocationToLabels = relativeLocationToLabels;
		this.relativeLocationToDebugs = relativeLocationToDebugs;
		this.commentsAfterInstr = commentsAfterInstr;
	}

	public static InstructionsPlease withInstructions() {
		return new InstructionsPlease();
	}

	private static class InstructionsPlease
			implements InstructionsLang<MockodoreProgram> {

		private final List<ByteExtent> instructions = new ArrayList<>();
		private RawAddress startAddress;
		private int programLength;
		private final Map<Label, Integer> labelToRelativeLocation = new LinkedHashMap<>();
		private final Map<Integer, List<Label>> relativeLocationToLabels = new LinkedHashMap<>();
		private final Map<ByteExtent, List<Label>> instructionToLabels = new LinkedHashMap<>();
		private final Map<Integer, List<Debug>> relativeLocationToDebugs = new LinkedHashMap<>();
		private final Map<ByteExtent, List<String>> commentsAfterInstr = new HashMap<>();

		@Override
		public MockodoreProgram end() {
			return new MockodoreProgram(startAddress, instructions,
					labelToRelativeLocation, instructionToLabels,
					relativeLocationToLabels, relativeLocationToDebugs,
					commentsAfterInstr);
		}

		@Override
		public InstructionsPlease startAddress(RawAddress startAddress) {
			this.startAddress = startAddress;
			return this;
		}

		@Override
		public InstructionsPlease bytes(ByteExtent i) {
			instructions.add(i);
			List<Label> maybeLabels = relativeLocationToLabels
					.get(programLength);
			if (maybeLabels != null) {
				instructionToLabels.put(i, maybeLabels);
			}
			programLength += i.length();
			return this;
		}

		@Override
		public InstructionsPlease instr(Op op, Operand operand) {
			return bytes(new Instruction(op, operand));
		}

		@Override
		public InstructionsPlease instr(Op op, RelativeAddress operand) {
			return instr(op, ResolvedRelativeAddress.fromTo(programLength,
					operand.value()));
		}

		@Override
		public InstructionsPlease label(Label label) {
			Integer prevValue = labelToRelativeLocation.get(label);
			if (prevValue != null) {
				throw new MockodoreException("Label '" + label
						+ "' was already defined at relative byte "
						+ prevValue);
			}
			labelToRelativeLocation.put(label, programLength);
			List<Label> labels = relativeLocationToLabels.get(programLength);
			if (labels == null) {
				labels = new ArrayList<>();
				relativeLocationToLabels.put(programLength, labels);
			}
			labels.add(label);
			return this;
		}

		@Override
		public InstructionsPlease label(Labeled labeled) {
			return label(labeled.label());
		}

		@Override
		public InstructionsPlease commentAfterInstr(String comment) {
			if (instructions.isEmpty()) {
				throw new MockodoreException(
						"No instruction to put comment after: " + comment);
			}
			ByteExtent latestInstr = instructions.get(instructions.size() - 1);
			List<String> comments = commentsAfterInstr.get(latestInstr);
			if (comments == null) {
				comments = new ArrayList<>();
				commentsAfterInstr.put(latestInstr, comments);
			}
			comments.add(comment);
			return this;
		}

		@Override
		public InstructionsPlease debug(Debug debug) {
			List<Debug> debugs = relativeLocationToDebugs.get(programLength);
			if (debugs == null) {
				debugs = new ArrayList<>();
				relativeLocationToDebugs.put(programLength, debugs);
			}
			debugs.add(debug);
			return this;
		}

	}

	public static C64AssyLangForProgram with() {
		return new AssyToProgramProxy();
	}

	public interface C64AssyLangForProgram
			extends C64AssyLangOf<C64AssyLangForProgram, MockodoreProgram> {

		// nothing to add

	}

	private static class AssyToProgramProxy
			extends C64AssyLangProxy<C64AssyLangForProgram, MockodoreProgram>
			implements C64AssyLangForProgram {

		AssyToProgramProxy() {
			super(new AssyToProgramInstructions());
		}

	}

	private static class AssyToProgramInstructions extends
			C64AssyToInstructions<C64AssyLangForProgram, MockodoreProgram> {

		AssyToProgramInstructions() {
			super(withInstructions());
		}

	}

	public JustPrgBytesWithoutLoadAddress asBytes() {
		Ctx ctx = new Ctx();
		return new JustPrgBytesWithoutLoadAddress(asBytes(ctx));
	}

	private byte[] asBytes(Ctx ctx) {
		for (ByteExtent instruction : instructions) {
			instruction.writeTo(ctx);
		}
		return ctx.bytesOut.toByteArray();
	}

	public PrgBytesWithLoadAddress asPrgBytes() {
		if (startAddress == null) {
			throw new MockodoreException(
					"Cannot create a prg without start address");
		}
		Ctx ctx = new Ctx();
		ctx.write(startAddress.lsb());
		ctx.write(startAddress.msb());
		return new PrgBytesWithLoadAddress(asBytes(ctx));
	}

	private class Ctx implements ProgramOutputContext {

		private final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

		@Override
		public RawAddress resolve(AbsRef address) {
			return addressOf(address);
		}

		@Override
		public RawAddress resolve(int programRelativeAddress) {
			return RawAddress
					.named(startAddress.value() + programRelativeAddress);
		}

		@Override
		public void write(int... bytes) {
			for (int b : bytes) {
				bytesOut.write(b);
			}
		}

	}

	public RawAddress addressOf(AbsRef address) {
		if (address instanceof RawAddress) {
			return (RawAddress) address;
		}
		if (startAddress == null) {
			throw new MockodoreException("Cannot reference label '" + address
					+ "' without program start address");
		}
		if (address instanceof AddressRelativeToLabel) {
			AddressRelativeToLabel rel = (AddressRelativeToLabel) address;
			RawAddress ref = addressOf(rel.reference());
			return RawAddress.named(ref.value() + rel.offset());
		}
		Integer relative = labelToRelativeLocation.get(address);
		if (relative == null) {
			throw new MockodoreException("Undefined label: " + address);
		}
		return RawAddress.named(startAddress.value() + relative);
	}

	public RawAddress startAddress() {
		return startAddress;
	}

	public String asAssy() {
		return asAssy(0);
	}

	public String asAssy(int indentationDepth) {
		return asAssy(indentationDepth, false);
	}

	public String asAssy(int indentationDepth, boolean showLabelAddresses) {
		Set<Label> printedLabels = new HashSet<>();
		String indentation = indentation(indentationDepth);
		StringBuilder b = new StringBuilder();
		for (ByteExtent instr : instructions) {
			List<Label> maybeLabels = instructionToLabels.get(instr);
			if (maybeLabels != null) {
				for (Label label : maybeLabels) {
					if (printedLabels.contains(label)) {
						continue;
					}
					printedLabels.add(label);
					b.append(label);
					b.append(":");
					if (showLabelAddresses) {
						b.append("  ;");
						b.append(addressOf(label));
					}
					b.append("\n");
				}
			}
			StringBuilder instrString = new StringBuilder();
			instr.toAssembler(instrString, indentation);
			b.append(instrString);

			List<String> comments = commentsAfterInstr.get(instr);
			if (comments != null) {
				for (Iterator<String> iterator = comments.iterator(); iterator
						.hasNext();) {
					String comment = iterator.next();
					b.append(" ; ");
					b.append(comment);
					if (iterator.hasNext()) {
						b.append("\n");
						for (int i = 0; i < instrString.length(); i++) {
							b.append(" ");
						}
					}
				}
			}

			b.append("\n");
		}
		return b.toString();
	}

	private static String indentation(int indentationDepth) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < indentationDepth; i++) {
			b.append(" ");
		}
		return b.toString();
	}

	public String asJava() {
		StringBuilder b = new StringBuilder();

		for (List<Label> labels : instructionToLabels.values()) {
			for (Label label : labels) {
				b.append("Label ");
				b.append(label.asJava());
				b.append(" = Label.named(\"");
				b.append(label.value());
				b.append("\");\n");
			}
		}

		for (ByteExtent instr : instructions) {
			List<Label> maybeLabels = instructionToLabels.get(instr);
			if (maybeLabels != null) {
				for (Label label : maybeLabels) {
					b.append("label(");
					b.append(label.asJava());
					b.append(");\n");
				}
			}
			instr.toJava(b);

			List<String> comments = commentsAfterInstr.get(instr);
			if (comments != null) {
				for (String comment : comments) {
					b.append(".commentAfterInstr(\"" + comment + "\")");
				}
			}

			b.append(";\n");
		}
		return b.toString();
	}

	public Map<Integer, List<Label>> labelMap() {
		Map<Integer, List<Label>> labelMap = new LinkedHashMap<>();
		for (Entry<Integer, List<Label>> e : relativeLocationToLabels
				.entrySet()) {
			labelMap.put(startAddress.value() + e.getKey(),
					Collections.unmodifiableList(e.getValue()));
		}
		return labelMap;
	}

	public Map<Integer, List<Debug>> debugMap() {
		Map<Integer, List<Debug>> debugMap = new LinkedHashMap<>();
		for (Entry<Integer, List<Debug>> e : relativeLocationToDebugs
				.entrySet()) {
			debugMap.put(startAddress.value() + e.getKey(),
					Collections.unmodifiableList(e.getValue()));
		}
		return debugMap;
	}

}
