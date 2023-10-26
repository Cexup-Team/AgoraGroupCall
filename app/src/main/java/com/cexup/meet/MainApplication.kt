package com.cexup.meet

import android.app.Application
import android.util.Log
import com.cexup.meet.utils.SDKManager

class MainApplication : Application() {
    private var globalSettings:GlobalSettings?= null


    override fun onCreate() {
        super.onCreate()
    }


    fun getGlobalSettings():GlobalSettings? {
        if(globalSettings== null) {
            globalSettings = GlobalSettings()
        }
        return globalSettings
    }

}