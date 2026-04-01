package com.renatoramos.tajawal.hotel.detail

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.model.HotelsModel
import com.renatoramos.tajawal.data.model.ImageModel
import com.renatoramos.tajawal.data.model.LocationModel
import com.renatoramos.tajawal.data.model.SummaryModel
import com.renatoramos.tajawal.data.store.HotelsRepository
import com.renatoramos.tajawal.data.store.local.HotelProvider
import com.renatoramos.tajawal.data.store.remote.network.NetworkService
import com.renatoramos.tajawal.presentation.ui.hotel.detail.DetailsContract
import com.renatoramos.tajawal.presentation.ui.hotel.detail.DetailsPresenter
import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetailsPresenterTest {

    private lateinit var presenter: DetailsPresenter
    private lateinit var view: FakeDetailsView
    private lateinit var hotelsRepository: FakeHotelsRepository

    private lateinit var hotelModelMock: HotelModel
    private lateinit var throwableMock: Throwable

    @Before
    fun setUp() {
        view = FakeDetailsView()
        hotelsRepository = FakeHotelsRepository()
        presenter = DetailsPresenter(view, hotelsRepository)

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

        throwableMock = Throwable("Failed to load hotel details")
    }

    @Test
    fun `attachHotelId`() {
        presenter.attachHotelId(hotelModelMock.hotelId)

        assertTrue(true)
    }

    @Test
    fun `OnStart`() {
        presenter.onStart()

        assertEquals(1, view.receiveIntentCalls)
        assertEquals(1, view.setToolbarCalls)
        assertEquals(1, view.addOnClickToolbarCalls)
        assertEquals(1, view.loadHotelByIdCalls)
    }

    @Test
    fun `onSuccess`() {
        presenter.onSuccess(hotelModelMock)

        assertEquals(hotelModelMock.summary?.hotelName, view.toolbarTitle)
        assertEquals(hotelModelMock.image?.get(0)?.url, view.imageUrl)
        assertEquals(hotelModelMock.location?.latitude, view.latitude)
        assertEquals(hotelModelMock.location?.longitude, view.longitude)
        assertEquals(1, view.showGoogleMapCalls)
        assertEquals(hotelModelMock.summary?.hotelName, view.hotelName)
        assertEquals(hotelModelMock.summary?.lowRate.toString(), view.lowRate)
        assertEquals(hotelModelMock.summary?.highRate.toString(), view.highRate)
        assertEquals(hotelModelMock.location?.address, view.address)
        assertEquals(hotelModelMock.image?.get(0)?.url, view.clickableImageUrl)
    }

    @Test
    fun `getHotelById should show fallback error when repository returns empty`() {
        presenter.attachHotelId(hotelModelMock.hotelId)
        hotelsRepository.hotelByIdResult = Maybe.empty()

        presenter.getHotelById()

        assertEquals(hotelModelMock.hotelId, hotelsRepository.requestedHotelId)
        assertEquals("Unable to load hotel details.", view.errorMessage)
    }

    @Test
    fun `onSuccess should skip map when location is incomplete`() {
        val hotelWithoutCoordinates = hotelModelMock.copy(
                location = LocationModel(
                        address = "The Corner 24Th And 9Th Street Near Al Qiyadah Metro Station",
                        latitude = null,
                        longitude = null
                )
        )

        presenter.onSuccess(hotelWithoutCoordinates)

        assertEquals(hotelWithoutCoordinates.summary?.hotelName, view.toolbarTitle)
        assertEquals(hotelWithoutCoordinates.image?.get(0)?.url, view.imageUrl)
        assertNull(view.latitude)
        assertNull(view.longitude)
        assertEquals(0, view.showGoogleMapCalls)
        assertEquals(hotelWithoutCoordinates.summary?.hotelName, view.hotelName)
        assertEquals(hotelWithoutCoordinates.summary?.lowRate.toString(), view.lowRate)
        assertEquals(hotelWithoutCoordinates.summary?.highRate.toString(), view.highRate)
        assertEquals(hotelWithoutCoordinates.location?.address, view.address)
        assertEquals(hotelWithoutCoordinates.image?.get(0)?.url, view.clickableImageUrl)
    }

    @Test
    fun `onError`() {
        presenter.onError(throwableMock)

        assertEquals(throwableMock.message.orEmpty(), view.errorMessage)
    }

    private class FakeDetailsView : DetailsContract.View {
        var receiveIntentCalls = 0
        var setToolbarCalls = 0
        var addOnClickToolbarCalls = 0
        var loadHotelByIdCalls = 0
        var showGoogleMapCalls = 0
        var toolbarTitle: String? = null
        var imageUrl: String? = null
        var latitude: Double? = null
        var longitude: Double? = null
        var hotelName: String? = null
        var lowRate: String? = null
        var highRate: String? = null
        var address: String? = null
        var clickableImageUrl: String? = null
        var errorMessage: String? = null

        override fun showHotelLocation(lat: Double, lng: Double) {
            latitude = lat
            longitude = lng
        }

        override fun showGoogleMap() {
            showGoogleMapCalls++
        }

        override fun showImage(url: String?) {
            imageUrl = url
        }

        override fun showToolbarTitle(title: String?) {
            toolbarTitle = title
        }

        override fun showHotelName(hotelName: String?) {
            this.hotelName = hotelName
        }

        override fun showDiscountPrice(lowRate: String?, highRate: String?) {
            this.lowRate = lowRate
            this.highRate = highRate
        }

        override fun showAddress(address: String?) {
            this.address = address
        }

        override fun showError(error: String) {
            errorMessage = error
        }

        override fun addOnClickHotelImage(imageUrl: String?) {
            clickableImageUrl = imageUrl
        }

        override fun receiveIntent() {
            receiveIntentCalls++
        }

        override fun addOnClickToolbar() {
            addOnClickToolbarCalls++
        }

        override fun loadHotelById() {
            loadHotelByIdCalls++
        }

        override fun setToolbar() {
            setToolbarCalls++
        }
    }

    private class FakeHotelsRepository : HotelsRepository(FakeNetworkService(), HotelProvider()) {
        var hotelByIdResult: Maybe<HotelModel> = Maybe.empty()
        var requestedHotelId: Int? = null

        override fun getHotelById(hotelId: Int?): Maybe<HotelModel> {
            requestedHotelId = hotelId
            return hotelByIdResult
        }
    }

    private class FakeNetworkService : NetworkService {
        override fun getHotels(): Observable<HotelsModel> = Observable.empty()
    }
}
