package com.example.vkcourse.applist.di

import com.example.vkcourse.applist.data.repository.AppRepositoryImpl
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.data.source.RemoteAppCatalogDataSource
import com.example.vkcourse.applist.domain.repository.AppRepository
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds
    @Singleton
    abstract fun bindAppCatalogDataSource(impl: RemoteAppCatalogDataSource): AppCatalogDataSource

    companion object {
        @Provides
        fun provideGetCatalogAppsUseCase(repository: AppRepository): GetCatalogAppsUseCase =
            GetCatalogAppsUseCase(repository)
    }
}
