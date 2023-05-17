package com.example.agoratesting

import android.app.Application
import com.example.Examples

class MainApplication : Application() {
    private var globalSettings:GlobalSettings?= null


    override fun onCreate() {
        super.onCreate()
        initExamples()
    }

    private fun initExamples() {
        try {
            val packageName:Set<String> = ClassUtils.getFileNameByPackageName(this,"com.example.agoratesting")
            for (name in packageName) {
                val aClass:Class<*> = Class.forName(name)
                val declaredAnnotation = aClass.annotations
                for (annotation in declaredAnnotation){
                    if (annotation is Example) {
                        val example: Example = annotation as Example
                        Examples.addItem(example)
                    }
                }
            }
            Examples.sortItem()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun getGlobalSettings():GlobalSettings? {
        if(globalSettings== null) {
            globalSettings = GlobalSettings()
        }
        return globalSettings
    }
}