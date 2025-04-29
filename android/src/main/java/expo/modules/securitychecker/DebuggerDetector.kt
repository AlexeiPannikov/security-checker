package expo.modules.securitychecker

import android.os.Debug
import kotlinx.coroutines.*

data class DebuggerReportEvidence(
    var debuggerCheckError: List<String>? = null,
    var debuggerDetected: List<String>? = null
)

data class DebuggerReport(
    override var value: Boolean? = null,
    override var evidence: DebuggerReportEvidence = DebuggerReportEvidence()
) : DetectorReport<DebuggerReportEvidence>

fun interface DebuggerDetectorDelegate {
    fun debuggerWasAttached(report: DebuggerReport)
}

class DebuggerDetector(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    var delegate: DebuggerDetectorDelegate? = null
    private var lastReport: DebuggerReport? = null
    private var job: Job? = null

    fun runDetection(): DebuggerReport {
        val report = DebuggerReport()

        println("Debugger")
        println(Debug.isDebuggerConnected())

        try {
            if (Debug.isDebuggerConnected()) {
                report.value = true
                report.evidence.debuggerDetected = listOf("Debugger is attached (Debug.isDebuggerConnected)")
            } else {
                report.value = false
            }
        } catch (e: Exception) {
            report.value = null
            report.evidence.debuggerCheckError = listOf("Exception during debugger check: ${e.message}")
        }

        return report
    }

    fun startMonitoring(intervalMillis: Long = 3000L) {
        stopMonitoring()

        job = coroutineScope.launch {
            while (isActive) {
                val report = runDetection()
                if (lastReport !== null && lastReport?.value !== report.value ) {
                    withContext(Dispatchers.Main) {
                        delegate?.debuggerWasAttached(report)
                    }
                }
                lastReport = report
                delay(intervalMillis)
            }
        }
    }

    fun stopMonitoring() {
        job?.cancel()
        job = null
    }
}

fun DebuggerReportEvidence.toJSMap(): Map<String, Any?> {
    return mapOf(
        "debuggerCheckError" to debuggerCheckError,
        "debuggerDetected" to debuggerDetected
    )
}

fun DebuggerReport.toJSMap(): Map<String, Any?> {
    return mapOf(
        "value" to value,
        "evidence" to evidence.toJSMap()
    )
}