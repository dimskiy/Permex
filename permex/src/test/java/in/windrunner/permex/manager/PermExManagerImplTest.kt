package `in`.windrunner.permex.manager

import `in`.windrunner.permex.PermExExplanationDelegate
import `in`.windrunner.permex.PermExRequest
import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PermExManagerImplTest {

    private lateinit var manager: PermExManagerImpl

    private val context = mock<Context>()
    private val resultsCallback = mock<(Map<String, Boolean>) -> Unit>()
    private val explanationDelegate = mock<PermExExplanationDelegate>()
    private val permissionsChecker = mock<(Context) -> Unit>()

    @Before
    fun setup() {
        val contextProxy = mock<Context>().apply {
            whenever(this.applicationContext).thenReturn(context)
        }

        manager = PermExManagerImpl(
            context = contextProxy,
            explanationDelegate = explanationDelegate,
            permissionsChecker = permissionsChecker
        )
    }

    @Test
    fun `link with decision holder`() {
        Assert.assertSame(manager, explanationDelegate.decisionHolder)
    }

    @Test
    fun `request permission`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)

        verify(permissionsChecker).invoke(context)
    }

    @Test
    fun `report requesting completed WHEN no permissions processed`() {
        with(manager){
            requestPermissions(PermExRequest("test"))
            setResultsListener(resultsCallback)
            onRequestingCompleted()
        }

        verify(resultsCallback).invoke(emptyMap())
    }

    @Test
    fun `continue WHEN explanation confirmed`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)
        manager.onUserConfirmed(request)

        verify(permissionsChecker, times(2)).invoke(context)
    }

    @Test
    fun `continue WHEN explanation not confirmed`() {
        val request = PermExRequest("test")

        manager.requestPermissions(request)
        manager.onUserDeclined(request)

        verify(permissionsChecker, times(2)).invoke(context)
    }
}