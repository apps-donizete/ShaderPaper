package com.dv.apps.shader.paper.domain.model

data class ShaderManifest(
    val baseUrl: String = "",
    val description: String = "",
    val items: List<ShaderManifestItem> = emptyList()
) {
    data class ShaderManifestItem(
        val title: String,
        val path: String
    )
}