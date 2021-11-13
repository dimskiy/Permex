package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CheckPresenterTest {

    private lateinit var presenter: CheckPresenter
    private val resultsHolder = mock<ResultsHolder>()
    private val requestingState = RequestingStateImpl(emptyList())
    private val view = mock<CheckView>()

    @Before
    fun setup() {
        presenter = CheckPresenter(resultsHolder, requestingState)
    }

    @Test
    fun `finish checking WHEN all permissions granted`() {
        initRequestingState(
            PermExRequest("test1") to PermissionStatus.UNKNOWN,
            PermExRequest("test2") to PermissionStatus.UNKNOWN
        )
        whenever(view.getPermissionGrantedState(any())).thenReturn(true)

        presenter.onAttach(view)

        Assert.assertEquals(
            mapOf(
                "test1" to true,
                "test2" to true
            ),
            requestingState.getPermissionsResults()
        )
        verify(resultsHolder).onRequestingCompleted()
    }

    @Test
    fun `report results WHEN some denied`() {
        val permissionDenied = PermExRequest("test1")
        val permissionGranted = PermExRequest("test2")
        initRequestingState(
            permissionDenied to PermissionStatus.UNKNOWN,
            permissionGranted to PermissionStatus.UNKNOWN
        )

        whenever(view.getPermissionGrantedState(permissionDenied)).thenReturn(false)
        whenever(view.getPermissionGrantedState(permissionGranted)).thenReturn(true)

        whenever(view.callSystemApprove(setOf(permissionDenied.nameRequested))).then {
            presenter.onPermissionsRequestingResults(
                mapOf(permissionDenied.nameRequested to true)
            )
        }

        presenter.onAttach(view)

        Assert.assertEquals(
            mapOf(
                "test1" to true,
                "test2" to true
            ),
            requestingState.getPermissionsResults()
        )
        verify(resultsHolder).onRequestingCompleted()
    }

    @Test
    fun `report results WHEN all denied`() {
        val permissionDenied = PermExRequest("test1")
        initRequestingState(permissionDenied to PermissionStatus.UNKNOWN)

        whenever(view.getPermissionGrantedState(permissionDenied)).thenReturn(false)
        whenever(view.callSystemApprove(setOf(permissionDenied.nameRequested))).then {
            presenter.onPermissionsRequestingResults(
                mapOf(permissionDenied.nameRequested to false)
            )
        }

        presenter.onAttach(view)

        Assert.assertEquals(
            mapOf(
                permissionDenied.nameRequested to false
            ),
            requestingState.getPermissionsResults()
        )

        verify(resultsHolder).onRequestingCompleted()
    }

    @Test
    fun `request WHEN all permissions denied`() {
        initRequestingState(
            PermExRequest("test1") to PermissionStatus.UNKNOWN,
            PermExRequest("test2") to PermissionStatus.UNKNOWN
        )
        whenever(view.getPermissionGrantedState(any())).thenReturn(false)

        presenter.onAttach(view)

        verify(view).callSystemApprove(
            setOf(
                "test1",
                "test2"
            )
        )
    }

    @Test
    fun `request WHEN some permissions denied`() {
        initRequestingState(
            PermExRequest("test1") to PermissionStatus.UNKNOWN,
            PermExRequest("test2") to PermissionStatus.UNKNOWN
        )
        whenever(view.getPermissionGrantedState(PermExRequest("test1")))
            .thenReturn(true)
        whenever(view.getPermissionGrantedState(PermExRequest("test2")))
            .thenReturn(false)

        presenter.onAttach(view)

        verify(view).callSystemApprove(
            setOf(
                "test2"
            )
        )
    }

    @Test
    fun `request user WHEN permissions require explanation`() {
        val request = PermExRequest("test1")
        initRequestingState(request to PermissionStatus.UNKNOWN)

        whenever(view.getPermissionGrantedState(request)).thenReturn(false)
        whenever(view.getPermissionRationaleRequired(request)).thenReturn(true)

        presenter.onAttach(view)

        verify(view).callUserConfirmation(request)
    }

    @Test
    fun `request sys approve WHEN user confirmed explanation`() {
        val request = PermExRequest("test1")
        initRequestingState(request to PermissionStatus.DENIED_RATIONALE_SHOWN)

        presenter.onAttach(view)

        verify(view).callSystemApprove(
            setOf(request.nameRequested)
        )
    }

    @Test
    fun `skip WHEN user not confirmed explanation`() {
        val request = PermExRequest("test1")
        initRequestingState(request to PermissionStatus.DENIED_PERMANENT)

        presenter.onAttach(view)

        verify(view, never()).callSystemApprove(any())
    }

    @Test
    fun `continue WHEN explanations declined`() {
        val requestExplained = PermExRequest("test1")
        val requestUnattended = PermExRequest("test2")
        initRequestingState(
            requestExplained to PermissionStatus.DENIED_PERMANENT,
            requestUnattended to PermissionStatus.DENIED
        )

        presenter.onAttach(view)

        verify(view).callSystemApprove(setOf(requestUnattended.nameRequested))
    }

    @Test
    fun `continue WHEN explanations confirmed`() {
        val requestExplained = PermExRequest("test1")
        val requestUnattended = PermExRequest("test2")
        initRequestingState(
            requestExplained to PermissionStatus.DENIED_RATIONALE_SHOWN,
            requestUnattended to PermissionStatus.DENIED
        )

        presenter.onAttach(view)

        verify(view).callSystemApprove(
            setOf(
                requestExplained.nameRequested,
                requestUnattended.nameRequested
            )
        )
    }

    private fun initRequestingState(vararg permissions: Pair<PermExRequest, PermissionStatus>) {
        requestingState.updateRequestsPending(permissions.toMap())
    }
}