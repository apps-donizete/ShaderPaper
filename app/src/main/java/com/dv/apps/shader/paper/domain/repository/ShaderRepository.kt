package com.dv.apps.shader.paper.domain.repository

import com.dv.apps.shader.paper.domain.model.ShaderManifest
import kotlinx.coroutines.flow.Flow

interface ShaderRepository {
    fun getManifest(): Flow<ShaderManifest>
}