package dev.skzr.lib.dependency.tree

import dev.skzr.lib.dependency.Dependencies

/**
 * A tree of dependencies. This tree is evaluated at runtime in order to download dependencies,
 * as well as dependencies of dependencies etc.
 */
class DependencyTree(override val builder: Dependencies) : Node(null) {
	internal val dependencies = mutableMapOf<String, DependencyNode>()
	private var built = false

	/**
	 * Rebuild the tree if it was already built.
	 */
	fun forceBuild(): DependencyTree {
		// set build state to false
		this.built = false
		// rebuild
		this.build()
		return this
	}

	/**
	 * Build the dependency tree.
	 */
	fun build(): DependencyTree {
		// prevent rebuild
		if (this.built) {
			return this
		}
		// recursively descend through tree and resolve dependencies
		this.resolveDependencies()
		// set build state
		this.built = true
		return this
	}
}
