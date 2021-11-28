package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.check.PermissionStatus.DENIED_PERMANENT
import `in`.windrunner.permex.check.PermissionStatus.GRANTED
import `in`.windrunner.permex.check.PermissionStatus.UNKNOWN

internal class RequestingStateImpl : RequestingState {

    private var requestsPending = mutableMapOf<PermExRequest, PermissionStatus>()

    private val permissionsResult: MutableMap<String, Boolean> = mutableMapOf()

    override fun addNewRequests(requests: Array<out PermExRequest>) {
        synchronized(requestsPending) {
            requests
                .filterNot { requestsPending.containsKey(it) }
                .associateWith { UNKNOWN }
                .let(requestsPending::putAll)
        }
    }

    override fun updateRequestsPending(newStatuses: Map<PermExRequest, PermissionStatus>) {
        newStatuses.forEach { (request, status) -> updateRequestPending(request, status) }
    }

    override fun updateRequestPending(request: PermExRequest, status: PermissionStatus) {
        when (status) {
            GRANTED, DENIED_PERMANENT -> {
                permissionsResult[request.nameRequested] = (status == GRANTED)
                requestsPending.remove(request)
            }
            else -> requestsPending[request] = status
        }
    }

    override fun updateRequestsResult(newStatuses: Map<String, Boolean>) {
        newStatuses.forEach { (permissionName, isGranted) ->
            permissionsResult[permissionName] = isGranted
            requestsPending
                .firstNotNullOfOrNull {
                    if (it.key.nameRequested == permissionName) it.key else null
                }
                ?.let(requestsPending::remove)
        }
    }

    override fun getRequestsPending(): Map<PermExRequest, PermissionStatus> = requestsPending

    override fun getPermissionsResultsAndClear(): Map<String, Boolean> {
        val result = HashMap(permissionsResult)
        permissionsResult.clear()

        return result
    }
}