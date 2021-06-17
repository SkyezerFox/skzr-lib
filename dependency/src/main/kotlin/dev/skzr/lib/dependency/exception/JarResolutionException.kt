package dev.skzr.lib.dependency.exception

import dev.skzr.lib.dependency.Dependency

/**
 * An exception thrown when the dependency's jar file could not be found.
 */
class JarResolutionException(val dependency: Dependency) : RuntimeException() {
	override val message: String
		get() = "Failed to fetch jar file for dependency '${dependency.dependencyNotation}'"
}
