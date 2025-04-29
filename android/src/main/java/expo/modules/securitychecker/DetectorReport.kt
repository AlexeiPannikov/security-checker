package expo.modules.securitychecker

interface DetectorReport<EvidenceType> {
    val value: Boolean?
    val evidence: EvidenceType
}

fun DetectorReport<*>.isEmptyEvidence(): Boolean {
    return when (val e = this.evidence) {
        is String -> e.isEmpty()
        else -> false
    }
}