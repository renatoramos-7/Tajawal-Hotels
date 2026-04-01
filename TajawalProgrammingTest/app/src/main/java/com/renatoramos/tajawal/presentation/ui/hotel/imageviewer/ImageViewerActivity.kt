package com.renatoramos.tajawal.presentation.ui.hotel.imageviewer

import android.os.Bundle
import com.renatoramos.tajawal.common.constants.AppConstants
import com.renatoramos.tajawal.common.extensions.loadWithGlide
import com.renatoramos.tajawal.databinding.ActivityImageViewerBinding
import com.renatoramos.tajawal.presentation.base.BaseActivity
import javax.inject.Inject

class ImageViewerActivity : BaseActivity(), ImageViewerContract.View {

    @Inject
    lateinit var presenter: ImageViewerPresenter

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    override fun loadImage(url: String) {
        binding.imageViewTouch.loadWithGlide(url)
    }

    override fun addImageClick() {
        binding.imageViewTouch.setSingleTapListener(this::finish)
    }

    private fun initialize() {
        intent.getStringExtra(AppConstants.IMAGE_URL)?.let(presenter::attachUrl)
        presenter.onStart()
    }

}
