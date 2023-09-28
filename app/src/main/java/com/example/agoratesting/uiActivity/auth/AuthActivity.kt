package com.example.agoratesting.uiActivity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.agoratesting.databinding.ActivityAuthBinding
import com.example.agoratesting.uiActivity.listmeeting.ListMeetingActivity
import com.example.agoratesting.utils.DataPreference

class AuthActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    private lateinit var username :String
    private lateinit var password :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.isLoading.observe(this){isLoading ->
            if (isLoading){
                binding.authPB.isVisible = true
                binding.joinBtn.isVisible = false
                binding.tvError.isVisible = false

                binding.TILUsername.isVisible = false
                binding.TILPassword.isVisible = false
            }
            else{
                binding.authPB.isVisible = false
                binding.joinBtn.isVisible = true

                binding.TILUsername.isVisible = true
                binding.TILPassword.isVisible = true
                if (viewModel.errorMSG.isNotEmpty()){
                    binding.tvError.isVisible = true
                    binding.tvError.text = viewModel.errorMSG
                }
            }
        }

        viewModel.loggedIn.observe(this){isLoggedIn ->
            if (isLoggedIn){
                startActivity(Intent(this, ListMeetingActivity::class.java))
            }
        }


        binding.joinBtn.setOnClickListener {
            username = binding.TIEUsername.text.toString()
            password = binding.TIEPassword.text.toString()

            viewModel.login(username, password)
        }

    }
}