package org.fluentjava.mockodore.wsdef;

import java.util.Set;

import org.fluentjava.iwant.api.core.SubPath;
import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaClasses;
import org.fluentjava.iwant.api.javamodules.JavaClasses.JavaClassesSpex;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.wsdef.WorkspaceContext;
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

	private static final JavaBinModule hvsc = JavaBinModule
			.providing(hvscC64MusicZip()).end();
	private final Set<JavaModule> junit5runnerMods;

	public MockodoreModules(WorkspaceContext ctx) {
		this.junit5runnerMods = ctx.iwantPlugin().junit5runner()
				.withDependencies();
		dependencyRoots();
	}

	private void dependencyRoots() {
		mockodoreLibSidplayer();
		mockodoreUtilScreenMemory();
		mockodoreAppSidex();
	}

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
				.commit("4168e92d2f81ddd6cb286b4fb450736e7b4498ec");
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

	private JavaSrcModule mockodoreModelAddressing() {
		return lazy(
				() -> srcModule("mockodore-model-addressing").noMainResources()
						.noTestResources().mainDeps(jouluUnsignedByte())
						.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreModelLabels() {
		return lazy(() -> srcModule("mockodore-model-labels").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreModelMachine() {
		return lazy(() -> srcModule("mockodore-model-machine").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing(),
						mockodoreModelLabels())
				.testDeps(guavaTestlib).testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreApiAssylang() {
		return lazy(() -> srcModule("mockodore-api-assylang").noMainResources()
				.noTestJava().noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing(),
						mockodoreModelMachine(), mockodoreModelLabels())
				.end());
	}

	private JavaSrcModule mockodoreModelSid() {
		return lazy(() -> srcModule("mockodore-model-sid").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing(),
						mockodoreModelLabels(), mockodoreModelMachine())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreProgram() {
		return lazy(() -> srcModule("mockodore-program").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreApiAssylang(), mockodoreModelAddressing(),
						mockodoreModelLabels(), mockodoreModelMachine())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreUtilSysex() {
		return lazy(() -> srcModule("mockodore-util-sysex").noMainResources()
				.noTestResources().mainDeps(jouluUnsignedByte())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreLibBasicloader() {
		return lazy(() -> srcModule("mockodore-lib-basicloader")
				.noMainResources().noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreProgram())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreSidfile() {
		return lazy(() -> srcModule("mockodore-sidfile").noMainResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreProgram())
				.testDeps(commonsIo, hvsc).testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreLibMisc() {
		return lazy(() -> srcModule("mockodore-lib-misc").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreLibBasicloader(), mockodoreModelAddressing(),
						mockodoreModelLabels(), mockodoreModelMachine(),
						mockodoreProgram(), mockodoreSidfile())
				.testDeps(commonsIo).testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreLibSidplayer() {
		return lazy(() -> srcModule("mockodore-lib-sidplayer").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreLibBasicloader(), mockodoreLibMisc(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreProgram(),
						mockodoreSidfile())
				.testDeps(commonsIo, hvsc).testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreSimulator() {
		return lazy(() -> srcModule("mockodore-simulator").noMainResources()
				.noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreApiAssylang(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreProgram())
				.testDeps(junit5runnerMods).end());
	}

	// TODO don't depend on the whole junit5runner, only junit modules
	private JavaSrcModule mockodoreUtilScreenMemory() {
		return lazy(() -> srcModule("mockodore-util-screen-memory")
				.noMainResources().noTestResources()
				.mainDeps(jouluUnsignedByte(), mockodoreModelAddressing(),
						mockodoreModelLabels(), mockodoreModelMachine(),
						mockodoreSimulator())
				.mainDeps(junit5runnerMods)
				.testDeps(mockodoreApiAssylang(), mockodoreProgram()).end());
	}

	private JavaSrcModule mockodoreUtilSidripper() {
		return lazy(() -> srcModule("mockodore-util-sidripper")
				.noMainResources().noTestResources()
				.mainDeps(jouluMidievents(), jouluUnsignedByte(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreModelSid(),
						mockodoreUtilSysex())
				.testDeps(junit5runnerMods).end());
	}

	private JavaSrcModule mockodoreAppSidex() {
		return lazy(() -> srcModule("mockodore-app-sidex").noMainResources()
				.noTestResources()
				.mainDeps(jouluMidievents(), jouluUnsignedByte(),
						mockodoreApiAssylang(), mockodoreLibBasicloader(),
						mockodoreModelAddressing(), mockodoreModelLabels(),
						mockodoreModelMachine(), mockodoreModelSid(),
						mockodoreProgram())
				.testDeps(commonsIo, mockodoreSimulator(),
						mockodoreUtilSidripper(), mockodoreUtilSysex())
				.testDeps(junit5runnerMods).end());
	}

}
