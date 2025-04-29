import {PlatformOSType} from "react-native";

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

type RootEvidence = {
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

interface SecurityCheckReportBase {
    isSecure: boolean
    debugger: BaseReport<DebuggerEvidence>
    emulator: BaseReport<EmulatorEvidence>
}

interface SecurityCheckReportIOS extends  SecurityCheckReportBase {
    jailbreak: BaseReport<JailbreakEvidence>
}

interface SecurityCheckReportAndroid extends SecurityCheckReportBase {
    root: BaseReport<RootEvidence>
}

export type SecurityCheckReport<OS extends PlatformOSType | unknown = unknown> = OS extends 'ios' ? SecurityCheckReportIOS : OS extends 'android' ? SecurityCheckReportAndroid : SecurityCheckReportBase