package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.repository.MavenRepository
import dev.skzr.lib.dependency.repository.RepositoryConfig
import dev.skzr.lib.dependency.tree.DependencyTree
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DependencyTreeTest {
	@Test
	fun locateDependency() {
		// create dep tree
		val repositoryConfig = RepositoryConfig()
		repositoryConfig.addRepository(MavenRepository("https://repo1.maven.org/maven2"))
		val builder = Dependencies()
			.useRepositoryConfig(repositoryConfig)
		val dependencyTree = DependencyTree(builder)
		// add root-level dependencies
		dependencyTree.addDependency(
			Dependency("com.squareup.okhttp3:okhttp:5.0.0-alpha.2"),
		)
		// resolve entire dependency tree
		dependencyTree.resolveDependencies()
		// locate the target dependency
		// top-level
		Assertions.assertNotNull(dependencyTree.locateDependency("com.squareup.okhttp3:okhttp:5.0.0-alpha.2"))
		// 2nd level
		Assertions.assertNotNull(dependencyTree.locateDependency("org.jetbrains.kotlin:kotlin-stdlib:1.4.10"))
		// 3rd level
		Assertions.assertNotNull(dependencyTree.locateDependency("org.jetbrains.kotlin:kotlin-stdlib-common:1.4.10"))
	}
}
