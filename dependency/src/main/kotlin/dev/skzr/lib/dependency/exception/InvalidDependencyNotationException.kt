package dev.skzr.lib.dependency.exception

/**
 * An exception thrown when a string cannot be parsed as Gradle dependency notation.
 */
class InvalidDependencyNotationException(private val notation: String) : IllegalArgumentException() {
	override val message: String
		get() = "Invalid dependency notation '${notation}'"
}
