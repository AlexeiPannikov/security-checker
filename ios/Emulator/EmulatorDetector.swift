class EmulatorDetector: Detector {

    func runDetection() -> EmulatorReport {
        var report = EmulatorReport()

        #if targetEnvironment(simulator)
            report.value = true
            report.evidence.emulatorDetected = ["Running on simulator"]
        #else
            report.value = false
        #endif

        return report
    }
}
