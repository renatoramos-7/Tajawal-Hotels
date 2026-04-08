package com.renatoramos.tajawal.presentation.ui.hotel.list

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.Toast
import com.renatoramos.tajawal.R
import com.renatoramos.tajawal.common.constants.AppConstants
import com.renatoramos.tajawal.common.extensions.makeTextToast
import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.databinding.ActivityHotelListBinding
import com.renatoramos.tajawal.databinding.ToolbarBaseWithTitleBinding
import com.renatoramos.tajawal.presentation.base.BaseActivity
import com.renatoramos.tajawal.presentation.ui.hotel.detail.DetailsActivity
import com.renatoramos.tajawal.presentation.ui.hotel.list.adapters.HotelListAdapterListener
import com.renatoramos.tajawal.presentation.ui.hotel.list.adapters.HotelListRecyclerAdapter
import javax.inject.Inject


class HotelListActivity : BaseActivity(), HotelListContract.View, HotelListAdapterListener {

    @Inject
    lateinit var presenter: HotelListPresenter

    private lateinit var binding: ActivityHotelListBinding
    private lateinit var toolbarBinding: ToolbarBaseWithTitleBinding
    private lateinit var hotelListRecyclerAdapter: HotelListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarBinding = ToolbarBaseWithTitleBinding.bind(binding.root)
        initialize()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun showError(error: String) {
        baseContext.makeTextToast(error, Toast.LENGTH_LONG).show()
    }

    override fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showAdapter() {
        binding.mainRecyclerView.adapter = hotelListRecyclerAdapter
    }

    override fun loadHotelList() {
        presenter.getHotelList()
    }
    
    override fun createAdapter(hotelModelList: List<HotelModel>) {
        hotelListRecyclerAdapter = HotelListRecyclerAdapter(
            hotelModelList,
            this
        )
    }

    override fun onItemClick(position: Int) {
        presenter.onItemClick(position)
    }

    override fun openDetails(hotelId: Int) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(AppConstants.HOTEL_ID, hotelId)
        startActivity(intent)
    }

    override fun setupRecyclerView() {
        binding.mainRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun setToolbar() {
        toolbarBinding.toolbarTitleTextView.text = getString(R.string.HOTEL_LIST_TITLE)
    }

    private fun initialize() {
        presenter.onStart()
    }

}
