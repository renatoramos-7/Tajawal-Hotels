package com.renatoramos.tajawal.hotel.list

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.model.HotelsModel
import com.renatoramos.tajawal.data.model.ImageModel
import com.renatoramos.tajawal.data.model.LocationModel
import com.renatoramos.tajawal.data.model.SummaryModel
import com.renatoramos.tajawal.data.store.HotelsRepository
import com.renatoramos.tajawal.data.store.local.HotelProvider
import com.renatoramos.tajawal.data.store.remote.network.NetworkService
import com.renatoramos.tajawal.presentation.ui.hotel.list.HotelListContract
import com.renatoramos.tajawal.presentation.ui.hotel.list.HotelListPresenter
import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HotelListPresenterTest {

    private lateinit var presenter: HotelListPresenter
    private lateinit var view: FakeHotelListView
    private lateinit var hotelsRepository: FakeHotelsRepository

    private lateinit var hotelModelMock: HotelModel
    private lateinit var hotelModelListMock: List<HotelModel>
    private lateinit var throwableMock: Throwable

    @Before
    fun setUp() {
        view = FakeHotelListView()
        hotelsRepository = FakeHotelsRepository()
        presenter = HotelListPresenter(view, hotelsRepository)

        hotelModelMock = HotelModel(
                hotelId = 4021140,
                image = listOf(ImageModel("https://az712897.vo.msecnd.net/images/full/82524502-8E66-46B1-8B04-82E414894020.jpeg")),
                location = LocationModel(
                        address = "The Corner 24Th And 9Th Street Near Al Qiyadah Metro Station",
                        latitude = 25.271263,
                        longitude = 55.328996
                ),
                summary = SummaryModel(highRate = 3130.41, hotelName = "Al Manar Hotel Apartments", lowRate = 2379.11)
        )

        throwableMock = Throwable("Failed to load hotel list")
        hotelModelListMock = listOf(hotelModelMock)
    }

    @Test
    fun `OnStart`() {
        presenter.onStart()

        assertEquals(1, view.setupRecyclerViewCalls)
        assertEquals(1, view.setToolbarCalls)
        assertEquals(1, view.loadHotelListCalls)
    }

    @Test
    fun `should get list when internet is online`() {
        hotelsRepository.hotelListResult = Maybe.just(hotelModelListMock)

        presenter.getHotelList()

        assertEquals(1, view.showProgressBarCalls)
        assertEquals(1, view.hideProgressBarCalls)
        assertEquals(1, hotelsRepository.getHotelListCalls)
        assertEquals(hotelModelListMock, view.adapterHotels)
        assertEquals(1, view.showAdapterCalls)
    }

    @Test
    fun `should show fallback error when hotel list is empty`() {
        hotelsRepository.hotelListResult = Maybe.empty()

        presenter.getHotelList()

        assertEquals(1, view.showProgressBarCalls)
        assertEquals(1, view.hideProgressBarCalls)
        assertEquals("Unable to load hotel list.", view.errorMessage)
    }

    @Test
    fun `onSuccess`() {
        presenter.onSuccess(hotelModelListMock)

        assertEquals(hotelModelListMock, view.adapterHotels)
        assertEquals(1, view.showAdapterCalls)
        assertEquals(1, view.hideProgressBarCalls)
    }

    @Test
    fun `should open Details Screen in onItemClick`() {
        presenter.onSuccess(hotelModelListMock)
        presenter.onItemClick(0)

        assertEquals(hotelModelMock.hotelId, view.openDetailsHotelId)
    }

    @Test
    fun `onError`() {
        presenter.onError(throwableMock)

        assertEquals(throwableMock.message.orEmpty(), view.errorMessage)
        assertEquals(1, view.hideProgressBarCalls)
    }

    @Test
    fun `should ignore invalid item click positions`() {
        presenter.onSuccess(hotelModelListMock)
        presenter.onItemClick(10)

        assertNull(view.openDetailsHotelId)
    }

    @Test
    fun `OnStop`() {
        presenter.onStop()

        assertTrue(true)
    }

    private class FakeHotelListView : HotelListContract.View {
        var setupRecyclerViewCalls = 0
        var setToolbarCalls = 0
        var loadHotelListCalls = 0
        var showProgressBarCalls = 0
        var hideProgressBarCalls = 0
        var showAdapterCalls = 0
        var adapterHotels: List<HotelModel>? = null
        var errorMessage: String? = null
        var openDetailsHotelId: Int? = null

        override fun createAdapter(hotelModelList: List<HotelModel>) {
            adapterHotels = hotelModelList
        }

        override fun showAdapter() {
            showAdapterCalls++
        }

        override fun showError(error: String) {
            errorMessage = error
        }

        override fun showProgressBar() {
            showProgressBarCalls++
        }

        override fun hideProgressBar() {
            hideProgressBarCalls++
        }

        override fun openDetails(hotelId: Int) {
            openDetailsHotelId = hotelId
        }

        override fun setupRecyclerView() {
            setupRecyclerViewCalls++
        }

        override fun loadHotelList() {
            loadHotelListCalls++
        }

        override fun setToolbar() {
            setToolbarCalls++
        }
    }

    private class FakeHotelsRepository : HotelsRepository(FakeNetworkService(), HotelProvider()) {
        var hotelListResult: Maybe<List<HotelModel>> = Maybe.empty()
        var getHotelListCalls = 0

        override fun getHotelList(): Maybe<List<HotelModel>> {
            getHotelListCalls++
            return hotelListResult
        }
    }

    private class FakeNetworkService : NetworkService {
        override fun getHotels(): Observable<HotelsModel> = Observable.empty()
    }
}
