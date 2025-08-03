package com.sopnolikhi.booksmyfriend.design.ui.activities.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sopnolikhi.booksmyfriend.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}