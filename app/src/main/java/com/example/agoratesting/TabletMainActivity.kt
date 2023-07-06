package com.example.agoratesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityTabletMainBinding

class TabletMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabletMainBinding

    //main speaker atau sharescreen
    //lebih enak pke viewmodel trus di observe
    private var isMainFocus :Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabletMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //observe trus panggil func
        checkMainFocus(isMainFocus)
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

    private fun checkMainFocus(focused: Boolean) {
        if (focused){
            //rv dengan grid layouut
            val gridLayoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            binding.tabletVideoRv.layoutManager = gridLayoutManager
        }
        else{
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.tabletVideoRv.layoutManager = linearLayoutManager
        }

    }
}