package `in`.windrunner.permex.check

internal enum class PermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED,
    DENIED_NEED_RATIONALE,
    DENIED_RATIONALE_SHOWN,
    DENIED_PERMANENT
}