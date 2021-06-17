package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.artifact.ArtifactPom
import dev.skzr.lib.dependency.exception.DependencyResolutionException

/**
 * Manages the resolution of dependency POM files.
 */
internal class DependencyResolver(
	private val builder: Dependencies,
) {
	// reference to worker pool
	private val workers = this.builder.workers

	/**
	 * Attempt to resolve the target [Dependency] using one of the repositories
	 * defined in this resolver's configuration.
	 */
	fun fetchDependencyPom(dependency: Dependency): ArtifactPom {
		// check state is correct
		dependency.assertState(Dependency.State.UNRESOLVED)
		this.builder.logger.info("Locating POM file for '${dependency.dependencyNotation}'...")
		// iterate over repositories and fetch
		for (repo in this.builder.repositoryConfig.repositories) {
			this.builder.logger.info("Checking '${repo.name}'...")
			val resolved = repo.fetchDependencyPom(dependency)
			if (resolved != null) {
				this.builder.logger.info("Found POM file for '${dependency.dependencyNotation}' in repo '${repo.name}'")
				dependency.repository = repo
				dependency.pom = resolved
				dependency.state = Dependency.State.RESOLVED
				return resolved
			}
		}
		this.builder.logger.info("Failed to find POM file for '${dependency.dependencyNotation}'")
		// throw error
		throw DependencyResolutionException(dependency, this.builder.repositoryConfig.repositories)
	}

	/**
	 * Attempt to resolve the target list of dependencies using the repositories
	 * defined in the resolver's configuration.
	 */
	fun fetchDependencyPoms(dependencies: List<Dependency>): List<ArtifactPom> {
		this.builder.logger.fine("Resolving POMs for ${dependencies.size} dependencies...")
		return this.workers.parallelize(dependencies.map {{ this.fetchDependencyPom(it) }})
	}
}
