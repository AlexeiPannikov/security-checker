import ExpoModulesCore

public class SecurityCheckerModule: Module, VulnerabilitiesDetectorDelegate {
    
    let vulnerabilitiesDetector = VulnerabilitiesDetector()
    
    
    func onChange(report: VulnerabilitiesReport) {
        do {
            let encoded = try report.toDictionary()
            self.sendEvent("onChange", encoded)
        } catch {
            print("Error encoding report: \(error)")
        }
    }
    
    
    public func definition() -> ModuleDefinition {
        Name("SecurityChecker")
        
        OnCreate {
            self.vulnerabilitiesDetector.delegate = self
        }
        
        Events("onChange")
        
        Function("check") {
            let report = vulnerabilitiesDetector.runDetection()

            do {
                return try report.toDictionary()
            } catch {
                throw Exception(name: "EncodingError", description: error.localizedDescription)
            }
        }
    }
}

