package `in`.windrunner.permex

import `in`.windrunner.permex.check.CheckActivity
import `in`.windrunner.permex.manager.PermExManagerImpl
import android.content.Context

/**
 * @author Vadim Sinitskiy @see https://github.com/dimskiy
 *
 * PermExManager allows to offload permissions checking\requesting logic out of your app. There is
 * also bridge between the library and your app's UI to show rationale dialogs easily with no direct
 * connection between UI context and permissions checking logic:
 *
 * Quick start:
 * 1)Implement @link PermExExplanationDelegate allowing your app to receive
 * rationale dialog requests and report user's decision
 *
 * 2)Create PermExManager instance using @link PermExManager#create method.
 * Pass @link PermExExplanationDelegate implementation created before.
 *
 * 3)Add resultsListener using @link PermExManager#setResultsListener. This callback allows your app
 * to get permissions result after any call to @link PermExManager#requestPermissions.
 * The callback being invoked after the full permissions requesting cycle including these steps, as folows:
 * -Check permissions requested with Android system.
 * -Ask user for extra allowance using rationale dialogs if required either by the Android system or by
 * @link PermExRequest#forceShowExplanation.
 * -Placing permissions requests to the Android system
 * -Report the results of the requests placed to your app using @link PermExManager#setResultsListener
 *
 * @see `in`.windrunner.permex.PermExExplanationDelegate
 */
interface PermExManager {

    /**
     * Add resultsListener using @link PermExManager#setResultsListener. This callback allows your app
     * to get permissions result after any call to @link PermExManager#requestPermissions.
     */
    fun setResultsListener(resultsListener: (Map<String, Boolean>) -> Unit): PermExManager

    /**
     * Allows to make the new permissions request session using the set of @link PermExRequest.
     * Results will be returned using callback set with @link PermExManager#setResultsListener
     */
    fun requestPermissions(vararg permissions: PermExRequest)

    companion object {
        /**
         * Creates PermExManager instance. You can use this instance within the app lifecycle
         * for multiple permissions requests.
         *
         * @param context - use any @see android.content.Context instance. PermExManager will
         * explicitly call @see android.content.Context.getApplicationContext() to avoid memory
         * leaks.
         *
         * @param explanationDelegate - implementation of @link PermExExplanationDelegate
         * that allows to link permissions requesting logic with app's UI. This delegate allows showing
         * extra explanation when needed, as well as report the results to the PermExManager.
         * */
        fun create(
            context: Context,
            explanationDelegate: PermExExplanationDelegate
        ): PermExManager = PermExManagerImpl(
            context = context.applicationContext,
            explanationDelegate = explanationDelegate,
            onStartChecking = CheckActivity::checkAndRequestPermissions
        )
    }
}