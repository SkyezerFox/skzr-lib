package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.exception.JarResolutionException
import okio.sink
import java.io.File

/**
 * Provides implementations to download an entire dependency tree.
 */
class Downloader(
	private val builder: Dependencies,
) {
	// enable parallel downloading
	private val workers = builder.workers

	/**
	 * Attempt to download the target [Dependency].
	 */
	fun downloadDependency(dependency: Dependency) {
		dependency.assertState(Dependency.State.RESOLVED)
		val file = this.builder.dependencyDirectory.resolve("${dependency.qualifier}.jar")
		// check if the file already exists - we can validate it later
		if (file.exists()) {
			// set file
			dependency.file = file
			dependency.state = Dependency.State.DOWNLOADED
			return
		}
		// throw jar resolution exception if not found
		val source = dependency.repository!!.fetchDependencyJar(dependency)
			?: throw JarResolutionException(dependency)
		// write the file
		source.readAll(file.sink())
		// set file
		dependency.file = file
		dependency.state = Dependency.State.DOWNLOADED
	}

	/**
	 * Download the dependency tree to the target output directory.
	 */
	fun downloadDependencies(dependencies: List<Dependency>) {
		// if directory doesn't exist, and we couldn't make it successfully
		if (!builder.dependencyDirectory.exists() && !builder.dependencyDirectory.mkdirs()) {
			throw RuntimeException("Failed to create download directory")
		}
		// check if output is directory
		if (!builder.dependencyDirectory.isDirectory) {
			throw RuntimeException("Failed to download dependencies - target output is not a directory")
		}
		// download dependencies
		this.workers.parallelize(dependencies.map {{ this.downloadDependency(it) }})
	}
}
