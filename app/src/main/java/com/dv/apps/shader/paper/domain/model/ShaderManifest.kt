package com.dv.apps.shader.paper.domain.model

data class ShaderManifest(
    val description: String,
    val items: List<ShaderManifestItem>
) {
    data class ShaderManifestItem(
        val title: String,
        val path: String
    )
}