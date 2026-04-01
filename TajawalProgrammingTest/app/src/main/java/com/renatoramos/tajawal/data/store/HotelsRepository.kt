package com.renatoramos.tajawal.data.store

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.store.local.HotelProvider
import com.renatoramos.tajawal.data.store.remote.network.NetworkService
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


open class HotelsRepository @Inject constructor(private val networkService: NetworkService,
                                                private val hotelProvider: HotelProvider) {

    open fun getHotelList(): Maybe<List<HotelModel>> {
        val remote = getHotelListRemote()
                .onErrorResumeNext(Observable.empty())
        val local = getHotelListLocal()

        return Observable.concatArray(remote, local)
                .filter { list -> list.isNotEmpty() }
                .firstElement()
    }

    open fun getHotelById(hotelId: Int?): Maybe<HotelModel> {
        val local = getHotelByIdLocal(hotelId)

        return Observable.concatArray(local)
                .firstElement()
    }

    private fun getHotelListLocal(): Observable<List<HotelModel>> {
        return hotelProvider.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getHotelByIdLocal(hotelId: Int?): Observable<HotelModel> {
        return hotelProvider.getById(hotelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    private fun getHotelListRemote(): Observable<List<HotelModel>> {
        return networkService.getHotels()
                .subscribeOn(Schedulers.io())
                .map { hotels -> hotels.hotel.orEmpty() }
                .concatMap { hotels ->
                    if (hotels.isEmpty()) {
                        Observable.just(hotels)
                    } else {
                        hotelProvider.add(hotels)
                                .onErrorReturnItem(hotels)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }
}
