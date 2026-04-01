package com.renatoramos.tajawal.data.store.local

import com.renatoramos.tajawal.data.model.HotelModel
import io.paperdb.Paper
import io.reactivex.Observable


open class HotelProvider {

    private val TAG = HotelProvider::class.java.simpleName

    open fun add(hotels: List<HotelModel>): Observable<List<HotelModel>> {
        return Observable.create<List<HotelModel>> { e ->
            try {

                Paper.book().delete(TAG)
                Paper.book().write(TAG, hotels)
                e.onNext(hotels)
                e.onComplete()
            } catch (exception: Exception) {
                e.onError(exception)
            }
        }
    }

    open fun getAll(): Observable<List<HotelModel>> {
        return Observable.fromCallable {
            Paper.book().read<List<HotelModel>>(TAG).orEmpty()
        }
    }

    open fun getById(id: Int?): Observable<HotelModel> {
        return Observable.defer {
            val hotelList = Paper.book().read<List<HotelModel>>(TAG)

            if (hotelList != null) {
                Observable.fromIterable(hotelList).filter { hotel -> hotel.hotelId == id }
            } else {
                Observable.empty()
            }
        }
    }

    open fun delete(): Observable<Void> {
        return Observable.create { e ->
            try {
                Paper.book().delete(TAG)
                e.onComplete()
            } catch (exception: Exception) {
                e.onError(exception)
            }
        }
    }



}
