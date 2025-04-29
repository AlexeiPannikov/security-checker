protocol DetectorReport: Encodable {
    associatedtype Evidence: Encodable
    var value: Bool? { get }
    var evidence: Evidence { get }
}

protocol Detector {
    associatedtype Report: DetectorReport
    func runDetection() -> Report
}

extension DetectorReport {
    var isEmptyEvidence: Bool {
        switch self {
        case let string as String:
            return string.isEmpty
        default:
            return false
        }
    }
}
