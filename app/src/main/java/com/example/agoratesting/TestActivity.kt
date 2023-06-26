package com.example.agoratesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agoratesting.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManger = LinearLayoutManager(this)
        binding.rv.layoutManager = layoutManger
        setAdapter()


    }

    private fun setAdapter(){

    }
}