package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest

internal interface RequestingState {

    fun updateRequestsPending(newStatuses: Map<PermExRequest, PermissionStatus>)

    fun updateRequestsResult(newStatuses: Map<String, Boolean>)

    fun updateRequestPending(request: PermExRequest, status: PermissionStatus)

    fun getRequestsPending(): Map<PermExRequest, PermissionStatus>

    fun getPermissionsResults(): Map<String, Boolean>

}