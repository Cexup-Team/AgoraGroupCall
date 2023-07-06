package com.example.agoratesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
<<<<<<< HEAD
import androidx.recyclerview.widget.GridLayoutManager
=======
>>>>>>> origin/WIP_tablet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityTabletMainBinding

class TabletMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabletMainBinding
<<<<<<< HEAD

    //main speaker atau sharescreen
    //lebih enak pke viewmodel trus di observe
    private var isMainFocus :Boolean = false
=======
>>>>>>> origin/WIP_tablet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabletMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        //observe trus panggil func
        checkMainFocus(isMainFocus)
=======
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.tabletVideoRv.layoutManager = layoutManager
>>>>>>> origin/WIP_tablet

        binding.tabletChatBtn.setOnClickListener {
            if (binding.tabletChatScreen.isVisible){
                binding.tabletChatScreen.visibility = View.GONE
            }else{
                binding.tabletChatScreen.visibility = View.VISIBLE
            }
        }
    }
<<<<<<< HEAD

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
=======
>>>>>>> origin/WIP_tablet
}