package dev.skzr.lib.dependency.repository

import dev.skzr.lib.dependency.Dependency
import dev.skzr.lib.dependency.artifact.ArtifactPom
import okio.BufferedSource

/**
 * Represents a generic repository resolver.
 */
abstract class Repository {
	abstract val name: String

	/**
	 * Attempt to resolve the target dependency in this repository.
	 */
	abstract fun fetchDependencyPom(dependency: Dependency): ArtifactPom?

	/**
	 * Attempt to fetch the hex checksum of the target dependency.
	 */
	abstract fun fetchDependencyChecksum(dependency: Dependency): String?

	/**
	 * Attempt to fetch the jar of the target dependency.
	 */
	abstract fun fetchDependencyJar(dependency: Dependency): BufferedSource?
}
