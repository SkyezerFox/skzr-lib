package dev.skzr.lib.dependency.artifact

import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Convert a [NodeList] to a [List] of [Node]s.
 */
internal fun NodeList.asArray(): List<Node> {
	val out = mutableListOf<Node>()
	for (i in 0 until this.length) {
		out.add(this.item(i))
	}
	return out
}

