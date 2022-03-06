package `in`.windrunner.permex.manager

import `in`.windrunner.permex.PermExExplanationDelegate
import `in`.windrunner.permex.PermExManager
import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.check.*
import `in`.windrunner.permex.tools.ServiceLocator
import android.content.Context
import androidx.annotation.VisibleForTesting

internal class PermExManagerImpl(
    context: Context,
    private val explanationDelegate: PermExExplanationDelegate,
    private val onStartChecking: (Context) -> Unit
) : ResultsHolder(), PermExManager, PermExExplanationDelegate.DecisionHolder {

    init {
        explanationDelegate.decisionHolder = this
    }

    private val appContext: Context = context.applicationContext

    @Volatile
    private var resultsListener: (Map<String, Boolean>) -> Unit = {}

    @VisibleForTesting
    val requestingState: RequestingState by lazy { RequestingStateImpl() }
    private val checkingPresenter: CheckPresenter by lazy { CheckPresenter(this, requestingState) }

    override fun onRequestingCompleted() {
        resultsListener(requestingState.getPermissionsResultsAndClear())
    }

    override fun onUserConfirmed(request: PermExRequest) {
        requestingState.updateRequestPending(
            request = request,
            status = PermissionStatus.DENIED_USER_CONFIRMED
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
        onStartChecking(appContext)
    }

    override fun setResultsListener(resultsListener: (Map<String, Boolean>) -> Unit): PermExManager {
        this.resultsListener = resultsListener
        return this
    }

    override fun requestPermissions(vararg permissions: PermExRequest) {
        requestingState.addNewRequests(permissions)
        ServiceLocator.initLocator(checkingPresenter, explanationDelegate)
        onStartChecking(appContext)
    }
}