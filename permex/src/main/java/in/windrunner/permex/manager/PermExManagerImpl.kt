package `in`.windrunner.permex.manager

import `in`.windrunner.permex.PermExExplanationDelegate
import `in`.windrunner.permex.PermExManager
import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.check.CheckPresenter
import `in`.windrunner.permex.check.PermissionStatus
import `in`.windrunner.permex.check.RequestingState
import `in`.windrunner.permex.check.RequestingStateImpl
import `in`.windrunner.permex.check.ResultsHolder
import `in`.windrunner.permex.tools.ServiceLocator
import android.content.Context

internal class PermExManagerImpl(
    context: Context,
    private val explanationDelegate: PermExExplanationDelegate,
    private val permissionsChecker: (Context) -> Unit
) : ResultsHolder(), PermExManager, PermExExplanationDelegate.DecisionHolder {

    init {
        explanationDelegate.decisionHolder = this
    }

    private val appContext: Context = context.applicationContext

    private var resultsListener: (Map<String, Boolean>) -> Unit = {}
    private lateinit var requestingState: RequestingState
    private lateinit var checkingPresenter: CheckPresenter

    override fun onRequestingCompleted() {
        resultsListener(requestingState.getPermissionsResults())
    }

    override fun onUserConfirmed(request: PermExRequest) {
        requestingState.updateRequestPending(
            request = request,
            status = PermissionStatus.DENIED_RATIONALE_SHOWN
        )
        resumePermissionsRequesting()
    }

    override fun onUserDeclined(request: PermExRequest) {
        requestingState.updateRequestPending(
            request = request,
            status = PermissionStatus.DENIED_PERMANENT
        )
        resumePermissionsRequesting()
    }

    private fun resumePermissionsRequesting() {
        permissionsChecker(appContext)
    }

    override fun setResultsListener(resultsListener: (Map<String, Boolean>) -> Unit): PermExManager {
        this.resultsListener = resultsListener
        return this
    }

    override fun requestPermissions(vararg permissions: PermExRequest) {
        requestingState = RequestingStateImpl(permissions.toList())
        checkingPresenter = CheckPresenter(this, requestingState)

        ServiceLocator.initLocator(
            checkPresenter = checkingPresenter,
            explanationDelegate = explanationDelegate
        )
        
        permissionsChecker(appContext)
    }
}