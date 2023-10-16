package com.cexup.meet.utils

import android.content.Context

internal class DataPreference(context: Context){

    private val preferences = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)

    fun getPrefString(key:String):String?{
        return preferences.getString(key, "")
    }

    fun savePrefString(key: String, value: String){
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPrefInt(key: String): Int{
        return preferences.getInt(key, 0)
    }

    fun savePrefInt(key: String, value: Int){
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

}