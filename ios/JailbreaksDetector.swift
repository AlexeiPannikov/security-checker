struct JailbreakReportEvidence: Encodable {
    var suspiciousFiles: [String]?
    var sandboxEscape: [String]?
    var symbolicLinks: [String]?
    var injectedLibraries: [String]?
    var systemPreferencesAccess: [String]?
    
    var isEmpty: Bool {
            (suspiciousFiles?.isEmpty ?? true) &&
            (sandboxEscape?.isEmpty ?? true) &&
            (symbolicLinks?.isEmpty ?? true) &&
            (injectedLibraries?.isEmpty ?? true) &&
            (systemPreferencesAccess?.isEmpty ?? true)
    }
}

struct JailbreakReport: DetectorReport {
    
    var value: Bool?
    
    var evidence = JailbreakReportEvidence()
}

class JailbreaksDetector: Detector {
    func runDetection() -> JailbreakReport {
        var evidence = JailbreakReportEvidence()
        
        // 1. Known jailbreak files
        let knownPaths = [
            "/Applications/Cydia.app",
            "/Library/MobileSubstrate/MobileSubstrate.dylib",
            "/bin/bash",
            "/usr/sbin/sshd",
            "/etc/apt",
            "/private/var/lib/apt/"
        ]
        
        let foundFiles = knownPaths.filter { FileManager.default.fileExists(atPath: $0) }
        if !foundFiles.isEmpty {
            evidence.suspiciousFiles = foundFiles
        }

        // 2. Attempt to write outside sandbox
        let testPath = "/private/jb_test.txt"
        do {
            try "test".write(toFile: testPath, atomically: true, encoding: .utf8)
            try FileManager.default.removeItem(atPath: testPath)
            evidence.sandboxEscape = ["Writable: \(testPath)"]
        } catch {
            // expected
        }

        // 3. Symbolic links
        let symlinkTargets = ["/Applications", "/usr/libexec"]
        let foundSymlinks = symlinkTargets.compactMap { path -> String? in
            if let attrs = try? FileManager.default.attributesOfItem(atPath: path),
               let type = attrs[.type] as? FileAttributeType,
               type == .typeSymbolicLink {
                return path
            }
            return nil
        }
        if !foundSymlinks.isEmpty {
            evidence.symbolicLinks = foundSymlinks
        }

        // 4. Environment variables
        let envVars = ["DYLD_INSERT_LIBRARIES", "LD_PRELOAD"]
        let foundVars = envVars.compactMap { key -> String? in
            if let val = getenv(key), !String(cString: val).isEmpty {
                return "\(key)=\(String(cString: val))"
            }
            return nil
        }
        if !foundVars.isEmpty {
            evidence.injectedLibraries = foundVars
        }

        // 5. System preference plist access
        let plistPath = "/User/Library/Preferences/com.apple.springboard.plist"
        if FileManager.default.fileExists(atPath: plistPath) {
            evidence.systemPreferencesAccess = [plistPath]
        }

        return JailbreakReport(value: !evidence.isEmpty, evidence: evidence)
    }
}
