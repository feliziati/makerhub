package com.example.makerhub.di.module

import android.content.Context
import com.example.makerhub.data.local.AppDatabase
import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.impl.DataSourceRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ExternalModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideGson(): Gson = DataSourceRepositoryImpl.createGson()

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDataSourceRepository(
        context: Context,
        gson: Gson
    ): DataSourceRepository {
        return DataSourceRepositoryImpl(context, gson)
    }
}
