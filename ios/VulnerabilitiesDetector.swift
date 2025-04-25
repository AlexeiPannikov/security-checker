struct VulnerabilitiesReport: Encodable {
    var jailbreak: JailbreakReport?
    var debugger: DebuggerReport?
    var emulator: EmulatorReport?
    
    var isSecure: Bool {
        return !(jailbreak?.value ?? false || debugger?.value ?? false)
    }
    
    enum CodingKeys: String, CodingKey {
        case isSecure
        case jailbreak
        case debugger
        case emulator
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(isSecure, forKey: .isSecure)

        if let jailbreak = jailbreak {
            try container.encode(jailbreak, forKey: .jailbreak)
        }

        if let debugger = debugger {
            try container.encode(debugger, forKey: .debugger)
        }
        
        if let emulator = emulator {
            try container.encode(emulator, forKey: .emulator)
        }
    }
}

class VulnerabilitiesDetector: DebuggerDetectorDelegate {
    
    private let debuggerDetector = DebuggerDetector()
    private let jailbreaksDetector = JailbreaksDetector()
    private let emulatorDetector = EmulatorDetector()
    
    private var report: VulnerabilitiesReport?
    
    var delegate: VulnerabilitiesDetectorDelegate?
    
    func debuggerWasAttached(report: DebuggerReport) {
        if self.report == nil {
            self.report = VulnerabilitiesReport(debugger: report)
        } else {
            self.report?.debugger = report
        }
        delegate?.onChange(report: self.report!)
    }
    
    func runDetection() -> VulnerabilitiesReport {
        
        debuggerDetector.delegate = self
        
        let jailbrakeReport = jailbreaksDetector.runDetection()
        let debuggerReport = debuggerDetector.runDetection()
        let emulatorReport = emulatorDetector.runDetection()
        
        report = VulnerabilitiesReport(jailbreak: jailbrakeReport, debugger: debuggerReport, emulator: emulatorReport)
        
        debuggerDetector.startMonitoring()
        
        return report!
    }
}

protocol VulnerabilitiesDetectorDelegate {
    func onChange(report: VulnerabilitiesReport) -> Void
}


extension Encodable {
    func toDictionary() throws -> [String: Any] {
        let data = try JSONEncoder().encode(self)
        let jsonObject = try JSONSerialization.jsonObject(with: data, options: [])
        guard let dict = jsonObject as? [String: Any] else {
            throw NSError(domain: "toDictionary", code: 0, userInfo: [
                NSLocalizedDescriptionKey: "Failed to convert to dictionary"
            ])
        }
        return dict
    }
}
