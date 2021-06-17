package dev.skzr.lib.dependency;

import dev.skzr.lib.dependency.exception.InvalidDependencyNotationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class DependenciesTestJava {
	@Test
	void testUseRepositoryConfig() {

	}

	@Test
	void testAddDependency() {
		// test valid dep
		Assertions.assertDoesNotThrow(() -> {
			new Dependencies()
				.addDependency("com.squareup.okhttp3:okhttp:5.0.0-alpha.2");
		});
		// dest invalid dep
		Assertions.assertThrows(InvalidDependencyNotationException.class, () -> {
			new Dependencies()
				.addDependency("uwu i'm not a dependency");
		});
	}

	@Test
	void testResolveDependency() {
		new Dependencies()
			.useDefaultRepositoryConfig()
			.useDependencyDirectory(new File("./test_dependencies"))
			.addDependency("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
			.ensure();
	}
}
