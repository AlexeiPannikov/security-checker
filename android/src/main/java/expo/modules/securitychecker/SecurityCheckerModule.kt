package expo.modules.securitychecker

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SecurityCheckerModule : Module(), VulnerabilitiesDetectorDelegate {
  private val vulnerabilitiesDetector = VulnerabilitiesDetector()
  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  override fun definition() = ModuleDefinition {
    Name("SecurityChecker")

    Events("onChange")

    OnCreate {
      vulnerabilitiesDetector.delegate = this@SecurityCheckerModule
    }

    Function("check") {
      val report = vulnerabilitiesDetector.runDetection()
      report.toJSMap()
    }
  }

  override fun onChange(report: VulnerabilitiesReport) {
    try {
      val encoded = report.toJSMap()
      sendEvent("onChange", encoded)
    } catch (e: Exception) {
      println("Error serializing report: ${e.message}")
    }
  }
}
