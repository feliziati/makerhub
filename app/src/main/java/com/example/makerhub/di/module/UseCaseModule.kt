package com.example.makerhub.di.module

import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.usecase.ImportDataUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideImportDataUseCase(
        dataSource: DataSourceRepository,
        local: LocalRepository
    ): ImportDataUseCase {
        return ImportDataUseCase(dataSource, local)
    }
}
