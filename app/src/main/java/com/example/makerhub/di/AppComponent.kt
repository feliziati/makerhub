package com.example.makerhub.di

import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.di.module.ExternalModule
import com.example.makerhub.di.module.RepositoryModule
import com.example.makerhub.di.module.UseCaseModule
import com.example.makerhub.domain.usecase.ImportDataUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ExternalModule::class,
        RepositoryModule::class,
        UseCaseModule::class
    ]
)
interface AppComponent {
    fun getImportDataUseCase(): ImportDataUseCase
    fun getLocalRepository(): LocalRepository
    fun getDataSourceRepository(): DataSourceRepository
}
