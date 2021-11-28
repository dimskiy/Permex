package `in`.windrunner.permex.manager

import `in`.windrunner.permex.PermExExplanationDelegate
import `in`.windrunner.permex.PermExRequest
import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PermExManagerImplTest {

    private lateinit var manager: PermExManagerImpl

    private val context = mock<Context>()
    private val resultsCallback = mock<(Map<String, Boolean>) -> Unit>()
    private val explanationDelegate = mock<PermExExplanationDelegate>()
    private val startChecking = mock<(Context) -> Unit>()

    @Before
    fun setup() {
        val contextProxy = mock<Context>().apply {
            whenever(this.applicationContext).thenReturn(context)
        }

        manager = PermExManagerImpl(
            context = contextProxy,
            explanationDelegate = explanationDelegate,
            onStartChecking = startChecking
        )
        manager.setResultsListener(resultsCallback)
    }

    @Test
    fun `link with decision holder`() {
        Assert.assertSame(manager, explanationDelegate.decisionHolder)
    }

    @Test
    fun `request permission`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)

        verify(startChecking).invoke(context)
    }

    @Test
    fun `report requesting completed WHEN no permissions processed`() {
        with(manager){
            requestPermissions(PermExRequest("test"))
            onRequestingCompleted()
        }

        verify(resultsCallback).invoke(emptyMap())
    }

    @Test
    fun `continue WHEN explanation confirmed`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)
        manager.onUserConfirmed(request)

        verify(startChecking, times(2)).invoke(context)
    }

    @Test
    fun `continue WHEN explanation not confirmed`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)
        manager.onUserDeclined(request)

        verify(startChecking, times(2)).invoke(context)
    }

    @Test
    fun `combined result WHEN multiple request calls`() {
        val request1 = PermExRequest("test1")
        val request2 = PermExRequest("test2")

        with(manager) {
            requestPermissions(request1)
            requestingState.updateRequestsResult(
                mapOf(request1.nameRequested to true)
            )
            onRequestingCompleted()

            requestPermissions(request2)
            requestingState.updateRequestsResult(
                mapOf(request2.nameRequested to true)
            )
            onRequestingCompleted()
        }

        inOrder(resultsCallback) {
            verify(resultsCallback).invoke(
                mapOf(request1.nameRequested to true)
            )
            verify(resultsCallback).invoke(
                mapOf(request2.nameRequested to true)
            )
        }
    }
}