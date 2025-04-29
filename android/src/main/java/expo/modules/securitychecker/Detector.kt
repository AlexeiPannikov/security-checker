package expo.modules.securitychecker

interface Detector<ReportType : DetectorReport<*>> {
    fun runDetection(): ReportType
}