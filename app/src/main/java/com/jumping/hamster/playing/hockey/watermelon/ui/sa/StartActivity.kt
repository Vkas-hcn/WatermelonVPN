package com.jumping.hamster.playing.hockey.watermelon.ui.sa

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.bean.DataJson
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityStartBinding
import com.jumping.hamster.playing.hockey.watermelon.ui.ma.MainActivity
import com.jumping.hamster.playing.hockey.watermelon.ui.sta.SettingActivity
import com.jumping.hamster.playing.hockey.watermelon.utils.DueUserUtils
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        jumpToMain()
        clickFun()
    }
    private fun initData(){
        lifecycleScope.launch(Dispatchers.IO) {
            DataJson.getAllData()
            DueUserUtils.getLoadIp()
            DueUserUtils.getLoadOthIp()
        }
    }
    private fun jumpToMain(){
        lifecycleScope.launch {
            delay(2000)
            navigateTo<MainActivity>(finishCurrent = true)
        }
    }
    private fun clickFun() {
        onBackPressedDispatcher.addCallback(this) {
        }
    }
}