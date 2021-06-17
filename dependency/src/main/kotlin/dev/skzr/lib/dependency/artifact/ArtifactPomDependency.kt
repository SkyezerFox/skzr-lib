package dev.skzr.lib.dependency.artifact

import dev.skzr.lib.dependency.Dependency
import org.w3c.dom.Element
import java.util.Locale

/**
 * Represents a POM file dependency artifact.
 */
class ArtifactPomDependency(
	groupId: String,
	artifactId: String,
	version: String,
	val scope: Scope,
) : Dependency(
	groupId, artifactId, version
) {
	companion object {
		/**
		 * Attempt to create an [ArtifactPomDependency] from an XML [Element].
		 */
		fun fromElement(el: Element): ArtifactPomDependency {
			// extract info and return artifact
			return ArtifactPomDependency(
				el.getElementsByTagName("groupId").item(0).textContent,
				el.getElementsByTagName("artifactId").item(0).textContent,
				el.getElementsByTagName("version").item(0).textContent,
				Scope.valueOf(el.getElementsByTagName("scope").item(0).textContent.uppercase())
			)
		}
	}

	/**
	 * The dependency scope of this artifact.
	 */
	enum class Scope {
		COMPILE,
		PROVIDED,
		RUNTIME,
		TEST,
		SYSTEM,
		IMPORT;
	}
}
