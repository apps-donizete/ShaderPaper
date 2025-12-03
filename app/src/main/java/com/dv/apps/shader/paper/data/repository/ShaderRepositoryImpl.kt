package com.dv.apps.shader.paper.data.repository

import com.dv.apps.shader.paper.data.retrofit.RemoteShaderRepository
import com.dv.apps.shader.paper.domain.repository.ShaderRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShaderRepositoryImpl @Inject constructor(
    private val remoteShaderRepository: RemoteShaderRepository
) : ShaderRepository {
    override fun getManifest() = flow {
        emit(
            remoteShaderRepository.getManifest()
        )
    }
}