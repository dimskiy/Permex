package `in`.windrunner.permex

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface PermExManagerRx {

    /**
     * Allows to make the new permissions request session using the set of @link PermExRequest.
     * Results will be returned either in Observable @link PermExManagerRx#observeResults,
     * or @link PermExManagerRx#observeResultsFor (with requests filter applied).
     */
    fun requestPermissions(vararg permissions: PermExRequest): Completable

    /**
     * This Observable allows your app to get all permissions result after any call
     * to @link PermExManagerRx#requestPermissions. You can get the results for only specific
     * requests by using @link PermExManagerRx#observeResultsFor extension.
     */
    fun observeResults(): Observable<Map<String, Boolean>>

    companion object {
        /**
         * Creates PermExManager reactive instance. You can use this instance within the app lifecycle
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
        ): PermExManagerRx = PermExManagerRxImpl(
            PermExManager.create(context, explanationDelegate)
        )
    }
}