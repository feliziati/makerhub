package com.example.makerhub.di.module

import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.usecase.GetItemListUseCase
import com.example.makerhub.domain.usecase.ImportDataUseCase
import com.example.makerhub.domain.usecase.GetItemDetailUseCase
import com.example.makerhub.domain.usecase.ReturnItemUseCase
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

    @Provides
    fun provideGetItemsUseCase(
        localRepository: LocalRepository
    ): GetItemListUseCase {
        return GetItemListUseCase(localRepository)
    }

    @Provides
    fun provideManageItemUseCase(
        localRepository: LocalRepository
    ): GetItemDetailUseCase {
        return GetItemDetailUseCase(localRepository)
    }

    @Provides
    fun provideReturnItemUseCase(localRepository: LocalRepository): ReturnItemUseCase {
        return ReturnItemUseCase(localRepository)
    }
}
