package com.cexup.meet.uiActivity.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cexup.meet.databinding.ActivitySettingBinding
import com.cexup.meet.utils.DataPreference
import com.cexup.meet.utils.SDKManager
import com.cexup.meet.utils.TempMeeting
import com.github.dhaval2404.imagepicker.ImagePicker
import io.agora.rtc2.Constants
import io.agora.rtc2.video.SegmentationProperty
import io.agora.rtc2.video.VirtualBackgroundSource

class SettingActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySettingBinding
    private lateinit var pref : DataPreference

    private var rtcEngine = SDKManager.rtcEngine
    private var isEnabled = false
    private var bgSource = VirtualBackgroundSource()
    private var segmentationProperty = SegmentationProperty()
    private var bgColorDefault = 0x000000
    private val resOption = arrayOf("Auto", "High", "Medium", "Audio Only")
//    private val bgOption = arrayOf("Off", "Blur", "Color", "Image")
    private val bgOption = arrayOf("Off", "Blur", "Color")
    private val colorOption = arrayOf("Black", "White", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta")

    private  var selectedImg : String ? = null

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data!!
            selectedImg = data.data!!.path
            Log.e("selectedImgURI", selectedImg.toString())
            setVirtualBackGround()
            return@registerForActivityResult
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = DataPreference(this)
        bgColorDefault = pref.getPrefInt("ColorBG")

        checkPreviewColor()

        segmentationProperty = SegmentationProperty()
        segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI
        segmentationProperty.greenCapacity = 0.5f

        val resArrayAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            resOption
        )

        resArrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerRes.adapter = resArrayAdapter
        binding.spinnerRes.setSelection(resArrayAdapter.getPosition(pref.getPrefString("Resolution")))

        val bgArrayAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            bgOption
        )

        bgArrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerBackground.adapter = bgArrayAdapter
        binding.spinnerBackground.setSelection(bgArrayAdapter.getPosition(pref.getPrefString("PrefBG")))

        binding.spinnerRes.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(resOption[position]){
                    "High" -> {
                        rtcEngine?.muteAllRemoteVideoStreams(false)
                        for(i in 0 until TempMeeting.ListMember.size){
                            rtcEngine?.setRemoteVideoStreamType(TempMeeting.ListMember[i].uid, Constants.VIDEO_STREAM_HIGH)
                        }
                    }
                    "Medium" -> {
                        rtcEngine?.muteAllRemoteVideoStreams(false)
                        for(i in 0 until TempMeeting.ListMember.size){
                            rtcEngine?.setRemoteVideoStreamType(TempMeeting.ListMember[i].uid, Constants.VIDEO_STREAM_LOW)
                        }
                    }

                    "Audio Only" -> {
                        rtcEngine?.muteAllRemoteVideoStreams(true)
                    }
                }

                pref.savePrefString("Resolution", resOption[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do Nothing
            }

        }

        binding.spinnerBackground.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (bgOption[position]){
                    "Off" -> {
                        binding.backgroundColorSetting.isVisible = false
                        binding.backgroundImgSetting.isVisible = false
                        binding.localPreview.isVisible = false
                        isEnabled = false
                    }
                    "Blur" -> {
                        binding.backgroundColorSetting.isVisible = false
                        binding.backgroundImgSetting.isVisible = false
                        binding.localPreview.isVisible = true
                        isEnabled = true
                        bgSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_BLUR
                        bgSource.blurDegree = VirtualBackgroundSource.BLUR_DEGREE_HIGH
                    }
                    "Color" -> {
                        binding.backgroundColorSetting.isVisible = true
                        binding.backgroundImgSetting.isVisible = false
                        binding.localPreview.isVisible = true
                        isEnabled = true
                        bgSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_COLOR
                        bgSource.color = bgColorDefault
                    }

                    "Image" ->{
                        binding.backgroundImgSetting.isVisible = true
                        binding.backgroundColorSetting.isVisible = false
                        binding.localPreview.isVisible = true
                        isEnabled = true
                        bgSource.backgroundSourceType = VirtualBackgroundSource.BACKGROUND_IMG
                        bgSource.source = selectedImg
                        Log.e("selectedImgURI", selectedImg.toString())
                    }
                }

                pref.savePrefString("PrefBG", bgOption[position])
                setVirtualBackGround()
                setLocalPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do Nothing
            }

        }

        binding.btnPreviewColor.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Pick Background Color")
                .setNegativeButton("Cancel"){ dialog : DialogInterface, which : Int ->
                    dialog.cancel()
                }
                .setItems(colorOption){dialog : DialogInterface, which : Int ->
                    when (colorOption[which]){
                        "Black" -> bgColorDefault = 0x000000
                        "White" -> bgColorDefault = 0xFFFFFF
                        "Red" -> bgColorDefault = 0xFF0000
                        "Green" -> bgColorDefault = 0x00FF00
                        "Blue" -> bgColorDefault = 0x0000FF
                        "Yellow" -> bgColorDefault = 0xFFFF00
                        "Cyan" -> bgColorDefault = 0x00FFFF
                        "Magenta" -> bgColorDefault = 0xFF00FF
                    }
                    pref.savePrefInt("ColorBG", bgColorDefault)
                    bgSource.color = bgColorDefault

                    checkPreviewColor()
                    setVirtualBackGround()

                }
                .create()
                .show()
        }

        binding.btnPickImg.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).createIntent {
                launcherIntentGallery.launch(it)
            }
        }
    }

    private fun setLocalPreview() {
        if (isEnabled){
            val surface  = TempMeeting.ListMember[0].surfaceView
            if (surface.parent != null){
                (surface.parent as ViewGroup).removeView(surface)
            }
            binding.localPreview.addView(surface)
        } else{
            binding.localPreview.removeAllViews()
        }
    }

    private fun setVirtualBackGround() {
        rtcEngine?.enableVirtualBackground(
            isEnabled,
            bgSource,
            segmentationProperty
        )
    }

    private fun checkPreviewColor(){
        when(bgColorDefault){
            0x000000 -> binding.btnPreviewColor.setBackgroundColor(Color.BLACK)
            0xFFFFFF -> binding.btnPreviewColor.setBackgroundColor(Color.WHITE)
            0xFF0000 -> binding.btnPreviewColor.setBackgroundColor(Color.RED)
            0x00FF00 -> binding.btnPreviewColor.setBackgroundColor(Color.GREEN)
            0x0000FF -> binding.btnPreviewColor.setBackgroundColor(Color.BLUE)
            0xFFFF00 -> binding.btnPreviewColor.setBackgroundColor(Color.YELLOW)
            0x00FFFF -> binding.btnPreviewColor.setBackgroundColor(Color.CYAN)
            0xFF00FF -> binding.btnPreviewColor.setBackgroundColor(Color.MAGENTA)
        }
    }
}