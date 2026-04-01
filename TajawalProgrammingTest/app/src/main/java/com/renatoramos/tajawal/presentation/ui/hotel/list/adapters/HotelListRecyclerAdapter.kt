package com.renatoramos.tajawal.presentation.ui.hotel.list.adapters

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.renatoramos.tajawal.common.extensions.loadWithGlide
import com.renatoramos.tajawal.common.ui.components.DrawableRequestListener
import com.renatoramos.tajawal.data.model.HotelModel
import com.renatoramos.tajawal.databinding.HotelViewholderBinding

class HotelListRecyclerAdapter(
    private val hotelModelList: List<HotelModel>,
    private val hotelListAdapterListener: HotelListAdapterListener
) : RecyclerView.Adapter<HotelListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelListViewHolder {
        val binding = HotelViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val mainViewHolder = HotelListViewHolder(binding)

        //Cell clicks
        mainViewHolder.itemView.setOnClickListener {hotelListAdapterListener.onItemClick(mainViewHolder.adapterPosition)}
        return mainViewHolder
    }

    override fun getItemCount(): Int {
        return hotelModelList.size
    }

    override fun onBindViewHolder(viewHolder: HotelListViewHolder, position: Int) {
        // Just bind View Objects.
        val hotelModel = hotelModelList[position]

        hotelModel.image!![0].url?.let { setupImageView(viewHolder, it) }
        viewHolder.binding.titleTextView.text = hotelModel.summary!!.hotelName
    }

    private fun setupImageView(holder: HotelListViewHolder, url: String) {
        holder.binding.placeGuideImageView.loadWithGlide(url, object : DrawableRequestListener() {
            override fun onResourceReady(bitmap: Bitmap){
                holder.binding.placeGuideImageView.setRatio(bitmap.width.toFloat() / bitmap.height)
            }
        })
    }
}
