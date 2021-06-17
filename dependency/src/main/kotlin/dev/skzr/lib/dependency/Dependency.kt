package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.artifact.ArtifactPom
import dev.skzr.lib.dependency.exception.IllegalDependencyStateException
import dev.skzr.lib.dependency.exception.InvalidDependencyNotationException
import dev.skzr.lib.dependency.repository.Repository
import okio.BufferedSource
import java.io.File

/**
 * Test if the given dependency notation is valid.
 */
internal fun validateDependencyNotation(input: String): Boolean {
	return Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]:[A-z0-9_]+:[A-z0-9-_.]+\$").matches(input)
}

/**
 * Represents a runtime-downloaded dependency.
 */
open class Dependency {
	/**
	 * Represents the state of the current dependency.
	 */
	enum class State {
		/**
		 * The dependency has not been resolved.
		 */
		UNRESOLVED,

		/**
		 * The dependency has been resolved, but has yet to be downloaded.
		 */
		RESOLVED,

		/**
		 * The dependency has been downloaded, but is awaiting verification of its checksum.
		 */
		DOWNLOADED,

		/**
		 * The dependency is fully downloaded and verified.
		 */
		VERIFIED,

		/**
		 * The dependency has been injected into the classpath.
		 */
		INJECTED
	}


	/**
	 * The group ID of this dependency.
	 */
	val groupId: String

	/**
	 * The artifact ID of this dependency.
	 */
	val artifactId: String

	/**
	 * The version of this dependency.
	 */
	val version: String

	/**
	 * The repository that resolved this dependency.
	 */
	var repository: Repository? = null

	/**
	 * The POM file that represents this dependency.
	 */
	var pom: ArtifactPom? = null

	/**
	 * The file that this dependency was downloaded to.
	 */
	var file: File? = null

	/**
	 * The current state of this dependency.
	 */
	var state: State = State.UNRESOLVED

	/**
	 * Create a new dependency using split gradle dependency notation.
	 */
	constructor(groupId: String, artifactId: String, version: String) {
		this.groupId = groupId
		this.artifactId = artifactId
		this.version = version
	}

	/**
	 * Create a new dependency using gradle dependency notation.
	 */
	constructor(dependencyNotation: String) {
		// ensure is valid
		if (!validateDependencyNotation(dependencyNotation)) {
			throw InvalidDependencyNotationException(dependencyNotation)
		}
		// set
		this.groupId = dependencyNotation.split(":")[0]
		this.artifactId = dependencyNotation.split(":")[1]
		this.version = dependencyNotation.split(":")[2]
	}

	/**
	 * Assert that this dependency's state equals the given state.
	 */
	fun assertState(expected: State) {
		if (this.state != expected) {
			throw IllegalDependencyStateException(this, expected)
		}
	}

	/**
	 * The dependency notation that represents this dependency.
	 */
	val dependencyNotation
		get() = "${this.groupId}:${this.artifactId}:${this.version}"

	/**
	 * The qualifier that represents this dependency. This is used as the name
	 * of the .jar file when this dependency is downloaded.
	 */
	val qualifier
		get() = "${this.artifactId}-${this.version}"
}
