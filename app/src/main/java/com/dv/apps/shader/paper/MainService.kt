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
import kotlinx.coroutines.launch
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainService : WallpaperService() {
    override fun onCreateEngine() = EngineImpl()

    inner class EngineImpl : Engine(), LifecycleOwner {
        override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
        private val glslWallpaper = GLSLWallpaper(applicationContext, this)

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    glslWallpaper.onCreate()
                }
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    glslWallpaper.onResume()
                }
                repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                    glslWallpaper.onDestroy()
                }
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
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

        suspend fun onCreate() {
            glSurfaceView = object : GLSurfaceViewImpl(context) {
                override fun getHolder() = engine.surfaceHolder
            }
            glSurfaceView?.setEGLContextClientVersion(2)
            glSurfaceView?.preserveEGLContextOnPause = true
            glSurfaceView?.setRenderer(redPainter)
        }

        suspend fun onResume() {
            glSurfaceView?.onResume()
        }

        suspend fun onPause() {
            glSurfaceView?.onPause()
        }

        suspend fun onDestroy() {
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

        private val redPainter = object : GLSurfaceView.Renderer {
            override fun onDrawFrame(gl: GL10) {
                gl.glClearColor(1f, 0f, 0f, 1f)
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
            }

            override fun onSurfaceChanged(
                gl: GL10,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceCreated(
                gl: GL10,
                config: EGLConfig
            ) {
            }
        }
    }
}