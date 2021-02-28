package com.constantin.webscraperapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.constantin.webscraperapp.network.GoogleImageService
import com.constantin.webscraperapp.network.Response
import com.constantin.webscraperapp.network.fetch
import com.constantin.webscraperapp.network.getImageLinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OnlineImagesDataSource(
    private val searchKey: String,
    private val coroutineScope: CoroutineScope,
    private val googleImageService: GoogleImageService,
) : ItemKeyedDataSource<Long, String>() {
    private var key: Int = 0

    override fun getKey(item: String): Long = key.toLong()

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<String>
    ) {
        coroutineScope.launch {
            val response = googleImageService.fetch {
                getImageLinks(
                    search = searchKey,
                    start = key
                )
            }

            callback.onResult(
                when (response) {
                    is Response.Success -> response.body
                    else -> listOf()
                }
            )
        }
    }

    override fun loadAfter(
        params: LoadParams<Long>,
        callback: LoadCallback<String>
    ) {
        coroutineScope.launch {
            val response = googleImageService.fetch {
                googleImageService.getImageLinks(
                    search = searchKey,
                    start = key
                )
            }

            val list = when (response) {
                is Response.Success -> response.body
                else -> listOf()
            }

            key += list.size

            callback.onResult(list)
        }
    }

    override fun loadBefore(
        params: LoadParams<Long>,
        callback: LoadCallback<String>
    ) {
    }
}

class OnlineImagesDataSourceFactory(
    private val searchKey: String,
    private val coroutineScope: CoroutineScope,
    private val googleImageService: GoogleImageService
) : DataSource.Factory<Long, String>() {
    private val sourceLiveData = MutableLiveData<OnlineImagesDataSource>()
    private lateinit var latestSource: OnlineImagesDataSource
    override fun create(): DataSource<Long, String> {
        latestSource = OnlineImagesDataSource(
            searchKey, coroutineScope, googleImageService
        )

        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}