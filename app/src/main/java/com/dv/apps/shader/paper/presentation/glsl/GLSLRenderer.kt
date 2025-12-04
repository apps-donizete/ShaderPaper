package com.dv.apps.shader.paper.presentation.glsl

import android.opengl.GLES10.GL_FLOAT
import android.opengl.GLES10.GL_TRIANGLES
import android.opengl.GLES10.glDrawArrays
import android.opengl.GLES10.glViewport
import android.opengl.GLES20.GL_COMPILE_STATUS
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINK_STATUS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glBindAttribLocation
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.glGetProgramiv
import android.opengl.GLES20.glGetShaderInfoLog
import android.opengl.GLES20.glGetShaderiv
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glUniform1f
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSLRenderer(
    private val fragmentShaderBuilder: () -> String
) : GLSurfaceView.Renderer {
    private var programId = -1
    private var vertexId = -1
    private var fragmentId = -1

    private var iResolutionId = -1
    private var iTimeId = -1

    private val fullScreenTriangleBuffer = ByteBuffer
        .allocateDirect(6 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(
                floatArrayOf(
                    -3f, -1f,
                    1f, -1f,
                    1f, 3f
                )
            )
            position(0)
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
        }

        fragmentId = loadShader(GL_FRAGMENT_SHADER, fragmentShaderBuilder)

        programId = glCreateProgram().also {
            glAttachShader(it, vertexId)
            glAttachShader(it, fragmentId)
            glBindAttribLocation(it, 0, "vPosition")
            glLinkProgram(it)

            val linkStatus = intArrayOf(-1)
            glGetProgramiv(it, GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == 0) {
                glDeleteProgram(programId)

                val reason = glGetProgramInfoLog(programId)
                Log.e("GLSLWallpaper", "Failed to link program: $reason")

                throw IllegalStateException("Failed to link program")
            }
        }

        glDeleteShader(vertexId)
        glDeleteShader(fragmentId)

        glUseProgram(programId)

        iResolutionId = glGetUniformLocation(programId, "iResolution")
        iTimeId = glGetUniformLocation(programId, "iTime")

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, fullScreenTriangleBuffer)
    }

    override fun onDrawFrame(gl: GL10) {
        glUseProgram(programId)
        glUniform1f(iTimeId, SystemClock.uptimeMillis() / 1000f)
        glDrawArrays(GL_TRIANGLES, 0, 3)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        glViewport(0, 0, width, height)
        glUseProgram(programId)
        glUniform2f(iResolutionId, width.toFloat(), height.toFloat())
    }

    private fun loadShader(
        type: Int,
        source: () -> String,
    ): Int {
        val id = glCreateShader(type)

        glShaderSource(id, source())
        glCompileShader(id)

        val compileStatus = intArrayOf(-1)
        glGetShaderiv(id, GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            glDeleteShader(id)

            val reason = glGetShaderInfoLog(id)
            Log.e("GLSLWallpaper", "Failed to compile shader: $reason")

            throw IllegalStateException("Failed to compile shader")
        }

        return id
    }
}