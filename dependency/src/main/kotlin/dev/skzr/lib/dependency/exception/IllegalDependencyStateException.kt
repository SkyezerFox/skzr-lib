package dev.skzr.lib.dependency.exception

import dev.skzr.lib.dependency.Dependency

/**
 * An exception thrown when an attempted action is performed on a dependency in the incorrect state.
 */
class IllegalDependencyStateException(val dependency: Dependency, val expected: Dependency.State) : RuntimeException() {
	override val message: String?
		get() = "Expected dependency '${
			this.dependency.dependencyNotation
		}' state '${
			expected
		}', got '${
			this.dependency.state
		}'"
}
