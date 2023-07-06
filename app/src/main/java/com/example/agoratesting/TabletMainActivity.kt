package com.example.agoratesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityTabletMainBinding

class TabletMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabletMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabletMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.tabletVideoRv.layoutManager = layoutManager

        binding.tabletChatBtn.setOnClickListener {
            if (binding.tabletChatScreen.isVisible){
                binding.tabletChatScreen.visibility = View.GONE
            }else{
                binding.tabletChatScreen.visibility = View.VISIBLE
            }
        }
    }
}