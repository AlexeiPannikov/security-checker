package expo.modules.securitychecker

import android.os.Build
import java.io.File
import kotlin.reflect.KMutableProperty0

data class RootReportEvidence(
    var detectedRootFiles: List<String>? = null,
    var systemWriteAccess: List<String>? = null,
    var suspiciousSymlinks: List<String>? = null,
    var injectedLibraries: List<String>? = null,
    var dangerousSystemProperties: List<String>? = null
) {
    val isEmpty: Boolean
        get() = detectedRootFiles.isNullOrEmpty()
                && systemWriteAccess.isNullOrEmpty()
                && suspiciousSymlinks.isNullOrEmpty()
                && injectedLibraries.isNullOrEmpty()
                && dangerousSystemProperties.isNullOrEmpty()
}

data class RootReport(
    override var value: Boolean? = null,
    override var evidence: RootReportEvidence = RootReportEvidence()
) : DetectorReport<RootReportEvidence>

class RootDetector : Detector<RootReport> {

    private fun hasSuperuserApk(): List<String>? {
        val path = "/system/app/Superuser.apk"
        return if (File(path).exists()) {
            listOf("Found Superuser APK at: $path")
        } else null
    }

    private fun canExecuteSu(): List<String>? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val result = process.inputStream.bufferedReader().readLine()
            if (!result.isNullOrEmpty()) {
                listOf("Executable su found at: $result (via Runtime)")
            } else null
        } catch (e: Exception) {
            listOf("Exception when trying to execute su: ${e.message}")
        }
    }


    private fun hasTestKeys(): List<String>? {
        return if (Build.TAGS?.contains("test-keys") == true) {
            listOf("Build tags contain test-keys: ${Build.TAGS}")
        } else null
    }

    private fun canWriteToSystem(): List<String>? {
        val testPath = "/system/test_root.txt"
        return try {
            val file = File(testPath)
            file.writeText("test")
            file.delete()
            listOf("Able to write to protected path: $testPath")
        } catch (e: Exception) {
            null // expected on secure devices
        }
    }

    private fun hasSuBinary(): List<String>? {
        val paths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su"
        )
        val found = paths.filter { File(it).exists() }
        return if (found.isNotEmpty()) {
            found.map { it }
        } else null
    }

    private fun <T> appendIfNotNull(
        target: KMutableProperty0<List<T>?>,
        additions: List<T>?
    ) {
        if (!additions.isNullOrEmpty()) {
            target.set(target.get().orEmpty() + additions)
        }
    }


    override fun runDetection(): RootReport {
        val evidence = RootReportEvidence()

        appendIfNotNull(evidence::detectedRootFiles, hasSuBinary())
        appendIfNotNull(evidence::detectedRootFiles, hasSuperuserApk())
        appendIfNotNull(evidence::injectedLibraries, canExecuteSu())
        appendIfNotNull(evidence::dangerousSystemProperties, hasTestKeys())
        appendIfNotNull(evidence::systemWriteAccess, canWriteToSystem())

        return RootReport(value = !evidence.isEmpty, evidence = evidence)
    }
}

fun RootReport.toJSMap(): Map<String, Any?> {
    return mapOf(
        "value" to value,
        "evidence" to evidence.toJSMap()
    )
}

fun RootReportEvidence.toJSMap(): Map<String, Any?> {
    return mapOf(
        "detectedRootFiles" to detectedRootFiles,
        "systemWriteAccess" to systemWriteAccess,
        "suspiciousSymlinks" to suspiciousSymlinks,
        "injectedLibraries" to injectedLibraries,
        "dangerousSystemProperties" to dangerousSystemProperties
    )
}