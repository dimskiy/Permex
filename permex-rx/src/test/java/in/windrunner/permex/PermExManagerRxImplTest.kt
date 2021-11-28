package `in`.windrunner.permex

import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PermExManagerRxImplTest {

    private lateinit var instance: PermExManagerRx

    private val managerWrapper = mock<PermExManager>()

    private val permissionsResultMocked: (Map<String, Boolean>) -> Unit = { resultMocked ->
        val captor = argumentCaptor<(Map<String, Boolean>) -> Unit>()
        whenever(managerWrapper.setResultsListener(captor.capture())).then {
            captor.firstValue(resultMocked)
        }
    }


    @Before
    fun setup() {
        instance = PermExManagerRxImpl(managerWrapper)
    }

    @Test
    fun `request permission`() {
        val request = PermExRequest("test")

        instance.requestPermissions(request)
            .test()

        verify(managerWrapper).requestPermissions(request)
    }

    @Test
    fun `receive results requested`() {
        val request = PermExRequest("test")
        val resultExpected = mapOf(request.nameRequested to true)
        permissionsResultMocked(resultExpected)

        instance.requestPermissions(request)
            .test()

        instance.observeResults()
            .test()
            .assertNotComplete()
            .assertValue(resultExpected)
    }

    @Test
    fun `results requested not being repeated`() {
        val request = PermExRequest("test")
        val resultExpected = mapOf(request.nameRequested to true)
        permissionsResultMocked(resultExpected)

        with(instance) {
            requestPermissions(request)
                .test()

            observeResults()
                .test()
                .assertNotComplete()
                .assertValue(resultExpected)

            observeResults()
                .test()
                .assertNotComplete()
                .assertNoValues()
        }
    }
}