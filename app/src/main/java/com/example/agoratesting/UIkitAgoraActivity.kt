package com.example.agoratesting

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraSettings
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.agorauikit_android.requestPermission
import io.agora.rtc2.Constants

private const val PERMISSION_REQ_ID = 22
private val REQUESTED_PERMISSIONS = arrayOf<String>(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

@OptIn(ExperimentalUnsignedTypes::class)
class UIkitAgoraActivity : AppCompatActivity() {
    private var agView: AgoraVideoViewer? = null
    private val appId: String = "4300d680db99440ea74c274f82d30c59"
    private val channelName: String = "CexupTesting"
    private val token: String =
        "007eJxTYFDkcglbq101K/9V73dXx/Ldu6W51DUvOe9qMXBsVBHrMFVgMDE2MEgxszBISbK0NDExSE00N0k2MjdJszBKMTZINrVcNzckpSGQkeGNuA8rIwMEgvg8DM6pFaUFIanFJZl56QwMADcIHxM="

    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                REQUESTED_PERMISSIONS[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    private fun settingWithExtraButtons(): AgoraSettings {
        val agoraSettings = AgoraSettings()
        val agBeautyButton = AgoraButton(this)
        agBeautyButton.clickAction = {
            it.isSelected = !it.isSelected
            agBeautyButton.setImageResource(
                if (it.isSelected) android.R.drawable.star_on else android.R.drawable.star_off
            )
            it.background.setTint(if (it.isSelected) Color.GREEN else Color.GRAY)
            this.agView?.agkit?.setBeautyEffectOptions(it.isSelected, this.agView?.beautyOptions)
        }
        agBeautyButton.setImageResource(android.R.drawable.star_off)
        agoraSettings.extraButtons = mutableListOf(agBeautyButton)
        return agoraSettings
    }

    private fun initializedAndJoinChannel() {
        try {
            agView = AgoraVideoViewer(
                this, AgoraConnectionData(appId, token),
                agoraSettings = settingWithExtraButtons()
            )
        } catch (e: Exception) {
            Log.e("AgoraVideoViewer", "Could not initialize AgoraVideoViewer")
            Log.e("Exception", e.toString())
            return
        }

        this.addContentView(
            agView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )


        if (AgoraVideoViewer.requestPermission(this)) {
            joinChannel()
        } else {
            val joinButton: Button = Button(this)
            joinButton.text = "Allow camera and microphone access,then click here"
            joinButton.setOnClickListener {
                if (AgoraVideoViewer.requestPermission(this)) {
                    (joinButton.parent as ViewGroup).removeView(joinButton)
                    joinChannel()
                }
            }
            joinButton.setBackgroundColor(Color.GREEN)
            joinButton.setTextColor(Color.RED)
            this.addContentView(
                joinButton,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    200
                )
            )
        }
    }

    private fun joinChannel() {
        agView!!.join(channelName, token, Constants.CLIENT_ROLE_BROADCASTER, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uikit_agora)
        if (!AgoraVideoViewer.requestPermission(this)) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
        }
        initializedAndJoinChannel()
    }
}