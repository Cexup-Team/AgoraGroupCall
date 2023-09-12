package com.example.agoratesting.data

import android.view.SurfaceView

data class AccountInfo(
    var uid : Int,
    var username : String,
    var surfaceView: SurfaceView,
    var offCam : Boolean,
)
