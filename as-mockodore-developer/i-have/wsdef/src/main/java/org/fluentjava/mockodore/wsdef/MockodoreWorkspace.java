package org.fluentjava.mockodore.wsdef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.fluentjava.iwant.api.core.Concatenated;
import org.fluentjava.iwant.api.core.Concatenated.ConcatenatedBuilder;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.model.SideEffect;
import org.fluentjava.iwant.api.model.Target;
import org.fluentjava.iwant.api.wsdef.SideEffectDefinitionContext;
import org.fluentjava.iwant.api.wsdef.TargetDefinitionContext;
import org.fluentjava.iwant.api.wsdef.Workspace;
import org.fluentjava.iwant.core.download.TestedIwantDependencies;
import org.fluentjava.iwant.eclipsesettings.EclipseSettings;
import org.fluentjava.iwant.plugin.jacoco.JacocoDistribution;
import org.fluentjava.iwant.plugin.jacoco.JacocoTargetsOfJavaModules;

public class MockodoreWorkspace implements Workspace {

	private final MockodoreModules modules = new MockodoreModules();

	@Override
	public List<? extends Target> targets(TargetDefinitionContext ctx) {
		List<Target> t = new ArrayList<>();
		t.add(jacocoReport());
		t.add(classpathStringOfAllModules());
		return t;
	}

	private Target classpathStringOfAllModules() {
		Set<JavaModule> cp = new LinkedHashSet<>();
		for (JavaSrcModule m : modules.allSrcModules()) {
			cp.addAll(m.effectivePathForMainRuntime());
		}
		ConcatenatedBuilder str = Concatenated
				.named("classpath-of-all-modules");
		str.string(".");
		for (JavaModule m : cp) {
			str.string(":");
			str.unixPathTo(m.mainArtifact());
		}
		return str.end();
	}

	@Override
	public List<? extends SideEffect> sideEffects(
			SideEffectDefinitionContext ctx) {
		return Arrays.asList(EclipseSettings.with().name("eclipse-settings")
				.modules(ctx.wsdefdefJavaModule(), ctx.wsdefJavaModule())
				.modules(modules.allSrcModules()).end());
	}

	private Target jacocoReport() {
		return JacocoTargetsOfJavaModules.with()
				.jacoco(JacocoDistribution.newestTestedVersion())
				.antJars(TestedIwantDependencies.antJar(),
						TestedIwantDependencies.antLauncherJar())
				.modules(modules.allSrcModules()).end()
				.jacocoReport("jacoco-report");
	}

}
