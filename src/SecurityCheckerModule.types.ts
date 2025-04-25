type BaseReport<Evidence extends Partial<Record<string, string[]>>> = {
    value?: boolean
    evidence: Evidence
}

type JailbreakEvidence = {
    suspiciousFiles: string[]
    sandboxEscape: string[]
    symbolicLinks: string[]
    injectedLibraries: string[]
    systemPreferencesAccess: string[]
}

type DebuggerEvidence = {
    debuggerCheckError: string[]
    debuggerDetected: string[]
}

type EmulatorEvidence = {
    emulatorDetected: string[]
}

export type SecurityCheckReport = {
    isSecure: boolean
    jailbreak: BaseReport<JailbreakEvidence>
    debugger: BaseReport<DebuggerEvidence>
    emulator: BaseReport<EmulatorEvidence>
}