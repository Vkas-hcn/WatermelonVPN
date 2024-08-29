package com.jumping.hamster.playing.hockey.watermelon.ui.sta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivitySettingsBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        clickFun()
    }
    private fun initData(){
        binding.linPp.setOnClickListener {
            startActivity(
                Intent(
                    "android.intent.action.VIEW", Uri.parse("https://baidu.com/")
                )
            )
        }
    }
    private fun clickFun() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

}