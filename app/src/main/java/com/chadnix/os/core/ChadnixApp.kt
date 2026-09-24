package com.chadnix.os.core

import android.app.Application

class ChadnixApp : Application() {

    companion object {
        lateinit var instance: ChadnixApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
