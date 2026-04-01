package com.renatoramos.tajawal.hotel.imageviewer

import com.renatoramos.tajawal.presentation.ui.hotel.imageviewer.ImageViewerContract
import com.renatoramos.tajawal.presentation.ui.hotel.imageviewer.ImageViewerPresenter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ImageViewerPresenterTest {

    private lateinit var presenter: ImageViewerPresenter
    private lateinit var view: FakeImageViewerView

    @Before
    fun setUp() {
        view = FakeImageViewerView()
        presenter = ImageViewerPresenter(view)
    }

    @Test
    fun `OnStart`() {
        presenter.onStart()

        assertEquals(1, view.addImageClickCalls)
    }

    @Test
    fun `attachUrl`() {
        presenter.attachUrl("url")

        assertEquals("url", view.loadedImageUrl)
    }

    private class FakeImageViewerView : ImageViewerContract.View {
        var loadedImageUrl: String? = null
        var addImageClickCalls = 0

        override fun loadImage(url: String) {
            loadedImageUrl = url
        }

        override fun addImageClick() {
            addImageClickCalls++
        }
    }
}
