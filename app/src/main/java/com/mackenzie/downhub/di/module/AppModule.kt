package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.data.datasources.EmbeddedVideoResolver
import com.mackenzie.downhub.data.datasources.VideoHubRemoteDataSource
import com.mackenzie.downhub.data.embed.JsoupEmbeddedVideoResolver
import com.mackenzie.downhub.data.remote.datasource.VideoHubDataSource
import com.mackenzie.downhub.data.remote.models.RemoteVideoHubConnect
import com.mackenzie.downhub.data.remote.service.VideoHubService
import com.mackenzie.downhub.domain.video.ApiVideoUrl
import com.mackenzie.downhub.util.scheduler.BaseSchedulers
import com.mackenzie.downhub.util.scheduler.BaseSchedulersImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Provides
    @Singleton
    fun provideVideoApiUrl(): ApiVideoUrl = ApiVideoUrl()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideVideoHubService(apiUrl: ApiVideoUrl, client: OkHttpClient, moshi: Moshi): RemoteVideoHubConnect {

        val builderHub = Retrofit.Builder()
            .baseUrl(apiUrl.redtubeBaseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val service = builderHub.create(VideoHubService::class.java)

        val connection = RemoteVideoHubConnect(service)

        return connection
    }

    /*@Provides
    @Singleton
    fun networkHubModule() = NetworkHubModule()*/

    @Singleton
    @Binds
    abstract fun bindBaseSchedulers(baseSchedulers: BaseSchedulersImpl): BaseSchedulers
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppDataModule {

    @Binds
    abstract fun bindRemoteVideoHubDataSource(remoteVideoHubDataSource: VideoHubDataSource): VideoHubRemoteDataSource

    @Binds
    abstract fun bindEmbeddedVideoResolver(impl: JsoupEmbeddedVideoResolver): EmbeddedVideoResolver
}
