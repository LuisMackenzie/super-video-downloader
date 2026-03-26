package com.mackenzie.data.remote.service

import com.mackenzie.data.local.room.entity.SupportedPage
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET

interface ConfigService {

    @GET("supported_pages.json")
    fun getSupportedPages(): Flowable<List<SupportedPage>>
}