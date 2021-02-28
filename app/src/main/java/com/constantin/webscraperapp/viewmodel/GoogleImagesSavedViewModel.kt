package com.constantin.webscraperapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.constantin.webscraperapp.repository.GoogleImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleImagesSavedViewModel @Inject constructor(
    private val repository: GoogleImagesRepository,
    private val processLifecycleScope: LifecycleCoroutineScope,
    private val handle: SavedStateHandle
) : ViewModel() {

    val googleImagePagedList: LiveData<PagedList<String>> = repository.savedImagesDataSource

    fun deleteImage(url: String) = viewModelScope.launch { repository.deleteImage(url) }

}