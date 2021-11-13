package `in`.windrunner.permex

import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PermExExplanationDelegateTest {

    private lateinit var delegate: PermExExplanationDelegate

    private val confirmationViewStub = mock<(PermExRequest) -> Unit>()
    private val decisionHolder = mock< PermExExplanationDelegate.DecisionHolder>()

    @Before
    fun setup() {
        delegate = object : PermExExplanationDelegate() {
            override fun showConfirmationDialog(permission: PermExRequest) {
                confirmationViewStub(permission)
            }
        }
        delegate.decisionHolder = decisionHolder
    }

    @Test
    fun `call explanation dialog`() {
        val request = PermExRequest("test")

        delegate.showConfirmationDialog(request)

        verify(confirmationViewStub).invoke(request)
    }

    @Test
    fun `report user confirmed request explanation`() {
        val request = PermExRequest("test")

        delegate.confirmPermissionRequest(request)

        verify(decisionHolder).onUserConfirmed(request)
    }

    @Test
    fun `report user declined request explanation`() {
        val request = PermExRequest("test")

        delegate.declinePermissionRequest(request)

        verify(decisionHolder).onUserDeclined(request)
    }
}