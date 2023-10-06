package com.cexup.meet.data

import android.view.SurfaceView

data class AccountInfo(
    var uid : Int,
    var username : String,
    var surfaceView: SurfaceView,
    var offCam : Boolean,
)
