package `in`.windrunner.permex.tools

import `in`.windrunner.permex.PermExExplanationDelegate
import `in`.windrunner.permex.check.CheckPresenter
import java.lang.ref.WeakReference

//TODO: Get rid of it (pass necessary deps from the manager using respective constructors
internal object ServiceLocator {

    private var _checkPresenter = WeakReference<CheckPresenter>(null)
    val checkPresenter: CheckPresenter
        get() = _checkPresenter.get()
            ?: throw IllegalStateException("ServiceLocator not being initialized")

    private var _explanationDelegate = WeakReference<PermExExplanationDelegate>(null)
    val explanationDelegate: PermExExplanationDelegate
        get() = _explanationDelegate.get()
            ?: throw IllegalStateException("ServiceLocator not being initialized")

    fun initLocator(
        checkPresenter: CheckPresenter,
        explanationDelegate: PermExExplanationDelegate
    ) {
        _checkPresenter = WeakReference(checkPresenter)
        _explanationDelegate = WeakReference(explanationDelegate)
    }

}