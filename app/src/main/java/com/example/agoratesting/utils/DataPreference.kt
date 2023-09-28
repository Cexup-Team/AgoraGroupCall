package com.example.agoratesting.utils

import android.content.Context
import com.example.agoratesting.data.User

internal class DataPreference(context: Context){

    private val preferences = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)

    fun getResolution():String?{
        return preferences.getString("Resolution", "")
    }
    fun saveResolution(Res : String){
        val editor = preferences.edit()
        editor.putString("Resolution", Res)
        editor.apply()
    }

    fun getPrefBG():String?{
        return preferences.getString("PrefBG", "")
    }
    fun savePrefBG(Res : String){
        val editor = preferences.edit()
        editor.putString("PrefBG", Res)
        editor.apply()
    }

    fun getColorBG():Int{
        return preferences.getInt("ColorBG", 0x000000)
    }
    fun saveColorBG(color : Int){
        val editor = preferences.edit()
        editor.putInt("ColorBG", color)
        editor.apply()
    }

}