package expo.modules.securitychecker

import android.os.Build

data class EmulatorReportEvidence(
    var emulatorDetected: List<String>? = null
)

data class EmulatorReport(
    override var value: Boolean? = null,
    override var evidence: EmulatorReportEvidence = EmulatorReportEvidence()
) : DetectorReport<EmulatorReportEvidence>

class EmulatorDetector : Detector<EmulatorReport> {
    override fun runDetection(): EmulatorReport {
        val report = EmulatorReport()

        // Emulator detection logic
        val isEmulator = isRunningOnEmulator()
        report.value = isEmulator

        if (isEmulator) {
            report.evidence.emulatorDetected = listOf("Running on emulator")
        }

        return report
    }

    private fun isRunningOnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }
}

fun EmulatorReportEvidence.toJSMap(): Map<String, Any?> {
    return mapOf(
        "emulatorDetected" to emulatorDetected,
    )
}

fun EmulatorReport.toJSMap(): Map<String, Any?> {
    return mapOf(
        "value" to value,
        "evidence" to evidence.toJSMap()
    )
}