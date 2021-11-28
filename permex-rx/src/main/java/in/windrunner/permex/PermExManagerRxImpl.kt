package `in`.windrunner.permex

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.UnicastSubject

/**
 * This Observable allows your to get permissions result after any call
 * to @link PermExManagerRx#requestPermissions filtered with requests param.
 * @param requests - requests applied as the filter to the results flow.
 */
fun PermExManagerRx.observeResultsFor(requests: Collection<PermExRequest>): Observable<Map<String, Boolean>> =
    observeResults().filter { result ->
        requests.all {
            result.contains(it.nameRequested)
        }
    }

internal class PermExManagerRxImpl(private val managerWrapped: PermExManager) : PermExManagerRx {
    private val resultsCommonSubject = UnicastSubject.create<Map<String, Boolean>>()

    override fun requestPermissions(vararg permissions: PermExRequest): Completable =
        Completable.fromAction {
            managerWrapped.setResultsListener(resultsCommonSubject::onNext)
            managerWrapped.requestPermissions(*permissions)
        }

    override fun observeResults(): Observable<Map<String, Boolean>> = resultsCommonSubject
}