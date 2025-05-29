package com.micah.jikan

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreManager.init(this)
    }
}