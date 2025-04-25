struct DebuggerReportEvidence: Encodable {
 var debuggerCheckError: [String]?
 var debuggerDetected: [String]?
}

struct DebuggerReport: DetectorReport {
    var value: Bool?
    var evidence = DebuggerReportEvidence()
}

class DebuggerDetector: Detector {
    var delegate: DebuggerDetectorDelegate?
    private var lastReport: DebuggerReport?
    private var timer: Timer?
    
    func runDetection() -> DebuggerReport {
        
        var report = DebuggerReport()
        
        var info = kinfo_proc()
        var size = MemoryLayout<kinfo_proc>.stride
        var mib = [CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()]
        
        let result = sysctl(&mib, u_int(mib.count), &info, &size, nil, 0)
        if result != 0 {
            report.value = nil
            report.evidence.debuggerCheckError = ["sysctl failed with result \(result)"]
            return report
        }
        
        if (info.kp_proc.p_flag & P_TRACED) != 0 {
            report.value = true
            report.evidence.debuggerDetected = ["Debugger is attached (sysctl, P_TRACED)"]
        } else {
            report.value = false
        }
        
        return report
    }

    func startMonitoring() {
        stopMonitoring()

        timer = Timer.scheduledTimer(withTimeInterval: 3.0, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            let report = self.runDetection()
            if !(self.lastReport?.value ?? false), report.value == true {
                self.delegate?.debuggerWasAttached(report: report)
            }
            self.lastReport = report
        }
    }

    func stopMonitoring() {
        timer?.invalidate()
        timer = nil
    }
    
    deinit {
        stopMonitoring()
    }
}

protocol DebuggerDetectorDelegate {
    func debuggerWasAttached(report: DebuggerReport) -> Void
}
