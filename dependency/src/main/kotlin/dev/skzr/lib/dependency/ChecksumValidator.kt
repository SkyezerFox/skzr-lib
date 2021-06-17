package dev.skzr.lib.dependency

import dev.skzr.lib.dependency.exception.ChecksumMismatchException
import dev.skzr.lib.dependency.exception.ChecksumResolutionException
import okio.buffer
import okio.source
import java.nio.charset.Charset
import java.security.MessageDigest


fun String.toHex(): ByteArray {
	val out = ByteArray(this.length / 2)
	for (i in out.indices) {
		val index = i * 2
		val j: Int = this.substring(index, index + 2).toInt(16)
		out[i] = j.toByte()
	}
	return out
}

/**
 * Validates the checksums of downloaded dependencies.
 */
class ChecksumValidator(
	private val builder: Dependencies,
) {
	private val workers = this.builder.workers

	/**
	 * Verify the checksum of the given [Dependency].
	 */
	fun verifyChecksum(dependency: Dependency) {
		this.builder.logger.info("Verifying checksum for dependency '${dependency.dependencyNotation}'...")
		// ensure state is correct
		dependency.assertState(Dependency.State.DOWNLOADED)
		// fetch the checksums of the
		val expected = dependency.repository!!.fetchDependencyChecksum(dependency) ?:
			throw ChecksumResolutionException(dependency)
		val buffer = dependency.file!!.source().buffer()
		// create reference to digest
		val digest = MessageDigest.getInstance("MD5")
		// read bytes
		while (!buffer.exhausted()) {
			digest.update(buffer.readByte())
		}
		// compute checksum
		val actual = digest.digest()
		if (!actual.contentEquals(expected.toHex())) {
			throw ChecksumMismatchException(dependency, expected, actual.toString())
		}
		// set state
		this.builder.logger.info("Checksum OK")
		dependency.state = Dependency.State.VERIFIED
	}

	/**
	 * Verify all checksums of the given
	 */
	fun verifyChecksums(dependencies: List<Dependency>) {
		this.workers.parallelize(dependencies.map {{ this.verifyChecksum(it) }})
	}
}
