package com.example.makerhub.di.module

import com.example.makerhub.data.local.AppDatabase
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.data.repository.impl.LocalRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLocalRepository(appDatabase: AppDatabase): LocalRepository {
        return LocalRepositoryImpl(appDatabase)
    }
}
