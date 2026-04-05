package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.data.datasources.EmbeddedVideoResolver
import com.mackenzie.downhub.data.datasources.VideoHubRemoteDataSource
import com.mackenzie.downhub.data.embed.JsoupEmbeddedVideoResolver
import com.mackenzie.downhub.data.remote.ConfigRemoteDataSource
import com.mackenzie.downhub.data.remote.TopPagesRemoteDataSource
import com.mackenzie.downhub.data.remote.VideoRemoteDataSource
import com.mackenzie.downhub.di.qualifier.LocalData
import com.mackenzie.downhub.di.qualifier.RemoteData
import com.mackenzie.downhub.data.local.ConfigLocalDataSource
import com.mackenzie.downhub.data.local.FavoritesLocalDataSource
import com.mackenzie.downhub.data.local.HistoryLocalDataSource
import com.mackenzie.downhub.data.local.ProgressLocalDataSource
import com.mackenzie.downhub.data.local.TopPagesLocalDataSource
import com.mackenzie.downhub.data.local.VideoLocalDataSource
import com.mackenzie.downhub.data.remote.datasource.VideoHubDataSource
import com.mackenzie.downhub.data.repository.ConfigRepository
import com.mackenzie.downhub.data.repository.ConfigRepositoryImpl
import com.mackenzie.downhub.data.repository.FavoritesRepository
import com.mackenzie.downhub.data.repository.FavoritesRepositoryImpl
import com.mackenzie.downhub.data.repository.HistoryRepository
import com.mackenzie.downhub.data.repository.HistoryRepositoryImpl
import com.mackenzie.downhub.data.repository.ProgressRepository
import com.mackenzie.downhub.data.repository.ProgressRepositoryImpl
import com.mackenzie.downhub.data.repository.TopPagesRepository
import com.mackenzie.downhub.data.repository.TopPagesRepositoryImpl
import com.mackenzie.downhub.data.repository.VideoRepository
import com.mackenzie.downhub.data.repository.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    @LocalData
    abstract fun bindConfigLocalDataSource(localDataSource: ConfigLocalDataSource): ConfigRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindConfigRemoteDataSource(remoteDataSource: ConfigRemoteDataSource): ConfigRepository

    @Singleton
    @Binds
    abstract fun bindConfigRepositoryImpl(configRepository: ConfigRepositoryImpl): ConfigRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindTopPagesLocalDataSource(localDataSource: TopPagesLocalDataSource): TopPagesRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindTopPagesRemoteDataSource(remoteDataSource: TopPagesRemoteDataSource): TopPagesRepository

    @Singleton
    @Binds
    abstract fun bindTopPagesRepositoryImpl(topPagesRepository: TopPagesRepositoryImpl): TopPagesRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindVideoLocalDataSource(localDataSource: VideoLocalDataSource): VideoRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindVideoRemoteDataSource(remoteDataSource: VideoRemoteDataSource): VideoRepository

    @Singleton
    @Binds
    abstract fun bindVideoRepositoryImpl(videoRepository: VideoRepositoryImpl): VideoRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindProgressLocalDataSource(localDataSource: ProgressLocalDataSource): ProgressRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindHistoryLocalDataSource(localDataSource: HistoryLocalDataSource): HistoryRepository


    @Singleton
    @Binds
    abstract fun bindProgressRepositoryImpl(progressRepository: ProgressRepositoryImpl): ProgressRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepositoryImpl(historyRepository: HistoryRepositoryImpl): HistoryRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindFavoritesLocalDataSource(localDataSource: FavoritesLocalDataSource): FavoritesRepository

    @Singleton
    @Binds
    abstract fun bindFavoritesRepositoryImpl(favoritesRepository: FavoritesRepositoryImpl): FavoritesRepository

    @Singleton
    @Binds
    abstract fun bindRemoteVideoHubDataSource(remoteVideoHubDataSource: VideoHubDataSource): VideoHubRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindEmbeddedVideoResolver(impl: JsoupEmbeddedVideoResolver): EmbeddedVideoResolver
}