package `in`.windrunner.permex.tools

import androidx.annotation.CallSuper
import java.lang.ref.WeakReference

internal interface BasePresenterView

internal open class BasePresenter<T : BasePresenterView> {

    private var viewRef: WeakReference<T> = WeakReference(null)
    val view: T
        get() = viewRef.get()
            ?: throw IllegalStateException("View was not attached to the Presenter")

    @CallSuper
    open fun onAttach(view: T) {
        viewRef = WeakReference(view)
    }

    @CallSuper
    open fun onDetach() {
        viewRef.clear()
    }

}