package dev.skzr.lib.dependency.exception

import dev.skzr.lib.dependency.Dependency
import dev.skzr.lib.dependency.repository.Repository

/**
 * An exception thrown when resolution of a dependency's POM file fails.
 */
class DependencyResolutionException(val dependency: Dependency, val searchedRepositories: List<Repository>) : RuntimeException() {
	override val message
		get() = "Failed to resolve dependency '${dependency.dependencyNotation}'\nRepositories checked: ${this.searchedRepositories.map { 
			it.name
		}.joinToString(",")}"
}
