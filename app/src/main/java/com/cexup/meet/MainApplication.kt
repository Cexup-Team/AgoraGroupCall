package com.cexup.meet

import android.app.Application

class MainApplication : Application() {
    private var globalSettings:GlobalSettings?= null


    fun getGlobalSettings():GlobalSettings? {
        if(globalSettings== null) {
            globalSettings = GlobalSettings()
        }
        return globalSettings
    }

}