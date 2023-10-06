package com.cexup.meet.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username : String? = null,
    var password : String? = null,
) : Parcelable
