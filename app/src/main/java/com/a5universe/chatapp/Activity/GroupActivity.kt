package com.a5universe.chatapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.a5universe.chatapp.databinding.ActivityGroupBinding

class GroupActivity : AppCompatActivity() {

    lateinit var binding: ActivityGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}