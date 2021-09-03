package com.filipau.data.api

import com.chenxyu.retrofit.adapter.FlowCallAdapterFactory
import com.filipau.data.NetConstants.BASE_URL
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObj {

    private val loggingInterceptor = HttpLoggingInterceptor()
    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    private val flowRetrofitBuildr = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(FlowCallAdapterFactory())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    val POST_SERVICE: PostsService = retrofitBuilder.create(PostsService::class.java)
    val FLOW_USER_SERVICE: UsersService =
        flowRetrofitBuildr.create(UsersService::class.java)

    fun getPostApi(): PostsService = POST_SERVICE
    fun getUserApi(): UsersService = FLOW_USER_SERVICE

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }
}