package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.tools.BasePresenter

internal class CheckPresenter(
    private val resultHolder: ResultsHolder,
    private val requestingState: RequestingState
) : BasePresenter<CheckView>() {

    override fun onAttach(view: CheckView) {
        super.onAttach(view)

        requestingState.getRequestsPending()
            .filterValues { it == PermissionStatus.UNKNOWN }
            .mapValues { (permission, originalStatus) ->
                val currentlyGranted = view.getPermissionGrantedState(permission)

                when {
                    currentlyGranted -> PermissionStatus.GRANTED
                    !currentlyGranted && isExplanationRequired(permission, originalStatus) -> PermissionStatus.DENIED_NEED_RATIONALE
                    else -> PermissionStatus.DENIED
                }
            }
            .let(requestingState::updateRequestsPending)

        if (requestingState.getRequestsPending().isNotEmpty()) {
            startRequestingProcess()
        } else {
            finishPermissionsCheck()
        }
    }

    private fun isExplanationRequired(permission: PermExRequest, originalStatus: PermissionStatus): Boolean =
        originalStatus != PermissionStatus.DENIED_RATIONALE_SHOWN
                && (permission.forceShowExplanation || view.getPermissionRationaleRequired(permission))

    private fun startRequestingProcess() {
        requestingState.getRequestsPending()
            .firstNotNullOfOrNull { (request, state) ->
                if (state == PermissionStatus.DENIED_NEED_RATIONALE) request else null
            }
            ?.let { request ->
                requestingState.updateRequestPending(
                    request = request,
                    status = PermissionStatus.DENIED_RATIONALE_SHOWN
                )
                view.callUserConfirmation(request)
            }
            ?: callPermissionsPendingApprove()
    }

    private fun callPermissionsPendingApprove() {
        requestingState.getRequestsPending()
            .map { (permission, _) -> permission.nameRequested }
            .toSet()
            .takeIf(Set<*>::isNotEmpty)
            ?.let(view::callSystemApprove)
            ?: finishPermissionsCheck()
    }

    fun onPermissionsRequestingResults(permissionsWithStatuses: Map<String, Boolean>) {
        requestingState.updateRequestsResult(permissionsWithStatuses)
        finishPermissionsCheck()
    }

    private fun finishPermissionsCheck() {
        resultHolder.onRequestingCompleted()
        view.cleanUp()
    }

}