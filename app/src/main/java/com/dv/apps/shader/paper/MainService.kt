package com.dv.apps.shader.paper

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
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
            glSurfaceView?.setRenderer(ShaderRenderer())
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

        private class ShaderRenderer : GLSurfaceView.Renderer {
            private var programId = -1
            private var vertexId = -1
            private var fragmentId = -1

            private val fullQuadVertexBuffer = ByteBuffer
                .allocateDirect(6 * 4)
                .order(ByteOrder.nativeOrder())
                .also {
                    it.asFloatBuffer().put(
                        floatArrayOf(
                            -3f, -1f,
                            1f, -1f,
                            1f, 3f
                        )
                    )
                }

            override fun onSurfaceCreated(
                gl: GL10,
                config: EGLConfig
            ) {
                vertexId = loadShader(GL_VERTEX_SHADER) {
                    """
                    attribute vec4 vPosition;
                    void main()
                    {
                        gl_Position = vPosition;
                    }
                    """.trimIndent()
                } ?: -1

                fragmentId = loadShader(GL_FRAGMENT_SHADER) {
                    """
                    precision mediump float;
                    void main()
                    {
                        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
                    }
                    """.trimIndent()
                } ?: -1

                programId = glCreateProgram()

                glAttachShader(programId, vertexId)
                glAttachShader(programId, fragmentId)

                glBindAttribLocation(programId, 0, "vPosition")

                glLinkProgram(programId)

                val buffer = intArrayOf(-1)
                glGetProgramiv(programId, GL_LINK_STATUS, buffer, 0)

                if (buffer[0] == 0) {
                    val reason = glGetProgramInfoLog(programId)
                    android.util.Log.d("GLSLWallpaper", "Failed to link program: $reason")
                    glDeleteProgram(programId)
                    return
                }

                glUseProgram(programId)

                glEnableVertexAttribArray(0)
                glVertexAttribPointer(
                    0,
                    2,
                    GL_FLOAT,
                    false,
                    0,
                    fullQuadVertexBuffer
                )

                glDrawArrays(GL_TRIANGLES, 0, 3)
            }

            override fun onDrawFrame(gl: GL10) {

            }

            override fun onSurfaceChanged(
                gl: GL10?,
                width: Int,
                height: Int
            ) {
                glViewport(0, 0, width, height)
            }

            private fun loadShader(
                type: Int,
                source: () -> String,
            ): Int? {
                val id = glCreateShader(type)
                glShaderSource(id, source())
                glCompileShader(id)
                val buffer = intArrayOf(-1)
                glGetShaderiv(id, GL_COMPILE_STATUS, buffer, 0)
                if (buffer[0] == 0) {
                    val reason = glGetShaderInfoLog(id)
                    android.util.Log.d("GLSLWallpaper", "Failed to compile shader: $reason")
                    glDeleteShader(id)
                    return null
                }
                return id
            }
        }
    }
}