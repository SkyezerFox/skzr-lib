package dev.skzr.lib.dependency.exception

import java.io.File

class InvalidDestinationException(val destination: File) : RuntimeException() {
	override val message: String
		get() = "File '$destination' is not a valid directory"
}
