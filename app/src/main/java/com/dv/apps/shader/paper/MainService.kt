package com.dv.apps.shader.paper

import android.service.wallpaper.WallpaperService

class MainService : WallpaperService() {
    override fun onCreateEngine() = EngineImpl()

    inner class EngineImpl : Engine() {

    }
}