package dev.skzr.lib.dependency.exception

import dev.skzr.lib.dependency.Dependency

/**
 * An exception thrown when the checksum of a dependency could not be fetched.
 */
class ChecksumResolutionException(val dependency: Dependency) : RuntimeException() {
	override val message: String
		get() = "Failed to fetch checksum for dependency '${this.dependency.dependencyNotation}'"
}
