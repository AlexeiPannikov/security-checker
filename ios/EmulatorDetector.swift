struct EmulatorReportEvidence: Encodable {
    var emulatorDetected: [String]?
}

struct EmulatorReport: DetectorReport {
    var value: Bool?
    var evidence = EmulatorReportEvidence()
}

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
