package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestingStateImplTest {

    @Test
    fun `initial state`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        assertEquals(
            mapOf(PermExRequest("test1") to PermissionStatus.UNKNOWN),
            state.getRequestsPending()
        )
        assertEquals(
            emptyMap<String, Boolean>(),
            state.getPermissionsResultsAndClear()
        )
    }

    @Test
    fun `state WHEN permission became DENIED`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        state.updateRequestsPending(
            mapOf(
                PermExRequest("test1") to PermissionStatus.DENIED
            )
        )

        assertEquals(
            mapOf(PermExRequest("test1") to PermissionStatus.DENIED),
            state.getRequestsPending()
        )
        assertEquals(
            emptyMap<String, Boolean>(),
            state.getPermissionsResultsAndClear()
        )
    }

    @Test
    fun `state WHEN permission became DENIED_NEED_RATIONALE`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        state.updateRequestsPending(
            mapOf(
                PermExRequest("test1") to PermissionStatus.DENIED_NEED_RATIONALE
            )
        )

        assertEquals(
            mapOf(PermExRequest("test1") to PermissionStatus.DENIED_NEED_RATIONALE),
            state.getRequestsPending()
        )
        assertEquals(
            emptyMap<String, Boolean>(),
            state.getPermissionsResultsAndClear()
        )
    }

    @Test
    fun `state WHEN permission became DENIED_RATIONALE_SHOWN`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        state.updateRequestsPending(
            mapOf(
                PermExRequest("test1") to PermissionStatus.DENIED_RATIONALE_SHOWN
            )
        )

        assertEquals(
            mapOf(PermExRequest("test1") to PermissionStatus.DENIED_RATIONALE_SHOWN),
            state.getRequestsPending()
        )
        assertEquals(
            emptyMap<String, Boolean>(),
            state.getPermissionsResultsAndClear()
        )
    }

    @Test
    fun `state WHEN permission became DENIED_PERMANENT`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        state.updateRequestsPending(
            mapOf(
                PermExRequest("test1") to PermissionStatus.DENIED_PERMANENT
            )
        )

        assertEquals(
            emptyMap<PermExRequest, PermissionStatus>(),
            state.getRequestsPending()
        )
        assertEquals(
            mapOf("test1" to false),
            state.getPermissionsResultsAndClear()
        )
    }

    @Test
    fun `state WHEN permission became GRANTED`() {
        val state = RequestingStateImpl()
        state.addNewRequests(arrayOf(PermExRequest("test1")))

        state.updateRequestsPending(
            mapOf(
                PermExRequest("test1") to PermissionStatus.GRANTED
            )
        )

        assertEquals(
            emptyMap<PermExRequest, PermissionStatus>(),
            state.getRequestsPending()
        )
        assertEquals(
            mapOf("test1" to true),
            state.getPermissionsResultsAndClear()
        )
    }
}