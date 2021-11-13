package `in`.windrunner.permex

import java.lang.ref.WeakReference

/**
 * @author Vadim Sinitskiy @see https://github.com/dimskiy
 *
 * Allowing your app to receive rationale dialog requests and report user's decision. You can have
 * the single instance of this delegate within the app lifecycle. The good way of handing rationale
 * dialog requests by UI context is to link your Activity or Fragment with this instance using
 * Android lifecycle callbacks (for example, @link Activity#onCreate).
 */
abstract class PermExExplanationDelegate {

    private var _decisionHolder: WeakReference<DecisionHolder> = WeakReference(null)
    internal var decisionHolder: DecisionHolder
        get() = _decisionHolder.get()
            ?: throw IllegalStateException("Not attached to PermEx manager")
        set(value) {
            _decisionHolder = WeakReference(value)
        }

    /**
     * This method being called by PermEx library every time the particular permission requires
     * extra explanation to the user - either required by the Android system or by
     * @link PermExRequest#forceShowExplanation.
     *
     * @param permission - permission request model.
     *
     * You have to link your explanation dialog with decision callbacks:
     * 'user confirmed' action with @link PermExExplanationDelegate#confirmPermissionRequest
     * 'user declined' action with @link PermExExplanationDelegate#declinePermissionRequest
     */
    abstract fun showConfirmationDialog(permission: PermExRequest)

    /**
     * Call this method if user confirm the particular permission request after getting
     * a rationale delegate.
     *
     * @param request - permission request model received in
     * @link PermExExplanationDelegate#showConfirmationDialog
     */
    fun confirmPermissionRequest(request: PermExRequest) {
        _decisionHolder.get()?.onUserConfirmed(request)
    }

    /**
     * Call this method if user decline the particular permission request after getting
     * a rationale delegate.
     *
     * @param request - permission request model received in
     * @link PermExExplanationDelegate#showConfirmationDialog
     */
    fun declinePermissionRequest(request: PermExRequest) {
        _decisionHolder.get()?.onUserDeclined(request)
    }

    internal interface DecisionHolder {

        fun onUserConfirmed(request: PermExRequest)

        fun onUserDeclined(request: PermExRequest)

    }

}