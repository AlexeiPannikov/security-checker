struct DebuggerReportEvidence: Encodable {
    var debuggerCheckError: [String]?
    var debuggerDetected: [String]?
}

struct DebuggerReport: DetectorReport {
    var value: Bool?
    var evidence = DebuggerReportEvidence()
}
