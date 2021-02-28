package com.constantin.webscraperapp.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.GetRequest
import coil.request.SuccessResult
import com.constantin.webscraperapp.repository.GoogleImagesRepository
import com.constantin.webscraperapp.util.CoroutineDispatchers
import com.constantin.webscraperapp.util.FileUtils
import com.constantin.webscraperapp.util.GoogleImageDisplayState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoogleImageViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val repository: GoogleImagesRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val processLifecycleScope: LifecycleCoroutineScope,
    private val handle: SavedStateHandle,
    private val imageLoader: ImageLoader,
    private val fileUtils: FileUtils,
) : ViewModel() {

    companion object {
        private const val GOOGLE_IMAGE_URL = "imageUrl"
        private const val GOOGLE_IMAGE_DISPLAY_STATE = "imageState"
    }

    var googleImageUrl: String
        get() = handle.get<String>(GOOGLE_IMAGE_URL)!!
        set(value) {
            handle.set(GOOGLE_IMAGE_URL, value)
        }

    var googleImageState: GoogleImageDisplayState
        get() = handle.get<GoogleImageDisplayState>(GOOGLE_IMAGE_DISPLAY_STATE)!!
        set(value) {
            handle.set(GOOGLE_IMAGE_DISPLAY_STATE, value)
        }


    private fun saveImage() = viewModelScope.launch { repository.saveImage(googleImageUrl) }

    private fun deleteImage() = viewModelScope.launch { repository.deleteImage(googleImageUrl) }

    fun imageAction() {
        if (googleImageState == GoogleImageDisplayState.Saved) {
            googleImageState = GoogleImageDisplayState.Online
            deleteImage()
        } else {
            googleImageState = GoogleImageDisplayState.Saved
            saveImage()
        }
    }

    fun downloadImage(activity: Activity, onComplete: () -> Unit) {
        viewModelScope.launch {
            val request = GetRequest.Builder(applicationContext)
                .data(googleImageUrl)
                .allowHardware(false)
                .build()

            val result = (imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            if (fileUtils.saveBitmap(bitmap, googleImageUrl, activity)) {
                onComplete()
            }
        }
    }
}