package com.constantin.webscraperapp.viewmodel

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.constantin.webscraperapp.repository.GoogleImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleImagesOnlineViewModel @Inject constructor(
    private val repository: GoogleImagesRepository,
    private val processLifecycleScope: LifecycleCoroutineScope,
    private val handle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val GOOGLE_IMAGE_SEARCH_KEY = "searchKey"
    }

    var googleImageSearchKey: String
        get() = handle.get<String>(GOOGLE_IMAGE_SEARCH_KEY)!!
        set(value) {
            handle.set(GOOGLE_IMAGE_SEARCH_KEY, value)
        }

    val googleImagePagedList =
        repository.getOnlineImagesDataSource(viewModelScope, googleImageSearchKey)

    fun saveImage(url: String) = viewModelScope.launch { repository.saveImage(url) }


}