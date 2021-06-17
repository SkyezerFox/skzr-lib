package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.exception.InvalidDestinationException
import dev.skzr.lib.dependency.repository.RepositoryConfig
import dev.skzr.lib.dependency.tree.DependencyTree
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

/**
 * Parallelize the given array of methods, throwing any exceptions any
 * worker threads may encounter.
 */
fun <T : Any> ExecutorService.parallelize(tasks: List<() -> T>): List<T> {
	var exception: Exception? = null
	// invoke tasks
	val results = this.invokeAll(tasks.map { Callable { return@Callable try { it.invoke() } catch (e: Exception) { exception = e; null } } })
	// check if there was an exception
	if (exception != null) {
		throw exception as Exception
	}
	// wait for futures and return results
	return results.map { it.get() }.requireNoNulls()
}


/**
 * A utility builder class used for building dependency sets that are
 * downloaded at runtime.
 */
class Dependencies {
	// worker pool
	internal val workers = Executors.newWorkStealingPool()

	// list of registered dependencies
	internal var logger = Logger.getLogger("dependencies")
	internal var repositoryConfig = RepositoryConfig()

	// the directory to download dependencies to
	internal var dependencyDirectory = File("./")

	internal var dependencyTree = DependencyTree(this)
	internal var dependencyResolver = DependencyResolver(this)
	internal val downloader = Downloader(this)
	internal var checksumValidator = ChecksumValidator(this)


	/**
	 * Set the logger being used by this dependency builder.
	 */
	fun setLogger(logger: Logger): Dependencies {
		this.logger = logger
		return this
	}

	/**
	 * Add a dependency using split gradle dependency notation.
	 */
	fun addDependency(groupId: String, artifactId: String, version: String): Dependencies {
		return this.addDependency(Dependency(groupId, artifactId, version))
	}

	/**
	 * Add a dependency using gradle dependency notation.
	 */
	fun addDependency(dependencyNotation: String): Dependencies {
		return this.addDependency(Dependency(dependencyNotation))
	}

	/**
	 * Add a dependency using a constructed [Dependency] instance.
	 */
	fun addDependency(dependency: Dependency): Dependencies {
		this.dependencyTree.addDependency(dependency)
		return this
	}

	/**
	 * Use the given [RepositoryConfig] when resolving dependencies.
	 */
	fun useRepositoryConfig(config: RepositoryConfig): Dependencies {
		this.repositoryConfig = config
		return this
	}

	/**
	 * Invoke the given lambda function and apply the [RepositoryConfig] to thie builder.
	 */
	fun applyRepositoryConfig(consumer: (config: RepositoryConfig) -> RepositoryConfig): Dependencies {
		this.repositoryConfig = consumer.invoke(RepositoryConfig())
		return this
	}

	/**
	 * Use the target dependency directory.
	 */
	fun useDependencyDirectory(file: File): Dependencies {
		this.dependencyDirectory = file
		// if directory doesn't exist, and we couldn't make it successfully
		if (!this.dependencyDirectory.exists() && !this.dependencyDirectory.mkdirs()) {
			throw RuntimeException("Failed to create download directory")
		}
		// check if output is directory
		if (!this.dependencyDirectory.isDirectory) {
			throw InvalidDestinationException(this.dependencyDirectory)
		}
		return this
	}

	/**
	 * Ensure that all dependencies are valid.
	 */
	fun ensure() {
		this.dependencyTree.build()
		val flattenedDependencies = this.dependencyTree.getDependencies()
		this.downloader.downloadDependencies(flattenedDependencies)
		this.checksumValidator.verifyChecksums(flattenedDependencies)
	}

	/**
	 * Use the default repository config.
	 */
	fun useDefaultRepositoryConfig(): Dependencies {
		this.repositoryConfig = RepositoryConfig.defaultConfig
		return this
	}
}
