package com.constantin.webscraperapp

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import coil.ImageLoader
import com.constantin.webscraperapp.database.DatabaseModule
import com.constantin.webscraperapp.network.GoogleImageService
import com.constantin.webscraperapp.network.NetworkDetails
import com.constantin.webscraperapp.network.NetworkModule
import com.constantin.webscraperapp.util.CoroutineDispatchers
import com.constantin.webscraperapp.util.FileUtils
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.HttpUrl
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class
    ]
)
@InstallIn(SingletonComponent::class)
interface ApplicationModule {

    companion object {

        @Provides
        @Singleton
        fun provideCoroutineDispatchers(): CoroutineDispatchers {
            return object : CoroutineDispatchers {
                override val default = Dispatchers.Default
                override val unconfined = Dispatchers.Unconfined
                override val io = Dispatchers.IO
            }
        }

        @Provides
        fun provideNetworkDetails(@ApplicationContext context: Context): NetworkDetails {
            return NetworkDetails(
                baseUrl = HttpUrl.get(GoogleImageService.baseUrl),
                cacheDir = context.cacheDir
            )
        }

        @Provides
        fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
            return AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = "webscrapper.db"
            )
        }

        @Provides
        fun provideProcessLifecycleCoroutineScope(): LifecycleCoroutineScope {
            return ProcessLifecycleOwner.get().lifecycleScope
        }

        @Provides
        fun provideWorkManager(@ApplicationContext context: Context) =
            WorkManager.getInstance(context)

        @Provides
        @Singleton
        fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
            return ImageLoader.Builder(context)
                .build()
        }

        @Provides
        @Singleton
        fun provideCustomTabsIntent(@ApplicationContext context: Context): CustomTabsIntent =
            CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams
                        .Builder()
                        .setToolbarColor(context.getColor(R.color.design_default_color_primary))
                        .build()
                )
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .build().also {
                    it.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

        @Provides
        @Singleton
        fun provideImageDownloader(
            @ApplicationContext context: Context,
        ): FileUtils = FileUtils(context)
    }
}
