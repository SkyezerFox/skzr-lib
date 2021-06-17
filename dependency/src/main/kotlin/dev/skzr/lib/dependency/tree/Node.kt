package dev.skzr.lib.dependency.tree

import dev.skzr.lib.dependency.Dependencies
import dev.skzr.lib.dependency.Dependency

/**
 * Represents a generic tree node.
 */
abstract class Node(val parent: Node?) {
	/**
	 * Fetch the root node.
	 */
	val root: DependencyTree
		get() {
			// if this has no parent, must be the root.
			if (this.parent == null) {
				return this as DependencyTree
			}
			// this has a parent, can't be root - check next node.
			return parent.root
		}

	/**
	 * A reference to the dependency builder object.
	 */
	open val builder: Dependencies
		get() = this.root.builder

	/**
	 * The children of this node.
	 */
	private val children = mutableListOf<Node>()

	/**
	 * Recursively descend through the tree and locate the target artifact.
	 */
	open fun locateDependency(dependencyNotation: String): DependencyNode? {
		// check if dependency is already in tree
		if (this.root.dependencies.containsKey(dependencyNotation)) {
			return this.root.dependencies[dependencyNotation]
		}
		// iterate over children and locate artifact
		for (child in this.children) {
			val node = child.locateDependency(dependencyNotation)
			if (node != null) {
				// cache dependency
				this.root.dependencies[node.dependency.dependencyNotation] = node
				return node
			}
		}
		// no artifact was found
		return null
	}

	/**
	 * Test if the target dependency exists from this node downwards.
	 */
	fun exists(dependency: Dependency): Boolean {
		return this.exists(dependency.dependencyNotation)
	}

	/**
	 * Test if the target dependency exists from this node downwards.
	 */
	fun exists(gradleNotation: String): Boolean {
		if (this.locateDependency(gradleNotation) != null) {
			return true
		}
		return false
	}

	/**
	 * Add a new artifact to this node. This will create a new [DependencyNode], or an [DependencyNodeReference] if
	 * the same artifact was found elsewhere in the tree.
	 */
	fun addDependency(dependency: Dependency) {
		val artifactNode = this.root.locateDependency(dependency.dependencyNotation)
		// check if the node is already on the tree
		if (artifactNode != null) {
			this.children.add(DependencyNodeReference(this, artifactNode))
		} else {
			val node = DependencyNode(this, dependency)
			this.children.add(DependencyNode(this, dependency))
			// store in hashmap for quick access
			this.root.dependencies[dependency.dependencyNotation] = node
		}
	}

	/**
	 * Add multiple artifacts to this node.
	 */
	fun addDependency(vararg dependencies: Dependency) {
		dependencies.forEach { this.addDependency(it) }
	}

	/**
	 * Resolve all dependencies of this node.
	 */
	fun resolveDependencies() {
		this.builder.logger.info("Resolving dependencies...")
		// filter for artifact nodes
		val artifactNodes = this.children.filterIsInstance<DependencyNode>()
		val childDependencies = this.builder.dependencyResolver.fetchDependencyPoms(artifactNodes.map { it.dependency })
			.flatMap {
				it.dependencies
			}
		// add child dependencies
		artifactNodes.forEachIndexed { index, node ->
			run {
				this.builder.logger.info("Adding dependency '${childDependencies[index].dependencyNotation}' to '${node.dependency.dependencyNotation}'")
				node.addDependency(childDependencies[index])
			}
		}
		// recursively descend through the tree and resolve child node dependencies.
		this.children.forEach {
			it.resolveDependencies()
		}
	}

	/**
	 * Return a list of all dependencies required by this tree.
	 */
	open fun getDependencies(): List<Dependency> {
		return this.children.flatMap { it.getDependencies() }
	}
}
