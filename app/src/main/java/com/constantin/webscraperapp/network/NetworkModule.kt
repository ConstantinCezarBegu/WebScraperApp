package com.constantin.webscraperapp.network

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

data class NetworkDetails(
    val baseUrl: HttpUrl,
    val cacheDir: File? = null
)

@Module
@DisableInstallInCheck
object NetworkModule {

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    private annotation class InternalApi

    @Provides
    @InternalApi
    fun provideCache(networkDetails: NetworkDetails): Cache? {
        val cacheSize = 10L * 1024 * 1024 // 10 MiB
        return networkDetails.cacheDir?.let { Cache(it, cacheSize) }
    }

    @Provides
    @Singleton
    fun provideUserAgentInterceptor(): UserAgentInterceptor = UserAgentInterceptor(
        "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"
    )

    @Provides
    @Singleton
    fun provideClient(
        UserAgentInterceptor: UserAgentInterceptor
    ): OkHttpClient = OkHttpClient.Builder().addInterceptor(UserAgentInterceptor).build()


    @Provides
    @Singleton
    fun provideGoogleImageService(
        client: OkHttpClient,
        networkDetails: NetworkDetails,
    ): GoogleImageService = Retrofit.Builder()
        .baseUrl(networkDetails.baseUrl)
        .addConverterFactory(JsoupConverterFactory)
        .client(client)
        .build()
        .create()

}
