package com.renatoramos.tajawal.presentation.ui.hotel.list.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.renatoramos.tajawal.R
import com.renatoramos.tajawal.common.ui.widgets.ResizableImageView

class HotelListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val placeGuideImageView by lazy { itemView?.findViewById<ResizableImageView>(R.id.placeGuideImageView)}
    val titleTextView by lazy { itemView?.findViewById<TextView>(R.id.titleTextView)}
}
