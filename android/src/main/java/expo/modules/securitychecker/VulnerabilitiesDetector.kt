package expo.modules.securitychecker

import android.util.Log
import org.json.JSONObject

data class VulnerabilitiesReport(
    val root: RootReport? = null,
    val debugger: DebuggerReport? = null,
    val emulator: EmulatorReport? = null
) {
    val isSecure: Boolean
        get() = !(root?.value == true || debugger?.value == true || emulator?.value == true)
}

fun interface VulnerabilitiesDetectorDelegate {
    fun onChange(report: VulnerabilitiesReport)
}

class VulnerabilitiesDetector(
    private val debuggerDetector: DebuggerDetector = DebuggerDetector(),
    private val rootDetector: RootDetector = RootDetector(),
    private val emulatorDetector: EmulatorDetector = EmulatorDetector()
) : DebuggerDetectorDelegate {

    private var report: VulnerabilitiesReport? = null
    var delegate: VulnerabilitiesDetectorDelegate? = null

    override fun debuggerWasAttached(report: DebuggerReport) {
        if (this.report == null) {
            this.report = VulnerabilitiesReport(debugger = report)
        } else {
            this.report = this.report?.copy(debugger = report)
        }
        delegate?.onChange(this.report!!)
    }

    fun runDetection(): VulnerabilitiesReport {
        debuggerDetector.delegate = this

        val rootReport = rootDetector.runDetection()
        val debuggerReport = debuggerDetector.runDetection()
        val emulatorReport = emulatorDetector.runDetection()

        val result = VulnerabilitiesReport(
            root = rootReport,
            debugger = debuggerReport,
            emulator = emulatorReport
        )

        Log.d("SecurityChecker", "Report = ${result.toJSMap()}" )

        this.report = result

        debuggerDetector.startMonitoring()

        return result
    }
}

fun VulnerabilitiesReport.toJSMap(): Map<String, Any?> {
    return mapOf(
        "isSecure" to isSecure,
        "root" to root?.toJSMap(),
        "debugger" to debugger?.toJSMap(),
        "emulator" to emulator?.toJSMap()
    )
}

fun JSONObject.toMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    val keys = keys()
    while (keys.hasNext()) {
        val key = keys.next()
        map[key] = this.opt(key)
    }
    return map
}