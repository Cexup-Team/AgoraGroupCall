package com.example.agoratesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.example.agoratesting.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    var x : Double? = 0.0
    var y : Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fragmentContainerFloatVid.setOnTouchListener { view, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    x = view.x.toDouble() - event.rawX
                    y = view.y.toDouble() - event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {

                    // animate the image on x axis line only according to your finger movements
                    binding.fragmentContainerFloatVid.animate()
                        .x(event.rawX + x!!.toFloat())
                        .y(event.rawY + y!!.toFloat())
                        .setDuration(0)
                        .start()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }
}