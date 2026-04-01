package com.renatoramos.tajawal.presentation.ui.hotel.detail

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.store.HotelsRepository
import com.renatoramos.tajawal.presentation.base.BasePresenter
import javax.inject.Inject

class DetailsPresenter @Inject constructor(view: DetailsContract.View, private val hotelsRepository: HotelsRepository)
    : BasePresenter<DetailsContract.View>(view), DetailsContract.Presenter {

    private var hotelId: Int? = 0
    private val hotelNotFoundMessage = "Unable to load hotel details."

    override fun onStart() {
        view.receiveIntent()
        view.setToolbar()
        view.addOnClickToolbar()
        view.loadHotelById()
    }

    override fun attachHotelId(hotelId: Int?) {
        this.hotelId = hotelId
    }

    override fun getHotelById() {
        addDisposable(hotelsRepository.getHotelById(hotelId)
                .subscribe(
                        { hotel -> onSuccess(hotel) },
                        { throwable -> onError(throwable) },
                        { onError(IllegalStateException(hotelNotFoundMessage)) }
                ))
    }

    override fun onSuccess(hotelModel: HotelModel?) {
        if (hotelModel == null) {
            onError(IllegalStateException(hotelNotFoundMessage))
            return
        }

        val summary = hotelModel.summary
        val imageUrl = hotelModel.image?.firstOrNull()?.url
        val latitude = hotelModel.location?.latitude
        val longitude = hotelModel.location?.longitude

        view.showToolbarTitle(summary?.hotelName)
        view.showImage(imageUrl)
        if (latitude != null && longitude != null) {
            view.showHotelLocation(latitude, longitude)
            view.showGoogleMap()
        }
        view.showHotelName(summary?.hotelName)
        view.showDiscountPrice(summary?.lowRate?.toString(), summary?.highRate?.toString())
        view.showAddress(hotelModel.location?.address)
        view.addOnClickHotelImage(imageUrl)
    }

    override fun onError(throwable: Throwable) {
        view.showError(throwable.message.orEmpty())
    }
}
