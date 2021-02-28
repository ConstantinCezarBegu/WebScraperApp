package com.constantin.webscraperapp.repository

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.constantin.webscraperapp.network.GoogleImageService
import com.constantin.webscraperapp.util.CoroutineDispatchers
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import comconstantinwebscraperapp.GoogleImage
import comconstantinwebscraperapp.GoogleImageEntityQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleImagesRepository @Inject constructor(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val googleImageService: GoogleImageService,
    private val googleImageEntityQueries: GoogleImageEntityQueries
) {

    private val imagesPagedListConfig = PagedList.Config.Builder().setPageSize(20).build()

    val savedImagesDataSource = LivePagedListBuilder(
        QueryDataSourceFactory(
            queryProvider = googleImageEntityQueries::selectAll,
            countQuery = googleImageEntityQueries.count(),
            transacter = googleImageEntityQueries
        ),
        imagesPagedListConfig,
    ).build()

    fun getOnlineImagesDataSource(
        coroutineScope: CoroutineScope,
        searchKey: String,
    ) = LivePagedListBuilder(
        OnlineImagesDataSourceFactory(
            searchKey = searchKey,
            coroutineScope = coroutineScope,
            googleImageService = googleImageService,
        ),
        imagesPagedListConfig,
    ).build()

    private val webPagedListConfig = PagedList.Config.Builder().setPageSize(20).build()

    fun getOnlineWebDataSource(
        coroutineScope: CoroutineScope,
        searchKey: String,
    ) = LivePagedListBuilder(
        OnlineWebDataSourceFactory(
            searchKey = searchKey,
            coroutineScope = coroutineScope,
            googleImageService = googleImageService,
        ),
        webPagedListConfig,
    ).build()


    suspend fun saveImage(url: String) = withContext(coroutineDispatchers.io) {
        googleImageEntityQueries.insert(
            googleImage = GoogleImage(url)
        )
    }

    suspend fun deleteImage(url: String) = withContext(coroutineDispatchers.io) {
        googleImageEntityQueries.delete(url)
    }
}