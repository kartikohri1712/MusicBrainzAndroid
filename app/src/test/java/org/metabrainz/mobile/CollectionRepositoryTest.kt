package org.metabrainz.mobile

import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.metabrainz.mobile.EntityTestUtils.loadResourceAsString
import org.metabrainz.mobile.EntityTestUtils.testCollection
import org.metabrainz.mobile.EntityTestUtils.testCollectionDetails
import org.metabrainz.mobile.RetrofitUtils.createTestService
import org.metabrainz.mobile.data.repository.CollectionRepository
import org.metabrainz.mobile.data.repository.CollectionRepositoryImpl
import org.metabrainz.mobile.data.sources.api.CollectionService
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.presentation.features.adapters.ResultItem
import org.metabrainz.mobile.util.Resource

class CollectionRepositoryTest {

    // FIXME: Replace runBlockingTest with runBlocking for the time being to pass the tests
    // This is a bug in the kotlinx-coroutines-test library. This should not be necessary once the
    // bug is fixed upstream.
    private lateinit var webServer: MockWebServer
    private lateinit var repository: CollectionRepository

    @Before
    fun setup() {
        webServer = MockWebServer()
        webServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val endpoint = request.path?.substring(1, request.path!!.indexOf('/', 1))
                val file = endpoint + "_public.json"
                return MockResponse().setResponseCode(200).setBody(loadResourceAsString(file))
            }
        }
        webServer.start()
        val service = createTestService(CollectionService::class.java, webServer.url("/"))
        repository = CollectionRepositoryImpl(service)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchCollectionDetails() = runBlockingTest {
        val expected = testCollectionDetails
        val resource = repository.fetchCollectionDetails("release","a691377c-6949-44cb-8a10-9696178cca18")
        Assert.assertEquals(Resource.Status.SUCCESS, resource.status)
        val collectionDetails = Gson().fromJson(resource.data, ResultItem::class.java)
        AssertionUtils.checkCollectionDetailsAssertions(expected, collectionDetails)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun fetchCollections() = runBlockingTest {
        val expected = testCollection
        val resource = repository.fetchCollections("akshaaatt",false)
        Assert.assertEquals(Resource.Status.SUCCESS, resource.status)
        val collection = Gson().fromJson(resource.data.toString(), Collection::class.java)
        AssertionUtils.checkCollectionAssertions(expected, collection)
    }

    @After
    fun teardown() {
        webServer.close()
    }
}