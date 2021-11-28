package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest

internal interface RequestingState {

    fun addNewRequests(requests: Array<out PermExRequest>)

    fun updateRequestsResult(newStatuses: Map<String, Boolean>)

    fun updateRequestsPending(newStatuses: Map<PermExRequest, PermissionStatus>)

    fun updateRequestPending(request: PermExRequest, status: PermissionStatus)

    fun getRequestsPending(): Map<PermExRequest, PermissionStatus>

    fun getPermissionsResultsAndClear(): Map<String, Boolean>

}