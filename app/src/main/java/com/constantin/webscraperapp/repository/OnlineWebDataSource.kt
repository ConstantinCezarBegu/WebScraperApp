package com.constantin.webscraperapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.constantin.webscraperapp.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OnlineWebDataSource(
    private val searchKey: String,
    private val coroutineScope: CoroutineScope,
    private val googleImageService: GoogleImageService,
) : ItemKeyedDataSource<Long, WebSearch>() {
    private var key: Int = 0

    override fun getKey(item: WebSearch): Long = key.toLong()

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<WebSearch>
    ) {
        coroutineScope.launch {
            val response = googleImageService.fetch {
                getWebLinks(
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
        callback: LoadCallback<WebSearch>
    ) {
        coroutineScope.launch {
            val response = googleImageService.fetch {
                googleImageService.getWebLinks(
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
        callback: LoadCallback<WebSearch>
    ) {
    }
}

class OnlineWebDataSourceFactory(
    private val searchKey: String,
    private val coroutineScope: CoroutineScope,
    private val googleImageService: GoogleImageService
) : DataSource.Factory<Long, WebSearch>() {
    private val sourceLiveData = MutableLiveData<OnlineWebDataSource>()
    private lateinit var latestSource: OnlineWebDataSource
    override fun create(): DataSource<Long, WebSearch> {
        latestSource = OnlineWebDataSource(
            searchKey, coroutineScope, googleImageService
        )

        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}