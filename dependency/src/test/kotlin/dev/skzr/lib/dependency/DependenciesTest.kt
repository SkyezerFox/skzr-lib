package dev.skzr.lib.dependency

import org.junit.jupiter.api.Test

class DependenciesTest {
	@Test
	fun testDependenciesExt() {
		dependency {
			useDefaultRepositoryConfig()
			dependencies {
				implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
			}
		}
	}
}