package dev.skzr.lib.dependency.tree

/**
 * Represents a reference to an artifact node. This node is used to solve recursive
 * dependency declaration.
 */
class DependencyNodeReference(parent: Node, val node: DependencyNode) : Node(parent)
