package com.example.agoratesting.utils

import android.content.Context
import com.example.agoratesting.data.User

internal class DataPreference(context: Context){

    private val preferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    fun getUser(): User{
        val user = User()
        user.username = preferences.getString("USERNAME", "")
        user.password = preferences.getString("PASSWORD", "")
        return user
    }

    fun saveToken(user: User){
        val editor = preferences.edit()
        editor.putString("USERNAME", user.username)
        editor.putString("PASSWORD", user.password)
    }

    fun deleteToken(){
        preferences.edit().clear().apply()
    }

}