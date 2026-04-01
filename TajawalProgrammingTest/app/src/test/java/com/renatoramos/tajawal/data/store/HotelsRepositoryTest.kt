package com.renatoramos.tajawal.data.store

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.model.HotelsModel
import com.renatoramos.tajawal.data.model.ImageModel
import com.renatoramos.tajawal.data.model.LocationModel
import com.renatoramos.tajawal.data.model.SummaryModel
import com.renatoramos.tajawal.data.store.local.HotelProvider
import com.renatoramos.tajawal.data.store.remote.network.NetworkService
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HotelsRepositoryTest {

    private lateinit var repository: HotelsRepository
    private lateinit var hotelModelListMock: List<HotelModel>

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        hotelModelListMock = listOf(
                HotelModel(
                        hotelId = 4021140,
                        image = listOf(ImageModel("https://az712897.vo.msecnd.net/images/full/82524502-8E66-46B1-8B04-82E414894020.jpeg")),
                        location = LocationModel(
                                address = "The Corner 24Th And 9Th Street Near Al Qiyadah Metro Station",
                                latitude = 25.271263,
                                longitude = 55.328996
                        ),
                        summary = SummaryModel(
                                highRate = 3130.41,
                                hotelName = "Al Manar Hotel Apartments",
                                lowRate = 2379.11
                        )
                )
        )
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun `should return cached list when remote request fails`() {
        var getAllCalls = 0

        val networkService = object : NetworkService {
            override fun getHotels(): Observable<HotelsModel> = Observable.error(RuntimeException("offline"))
        }
        val hotelProvider = object : HotelProvider() {
            override fun getAll(): Observable<List<HotelModel>> {
                getAllCalls++
                return Observable.just(hotelModelListMock)
            }
        }
        repository = HotelsRepository(networkService, hotelProvider)

        val testObserver = repository.getHotelList().test()

        testObserver.assertComplete()
        testObserver.assertValue(hotelModelListMock)
        assertEquals(1, getAllCalls)
    }

    @Test
    fun `should persist and return remote list when request succeeds`() {
        var addCalls = 0

        val networkService = object : NetworkService {
            override fun getHotels(): Observable<HotelsModel> = Observable.just(HotelsModel(hotelModelListMock))
        }
        val hotelProvider = object : HotelProvider() {
            override fun add(hotels: List<HotelModel>): Observable<List<HotelModel>> {
                addCalls++
                return Observable.just(hotels)
            }
        }
        repository = HotelsRepository(networkService, hotelProvider)

        val testObserver = repository.getHotelList().test()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        assertEquals(hotelModelListMock, testObserver.values().single())
        assertEquals(1, addCalls)
        assertTrue(testObserver.values().single().isNotEmpty())
    }
}
