package com.jumping.hamster.playing.hockey.watermelon.ui.la

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.bean.DataJson
import com.jumping.hamster.playing.hockey.watermelon.bean.VpnServerBean
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityMoreBinding
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityStartBinding
import com.jumping.hamster.playing.hockey.watermelon.ui.ma.MainActivity
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.navigateTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    private var moreList:MutableList<VpnServerBean>?=null
    private var moreAdapter: MoreAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        clickFun()
    }
    private fun initAdapter(){
        moreList =  DataJson.getAllData()
        binding.rvVpn.layoutManager = LinearLayoutManager(this)
        moreAdapter = moreList?.let { MoreAdapter(it,this) }
        binding.rvVpn.adapter = moreAdapter
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