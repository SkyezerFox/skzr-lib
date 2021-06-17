package dev.skzr.lib.dependency

/**
 * The scope used for handling the repositories block.
 */
class RepositoryHandlerScope(private val dependencies: Dependencies) {}

/**
 * The scope used for handling the dependencies block.
 */
class DependenciesHandlerScope(private val dependencies: Dependencies) {
	/**
	 * Declare a new dependency using split gradle dependency notation.
	 */
	fun implementation(groupId: String, artifactId: String, version: String) {
		this.dependencies.addDependency(groupId, artifactId, version)
	}

	/**
	 * Declare a new dependency using gradle dependency notation.
	 */
	fun implementation(dependencyNotation: String) {
		this.dependencies.addDependency(dependencyNotation)
	}
}


/**
 * The scope used for handling the root dependency block.
 */
class DependencyHandlerScope(private val dependencies: Dependencies) {
	/**
	 * Use the default repository config.
	 */
	fun useDefaultRepositoryConfig() {
		this.dependencies.useDefaultRepositoryConfig()
	}

	/**
	 * Modify the dependency configuration.
	 */
	fun dependencies(consumer: DependenciesHandlerScope.() -> Unit) {
		consumer(DependenciesHandlerScope(this.dependencies))
	}

	/**
	 * Modify the repository configuration.
	 */
	fun repositories(consumer: RepositoryHandlerScope.() -> Unit) {
		consumer(RepositoryHandlerScope(this.dependencies))
	}
}

/**
 * Declare a new dependency set.
 */
fun dependency(consumer: DependencyHandlerScope.() -> Unit) {
	val dependencies = Dependencies()
	consumer(DependencyHandlerScope(dependencies))
	dependencies.ensure()
}
