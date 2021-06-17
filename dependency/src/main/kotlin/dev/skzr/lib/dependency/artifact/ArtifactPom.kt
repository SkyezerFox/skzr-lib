package dev.skzr.lib.dependency.artifact

import dev.skzr.lib.dependency.Dependency
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Represents a resolved Maven POM file.
 */
class ArtifactPom(
	groupId: String,
	artifactId: String,
	version: String,
	val dependencies: List<ArtifactPomDependency>
) : Dependency(groupId, artifactId, version) {
	companion object {
		/**
		 * Create an [ArtifactPom] from the given [Document].
		 */
		fun fromDocument(doc: Document): ArtifactPom {
			val pom = doc.firstChild as Element
			// extract basic info
			val groupId = pom.getElementsByTagName("groupId").item(0).textContent
			val artifactId = pom.getElementsByTagName("artifactId").item(0).textContent
			val version = pom.getElementsByTagName("version").item(0).textContent
			// extract dep info
			val artifacts = pom.getElementsByTagName("dependencies")
			// check if no dependencies
			if (artifacts.length == 0) {
				return ArtifactPom(groupId, artifactId, version, listOf())
			}
			// resolve dependencies
			val deps = artifacts.item(0).childNodes.asArray().filter {
				it.hasChildNodes()
			}.map {
				ArtifactPomDependency.fromElement(it as Element)
			}
			// return pom
			return ArtifactPom(groupId, artifactId, version, deps)
		}
	}

	/**
	 * Convert this [ArtifactPom] into an [Dependency].
	 */
	fun createDependency(): Dependency {
		return Dependency(this.groupId, this.artifactId, this.version)
	}
}
