package com.jumping.hamster.playing.hockey.watermelon.ui.ea

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityEndBinding
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityMainBinding
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityStartBinding
import com.jumping.hamster.playing.hockey.watermelon.ui.ma.MainActivity
import com.jumping.hamster.playing.hockey.watermelon.utils.AppData
import com.jumping.hamster.playing.hockey.watermelon.utils.FileStorageManager
import com.jumping.hamster.playing.hockey.watermelon.utils.GlobalTimer
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.navigateTo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EndActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEndBinding.inflate(layoutInflater) }
    private var speedJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
        clickFun()
    }

    private fun initData() {
        binding.atvDown.isVisible = App.vpnState == 2
        binding.atvUp.isVisible = App.vpnState == 2
        binding.viewDis.isVisible =  App.vpnState != 2
        binding.tvPing.isVisible = App.vpnState == 2
        binding.tvTimes.isVisible = App.vpnState == 2
        if (App.vpnState == 2) {
            binding.tvState.text = "Connection successful!"
            GlobalTimer.setOnTimeUpdateListener { formattedTime ->
                binding.tvTimes.text = formattedTime
            }
            binding.tvPing.text = App.localStorage.pingValue
            showVpnSpeed()
        } else {
            binding.tvState.text = "DisConnection successful!"
            binding.tvTimes.text = "00:00:00"
        }
        if (App.localStorage.click_service.isNotBlank()) {
            App.localStorage.now_service = App.localStorage.click_service
            App.localStorage.click_service = ""
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
    private fun showVpnSpeed() {
        val fileStorageManager = FileStorageManager(this)
        speedJob?.cancel()
        speedJob = lifecycleScope.launch {
            while (isActive) {
                val data = fileStorageManager.loadData()
                val bean = data?.let {
                    Gson().fromJson(it, AppData::class.java)
                }
                binding.atvDown.text = bean?.water_down_v ?: "0/kb"
                binding.atvUp.text = bean?.water_up_v ?: "0/kb"

                delay(500)
            }
        }
    }
}