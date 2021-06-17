package dev.skzr.lib.dependency.exception

import dev.skzr.lib.dependency.Dependency

/**
 * An exception thrown when a dependency's checksum does not match the remote.
 */
class ChecksumMismatchException(val dependency: Dependency, val expected: String, val actual: String) : RuntimeException() {
	override val message: String
		get() = "Checksum of dependency '${dependency.dependencyNotation}' did not match its expected value.\nExpected: ${expected}\nActual: $actual"
}
