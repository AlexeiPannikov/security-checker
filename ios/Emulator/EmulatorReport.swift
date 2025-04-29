struct EmulatorReportEvidence: Encodable {
    var emulatorDetected: [String]?
}

struct EmulatorReport: DetectorReport {
    var value: Bool?
    var evidence = EmulatorReportEvidence()
}
