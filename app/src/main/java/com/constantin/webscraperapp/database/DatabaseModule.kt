package com.constantin.webscraperapp.database

import com.constantin.webscraperapp.Database
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module
@DisableInstallInCheck
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(sqliteDriver: SqlDriver): Database = Database(sqliteDriver)


    @Provides
    fun provideGoogleImagesQueries(database: Database) = database.googleImageEntityQueries
}
