package com.dv.apps.shader.paper.di

import com.dv.apps.shader.paper.data.retrofit.RemoteShaderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private const val URL = "https://raw.githubusercontent.com/apps-donizete/ShaderPaper/refs/heads/main/repository/"

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    fun providesRetrofit(): Retrofit = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                )
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(URL)
        .build()

    @Provides
    fun providesRemoteShaderRepository(
        retrofit: Retrofit
    ) = retrofit.create<RemoteShaderRepository>()
}