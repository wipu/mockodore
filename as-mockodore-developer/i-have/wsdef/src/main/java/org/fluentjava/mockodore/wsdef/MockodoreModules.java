package org.fluentjava.mockodore.wsdef;

import org.fluentjava.iwant.api.core.SubPath;
import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaClasses;
import org.fluentjava.iwant.api.javamodules.JavaClasses.JavaClassesSpex;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.zip.Jar;
import org.fluentjava.iwant.api.zip.Unzipped;
import org.fluentjava.iwant.core.download.Downloaded;
import org.fluentjava.iwant.core.javamodules.JavaModules;
import org.fluentjava.iwant.plugin.github.FromGithub;

public class MockodoreModules extends JavaModules {

	private static final JavaBinModule commonsCodec = binModule("commons-codec",
			"commons-codec", "1.9");
	private static final JavaBinModule commonsIo = binModule("commons-io",
			"commons-io", "2.4");
	private static final JavaBinModule guava = binModule("com.google.guava",
			"guava", "18.0");
	private static final JavaBinModule guavaTestlib = binModule(
			"com.google.guava", "guava-testlib", "18.0", guava);
	private static final JavaBinModule hamcrestCore = binModule("org/hamcrest",
			"hamcrest-core", "1.3");
	private static final JavaBinModule junit = binModule("junit", "junit",
			"4.11", hamcrestCore);

	private static final JavaBinModule hvsc = JavaBinModule
			.providing(hvscC64MusicZip()).end();

	private static Path hvscC64MusicZip() {
		String name = "C64Music.zip";
		return new SubPath(name, hvscZipUnzipped(), name);
	}

	private static Path hvscZipUnzipped() {
		Path zip = hvscZip();
		return Unzipped.with().name(zip.name() + ".unzipped").from(zip).end();
	}

	private static Path hvscZip() {
		String name = "HVSC_60-all-of-them.zip";
		return Downloaded.withName(name)
				.url("http://www.prg.dtu.dk/HVSC/" + name).noCheck();
	}

	private static Path jouluCode() {
		return FromGithub.user("wipu").project("joulu")
				.commit("9ea49d95e47f36f732d472fdd218155e595ca246");
	}

	private static JavaBinModule jouluModule(String name, JavaModule... deps) {
		Path java = new SubPath("joulu-" + name + "-java", jouluCode(),
				name + "/src/main/java");
		JavaClassesSpex classes = JavaClasses.with()
				.name("joulu-" + name + "-classes").srcDirs(java);
		for (JavaModule dep : deps) {
			classes = classes.classLocations(dep.mainArtifact());
		}
		Path jar = Jar.with().name("joulu-" + name + ".jar")
				.classes(classes.end()).end();

		return JavaBinModule.providing(jar, java).end();
	}

	private static JavaBinModule jouluUnsignedByte() {
		return jouluModule("unsigned-byte");
	}

	private static JavaBinModule jouluMidievents() {
		return jouluModule("midievents", commonsCodec, jouluUnsignedByte());
	}

	final JavaSrcModule mockodoreModelAddressing = srcModule(
			"mockodore-model-addressing").noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte()).testDeps(junit).end();

	final JavaSrcModule mockodoreModelLabels = srcModule(
			"mockodore-model-labels").noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing)
					.testDeps(junit).end();

	final JavaSrcModule mockodoreModelMachine = srcModule(
			"mockodore-model-machine")
					.noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing,
							mockodoreModelLabels)
					.testDeps(guavaTestlib, junit).end();

	final JavaSrcModule mockodoreApiAssylang = srcModule(
			"mockodore-api-assylang").noMainResources().noTestJava()
					.noTestResources()
					.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing,
							mockodoreModelMachine, mockodoreModelLabels)
					.end();

	final JavaSrcModule mockodoreModelSid = srcModule("mockodore-model-sid")
			.noMainResources().noTestResources()
			.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing,
					mockodoreModelLabels, mockodoreModelMachine)
			.testDeps(junit).end();

	final JavaSrcModule mockodoreProgram = srcModule("mockodore-program")
			.noMainResources().noTestResources()
			.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
					mockodoreApiAssylang, mockodoreModelAddressing,
					mockodoreModelLabels, mockodoreModelMachine)
			.testDeps(junit).end();

	final JavaSrcModule mockodoreUtilSysex = srcModule("mockodore-util-sysex")
			.noMainResources().noTestResources().mainDeps(jouluUnsignedByte())
			.testDeps(junit).end();

	final JavaSrcModule mockodoreLibBasicloader = srcModule(
			"mockodore-lib-basicloader")
					.noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
							mockodoreModelAddressing, mockodoreModelLabels,
							mockodoreModelMachine, mockodoreProgram)
					.testDeps(junit).end();

	final JavaSrcModule mockodoreSidfile = srcModule("mockodore-sidfile")
			.noMainResources()
			.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
					mockodoreModelAddressing, mockodoreModelLabels,
					mockodoreModelMachine, mockodoreProgram)
			.testDeps(commonsIo, hvsc, junit).end();

	final JavaSrcModule mockodoreLibMisc = srcModule("mockodore-lib-misc")
			.noMainResources().noTestResources()
			.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
					mockodoreLibBasicloader, mockodoreModelAddressing,
					mockodoreModelLabels, mockodoreModelMachine,
					mockodoreProgram, mockodoreSidfile)
			.testDeps(commonsIo, junit).end();

	final JavaSrcModule mockodoreLibSidplayer = srcModule(
			"mockodore-lib-sidplayer")
					.noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
							mockodoreLibBasicloader, mockodoreLibMisc,
							mockodoreModelAddressing, mockodoreModelLabels,
							mockodoreModelMachine, mockodoreProgram,
							mockodoreSidfile)
					.testDeps(commonsIo, hvsc, junit).end();

	final JavaSrcModule mockodoreSimulator = srcModule("mockodore-simulator")
			.noMainResources().noTestResources()
			.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang,
					mockodoreModelAddressing, mockodoreModelLabels,
					mockodoreModelMachine, mockodoreProgram)
			.testDeps(junit).end();

	final JavaSrcModule mockodoreUtilScreenMemory = srcModule(
			"mockodore-util-screen-memory")
					.noMainResources().noTestResources()
					.mainDeps(jouluUnsignedByte(), junit,
							mockodoreModelAddressing, mockodoreModelLabels,
							mockodoreModelMachine, mockodoreSimulator)
					.testDeps(mockodoreApiAssylang, mockodoreProgram).end();

	final JavaSrcModule mockodoreUtilSidripper = srcModule(
			"mockodore-util-sidripper")
					.noMainResources().noTestResources()
					.mainDeps(jouluMidievents(), jouluUnsignedByte(),
							mockodoreModelAddressing, mockodoreModelLabels,
							mockodoreModelMachine, mockodoreModelSid,
							mockodoreUtilSysex)
					.testDeps(junit).end();

	final JavaSrcModule mockodoreAppWidi64 = srcModule("mockodore-app-widi64")
			.noMainResources().noTestResources()
			.mainDeps(jouluMidievents(), jouluUnsignedByte(),
					mockodoreApiAssylang, mockodoreLibBasicloader,
					mockodoreModelAddressing, mockodoreModelLabels,
					mockodoreModelMachine, mockodoreModelSid, mockodoreProgram)
			.testDeps(commonsIo, junit, mockodoreSimulator,
					mockodoreUtilSidripper, mockodoreUtilSysex)
			.end();

}
