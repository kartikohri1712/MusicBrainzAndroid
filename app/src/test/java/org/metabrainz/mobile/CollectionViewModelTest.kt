package org.metabrainz.mobile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.metabrainz.mobile.presentation.features.collection.CollectionViewModel
import org.metabrainz.mobile.util.Resource

class CollectionViewModelTest {

    // FIXME: flaky tests because we use Dispatchers.IO in running the coroutines in production
    // but the test library only provides option to replace Dispatchers.Main. Solution can be to
    // inject the CoroutineScope in the ViewModel.
    @ExperimentalCoroutinesApi
    val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCollectionDetailsViewModel() = testDispatcher.runBlockingTest {
        TODO()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCollectionViewModel() = testDispatcher.runBlockingTest {
        val testCollection = EntityTestUtils.testCollection
        val viewModel = CollectionViewModel(MockCollectionRepository())
        viewModel.mbid.value = EntityTestUtils.testCollectionMBID
//        val resource = LiveDataTestUtil.getOrAwaitValue(viewModel.data)
//        Assert.assertEquals(Resource.Status.SUCCESS, resource!!.status)
//        AssertionUtils.checkCollectionAssertions(testCollection, resource.data!!)
    }

    @ExperimentalCoroutinesApi
    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}