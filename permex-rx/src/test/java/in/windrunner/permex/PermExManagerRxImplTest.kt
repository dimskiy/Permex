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

            requestPermissions(request)
                .test()

            observeResults()
                .test()
                .assertNotComplete()
                .assertValueCount(1)
                .assertValue(resultExpected)
        }
    }

    @Test
    fun `get specific results`() {
        val request1 = PermExRequest("test")
        val request2 = PermExRequest("test2")

        permissionsResultMocked(
            mapOf(request1.nameRequested to true).plus(
                request2.nameRequested to true
            )
        )

        with(instance) {
            requestPermissions(request1, request2)
                .test()

            getFullResultsFor(listOf(request1))
                .test()
                .assertValue(
                    mapOf(request1.nameRequested to true)
                )
                .assertComplete()
        }
    }

    @Test
    fun `maintain other results WHEN someone get specific results`() {
        val request1 = PermExRequest("test")
        val request2 = PermExRequest("test2")

        permissionsResultMocked(
            mapOf(request1.nameRequested to true).plus(
                request2.nameRequested to true
            )
        )

        with(instance) {
            requestPermissions(request1, request2)
                .test()

            getFullResultsFor(listOf(request1))
                .test()
                .assertValue(
                    mapOf(request1.nameRequested to true)
                )
                .assertComplete()

            observeResults()
                .test()
                .assertNotComplete()
                .assertValue(
                    mapOf(
                        request1.nameRequested to true,
                        request2.nameRequested to true
                    )
                )
        }
    }

    @Test
    fun `get specific results WHEN split onto parts`() {
        val request1 = PermExRequest("test")
        val request2 = PermExRequest("test2")

        val captor = argumentCaptor<(Map<String, Boolean>) -> Unit>()
        whenever(managerWrapper.setResultsListener(captor.capture())).then {
            captor.firstValue(mapOf(request1.nameRequested to true))
            captor.firstValue(mapOf(request2.nameRequested to true))
        }

        instance.requestPermissions(request1, request2)
            .test()

        instance.getFullResultsFor(listOf(request1, request2))
            .test()
            .assertValue(
                mapOf(
                    request1.nameRequested to true,
                    request2.nameRequested to true
                )
            )
            .assertComplete()
    }
}