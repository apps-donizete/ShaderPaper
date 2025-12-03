package com.dv.apps.shader.paper.data.retrofit

import com.dv.apps.shader.paper.domain.model.ShaderManifest
import retrofit2.http.GET

interface RemoteShaderRepository {
    @GET("repository/manifest.json")
    suspend fun getManifest(): ShaderManifest
}