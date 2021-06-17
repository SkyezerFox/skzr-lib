package dev.skzr.lib.dependency.repository

import dev.skzr.lib.dependency.Dependency
import dev.skzr.lib.dependency.artifact.ArtifactPom
import dev.skzr.lib.dependency.artifact.ArtifactPomDependency
import dev.skzr.lib.dependency.artifact.asArray
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSource
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Convert this dependency into a Maven URI.
 */
fun Dependency.toMavenURI(endpoint: HttpUrl, suffix: String?): HttpUrl {
	val builder = endpoint.newBuilder()
	// split group id and add
	this.groupId.split(".").forEach {
		builder.addPathSegment(it)
	}
	// add path segments
	return builder
		.addPathSegment(this.artifactId)
		.addPathSegment(this.version)
		.addPathSegment(this.qualifier + (suffix ?: ""))
		.build()
}

/**
 * A Maven repository found at the target endpoint.
 */
internal class MavenRepository(val endpoint: HttpUrl) : Repository() {
	private val factory = DocumentBuilderFactory.newDefaultInstance()
	override val name = this.endpoint.toString()

	/**
	 * Construct a new Maven repository using a string URL.
	 */
	constructor(endpoint: String) : this(endpoint.toHttpUrl())

	override fun fetchDependencyPom(dependency: Dependency): ArtifactPom? {
		return try {
			// build request
			val request = Request.Builder()
				.method("GET", null)
				.url(dependency.toMavenURI(this.endpoint, ".pom"))
				.build()
			// make request
			val response = OkHttpClient().newCall(request).execute()
			// create pom
			val pom = factory.newDocumentBuilder().parse(InputSource(StringReader(response.body!!.string()))).firstChild as Element
			// get base info
			val groupId = pom.getElementsByTagName("groupId").item(0).textContent
			val artifactId = pom.getElementsByTagName("artifactId").item(0).textContent
			val version = pom.getElementsByTagName("version").item(0).textContent
			// fetch dependencies
			val dependencies = pom.getElementsByTagName("dependencies")
				.item(0)
				.childNodes.asArray()
				.filter {
					it.hasChildNodes()
				}.map {
					ArtifactPomDependency.fromElement(it as Element)
				}
			// return
			ArtifactPom(groupId, artifactId, version, dependencies)
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	override fun fetchDependencyChecksum(dependency: Dependency): String? {
		return try {
			val request = Request.Builder()
				.method("GET", null)
				.url(dependency.toMavenURI(this.endpoint, ".jar.md5"))
				.build()
			// make request
			OkHttpClient().newCall(request).execute().body?.string()
		} catch(e: Exception) {
			e.printStackTrace()
			null
		}
	}

	override fun fetchDependencyJar(dependency: Dependency): BufferedSource? {
		return try {
			val request = Request.Builder()
				.method("GET", null)
				.url(dependency.toMavenURI(this.endpoint, ".jar"))
				.build()
			OkHttpClient().newCall(request).execute().body?.source()
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}
}
