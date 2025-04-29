struct JailbreakReportEvidence: Encodable {
    var suspiciousFiles: [String]?
    var sandboxEscape: [String]?
    var symbolicLinks: [String]?
    var injectedLibraries: [String]?
    var systemPreferencesAccess: [String]?

    var isEmpty: Bool {
        (suspiciousFiles?.isEmpty ?? true) && (sandboxEscape?.isEmpty ?? true)
            && (symbolicLinks?.isEmpty ?? true)
            && (injectedLibraries?.isEmpty ?? true)
            && (systemPreferencesAccess?.isEmpty ?? true)
    }
}

struct JailbreakReport: DetectorReport {

    var value: Bool?

    var evidence = JailbreakReportEvidence()
}
