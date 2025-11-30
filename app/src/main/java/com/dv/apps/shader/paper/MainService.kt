package com.dv.apps.shader.paper

import android.graphics.Color
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class MainService : WallpaperService() {
    override fun onCreateEngine() = EngineImpl()

    inner class EngineImpl : Engine(), LifecycleOwner {
        override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

            val canvas = holder.lockCanvas()
            canvas.drawColor(Color.RED)
            holder.unlockCanvasAndPost(canvas)
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
}