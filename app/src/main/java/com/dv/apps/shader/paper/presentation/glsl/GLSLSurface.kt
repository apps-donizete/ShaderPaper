package com.dv.apps.shader.paper.presentation.glsl

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun GLSLSurface(
    modifier: Modifier = Modifier,
    fragmentShader: String
) {
    AndroidView(
        factory = ::GLSurfaceView,
        modifier = modifier,
        update = {
            it.setRenderer(GLSLRenderer { fragmentShader })
        }
    )
}