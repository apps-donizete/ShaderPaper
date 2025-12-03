package com.dv.apps.shader.paper.di

import com.dv.apps.shader.paper.data.repository.ShaderRepositoryImpl
import com.dv.apps.shader.paper.domain.repository.ShaderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {
    @Binds
    fun bindsShaderRepositoryImpl(
        impl: ShaderRepositoryImpl
    ): ShaderRepository
}