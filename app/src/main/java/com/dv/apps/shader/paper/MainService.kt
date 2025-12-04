package com.dv.apps.shader.paper

import android.content.Context
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dv.apps.shader.paper.presentation.glsl.GLSLRenderer
import kotlinx.coroutines.launch

class MainService : WallpaperService() {
    override fun onCreateEngine() = EngineImpl()

    inner class EngineImpl : Engine(), LifecycleOwner {
        override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
        private val glslWallpaper = GLSLWallpaper(applicationContext, this)

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            lifecycleScope.launch {
                launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        glslWallpaper.initialize()
                    }
                }
                launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        glslWallpaper.stopRendering()
                    }
                }
                launch {
                    repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        glslWallpaper.startRendering()
                    }
                }
                launch {
                    repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                        glslWallpaper.destroy()
                    }
                }
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (isVisible) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            } else {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
            }
        }

        override fun onDestroy() {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
    }

    private class GLSLWallpaper(
        private val context: Context,
        private val engine: Engine
    ) {
        private var glSurfaceView: GLSurfaceViewImpl? = null

        suspend fun initialize() {
            glSurfaceView = object : GLSurfaceViewImpl(context) {
                override fun getHolder() = engine.surfaceHolder
            }
            glSurfaceView?.setEGLContextClientVersion(2)
            glSurfaceView?.preserveEGLContextOnPause = true

            val fragmentShaderBuilder = {
                context.resources.openRawResource(
                    R.raw.complex_shader
                ).use {
                    it.readBytes().decodeToString()
                }
            }
            glSurfaceView?.setRenderer(GLSLRenderer(fragmentShaderBuilder))
        }

        suspend fun startRendering() {
            glSurfaceView?.onResume()
        }

        suspend fun stopRendering() {
            glSurfaceView?.onPause()
        }

        suspend fun destroy() {
            glSurfaceView?.onDestroy()
            glSurfaceView = null
        }

        private abstract class GLSurfaceViewImpl(
            context: Context
        ) : GLSurfaceView(context) {
            abstract override fun getHolder(): SurfaceHolder

            fun onDestroy() {
                onDetachedFromWindow()
            }
        }
    }
}