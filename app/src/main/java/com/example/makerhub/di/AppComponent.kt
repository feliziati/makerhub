package com.example.makerhub.di

import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.di.module.ExternalModule
import com.example.makerhub.di.module.RepositoryModule
import com.example.makerhub.di.module.UseCaseModule
import com.example.makerhub.domain.usecase.CheckoutItemUseCase
import com.example.makerhub.domain.usecase.GetItemListUseCase
import com.example.makerhub.domain.usecase.ImportDataUseCase
import com.example.makerhub.domain.usecase.GetItemDetailUseCase
import com.example.makerhub.domain.usecase.LostItemUseCase
import com.example.makerhub.domain.usecase.ReturnItemUseCase
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
    fun getGetItemsUseCase(): GetItemListUseCase
    fun getItemDetailUseCase(): GetItemDetailUseCase
    fun getCheckoutItemUseCase(): CheckoutItemUseCase
    fun getReturnItemUseCase(): ReturnItemUseCase
    fun getLostItemUseCase(): LostItemUseCase
    fun getLocalRepository(): LocalRepository
    fun getDataSourceRepository(): DataSourceRepository
}
