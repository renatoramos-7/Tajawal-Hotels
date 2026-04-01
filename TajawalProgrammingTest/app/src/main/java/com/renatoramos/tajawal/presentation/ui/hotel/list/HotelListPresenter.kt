package com.renatoramos.tajawal.presentation.ui.hotel.list

import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.data.store.HotelsRepository
import com.renatoramos.tajawal.presentation.base.BasePresenter
import javax.inject.Inject


class HotelListPresenter @Inject constructor(view: HotelListContract.View, private val hotelsRepository: HotelsRepository)
    : BasePresenter<HotelListContract.View>(view), HotelListContract.Presenter {

    private var hotelModelList: List<HotelModel> = listOf()
    private val hotelListUnavailableMessage = "Unable to load hotel list."

    override fun onStart() {
        view.setupRecyclerView()
        view.setToolbar()
        view.loadHotelList()
    }

    override fun getHotelList() {
        view.showProgressBar()
        addDisposable(hotelsRepository
                .getHotelList()
                .subscribe(
                        { list -> onSuccess(list) },
                        { throwable -> onError(throwable) },
                        { onError(IllegalStateException(hotelListUnavailableMessage)) }
                ))
    }

    override fun onSuccess(hotelModelList: List<HotelModel>) {
        this.hotelModelList = hotelModelList
        view.createAdapter(hotelModelList)
        view.showAdapter()
        view.hideProgressBar()
    }

    override fun onError(throwable: Throwable) {
        view.showError(throwable.message.orEmpty())
        view.hideProgressBar()
    }

    override fun onItemClick(position: Int) {
        val hotelModel = hotelModelList.getOrNull(position) ?: return
        val hotelId = hotelModel.hotelId ?: return
        view.openDetails(hotelId)
    }
}
