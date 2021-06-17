package dev.skzr.lib.dependency.repository

/**
 * Holds information on available repositories used when resolving dependencies.
 */
class RepositoryConfig {
	internal val repositories = mutableListOf<Repository>()

	companion object {
		@JvmStatic
		val defaultConfig: RepositoryConfig
			get() = RepositoryConfig().addRepository(MavenRepository("https://repo1.maven.org/maven2"))
	}

	/**
	 * Add a repository to this config.
	 */
	fun addRepository(repository: Repository): RepositoryConfig {
		this.repositories.add(repository)
		return this
	}
}
