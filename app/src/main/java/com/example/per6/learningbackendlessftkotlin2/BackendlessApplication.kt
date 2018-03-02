package com.example.per6.learningbackendlessftkotlin2

import android.app.Application
import com.backendless.Backendless

/**
 * Created by per6 on 3/2/18.
 */
class BackendlessApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Backendless.initApp(this, BackendSettings.APP_ID, BackendSettings.API_KEY)
    }
}