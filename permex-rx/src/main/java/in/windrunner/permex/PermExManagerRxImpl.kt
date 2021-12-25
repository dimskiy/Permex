package `in`.windrunner.permex

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * Allows you to get permissions result for the particular requests list. Note that if not all
 * of permissions requested being received from 'observeResults()' yet, the Single will not complete
 * till the rest of results being received.
 * @param requests - requests applied as the filter to the results flow.
 */
fun PermExManagerRx.getFullResultsFor(requests: Collection<PermExRequest>): Single<Map<String, Boolean>> =
    observeResults()
        .getThoseContainsIn(requests)
        .scan { previous, newItems -> previous.plus(newItems) }
        .filter { result ->
            requests.all {
                result.contains(it.nameRequested)
            }
        }
        .firstOrError()

private fun Observable<Map<String, Boolean>>.getThoseContainsIn(
    requests: Collection<PermExRequest>
): Observable<Map<String, Boolean>> = map { result ->
    result.filterKeys { name ->
        requests.any { it.nameRequested == name }
    }
}

internal class PermExManagerRxImpl(private val managerWrapped: PermExManager) : PermExManagerRx {
    private val resultsCommonSubject = BehaviorSubject.create<Map<String, Boolean>>()

    override fun requestPermissions(vararg permissions: PermExRequest): Completable =
        Completable.fromAction {
            managerWrapped.setResultsListener(this::updateResults)
            managerWrapped.requestPermissions(*permissions)
        }

    private fun updateResults(newItems: Map<String, Boolean>) {
        val accumulatedValue = resultsCommonSubject.value
            ?.plus(newItems)
            ?: newItems

        resultsCommonSubject.onNext(accumulatedValue)
    }

    override fun observeResults(): Observable<Map<String, Boolean>> = resultsCommonSubject
}