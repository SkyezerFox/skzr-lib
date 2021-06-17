package dev.skzr.lib.dependency.tree

import dev.skzr.lib.dependency.Dependency

/**
 * Represents a dependency node. This holds a [Dependency] instance, as well as its children dependencies.
 */
class DependencyNode(parent: Node, val dependency: Dependency) : Node(parent) {
	override fun locateDependency(dependencyNotation: String): DependencyNode? {
		// check if this node's artifact has the correct notation
		if (this.dependency.dependencyNotation == dependencyNotation) {
			return this
		}
		// not in this node, locate in children
		return super.locateDependency(dependencyNotation)
	}

	override fun getDependencies(): List<Dependency> {
		// return child dependencies with this node's artifact
		return super.getDependencies().plus(this.dependency)
	}
}
