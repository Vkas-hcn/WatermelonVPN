package com.jumping.hamster.playing.hockey.watermelon.ui.ma

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import com.jumping.hamster.playing.hockey.watermelon.bean.DataJson.getWaterImage
import com.jumping.hamster.playing.hockey.watermelon.bean.VpnServerBean
import com.jumping.hamster.playing.hockey.watermelon.databinding.ActivityMainBinding
import com.jumping.hamster.playing.hockey.watermelon.ui.ea.EndActivity
import com.jumping.hamster.playing.hockey.watermelon.ui.la.MoreActivity
import com.jumping.hamster.playing.hockey.watermelon.ui.sta.SettingActivity
import com.jumping.hamster.playing.hockey.watermelon.utils.AppData
import com.jumping.hamster.playing.hockey.watermelon.utils.DueUserUtils
import com.jumping.hamster.playing.hockey.watermelon.utils.FileStorageManager
import com.jumping.hamster.playing.hockey.watermelon.utils.GlobalTimer
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.navigateTo
import com.jumping.hamster.playing.hockey.watermelon.utils.WaterYTils.splitNumberAndUnit
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var mService: IOpenVPNAPIService? = null
    val animator = ValueAnimator.ofInt(0, 100)
    private lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>
    private var speedJob: Job? = null
    private lateinit var tvUpValue: TextView
    private var stopConnect = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
        activeGuide(true)
        clickFun()
        backFun()
        setAnimator()
        if (DueUserUtils.showDueDialog(this)) {
            return
        }
        if (WaterYTils.isNetworkConnected(this)) {
        }
    }

    private fun initData() {
        requestPermissionForResultVPN =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
        bindService(
            Intent(this, ExternalOpenVPNService::class.java),
            mConnection,
            BIND_AUTO_CREATE
        )
        GlobalTimer.setOnTimeUpdateListener { formattedTime ->
            binding.tvTimes.text = formattedTime
        }
    }


    private fun activeGuide(cloneState: Boolean) {
        if ((cloneState && !App.localStorage.isCloneGuide) && App.vpnState != 2) {
            binding.showVpnGuide = true
        } else {
            binding.showVpnGuide = false
            App.localStorage.isCloneGuide = true
        }
    }

    private fun setAnimator() {
        animator.duration = 20000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            binding.pbConnect.progress = progress
            binding.tvJd.text = "$progress%"
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.e("TAG", "onAnimationEnd: ")
                binding.tvJd.text = if (App.vpnState == 2) {
                    "connected"
                } else {
                    "connect"
                }
                if (App.clickState == 0 && App.vpnState != 2) {
                    mService?.disconnect()
                    Toast.makeText(this@MainActivity, "The connection failed", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
            }
        })
    }

    private fun clickFun() {
        tvUpValue = findViewById(R.id.tv_up_value)
        binding.viewGuide.setOnClickListener {  }
        binding.llConnectMain.setOnClickListener {
            activeTONextVpn()
        }
        binding.lavGuide.setOnClickListener {
            activeTONextVpn()
        }
        binding.imgSettingMain.setOnClickListener {
            if (isConnecting()) {
                Toast.makeText(this@MainActivity, "Connecting... Please wait", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (isDisConnecting()) {
                stopConnect = true
                binding.showVpnState = 2
                animator.cancel()
            }
            navigateTo<SettingActivity>()

        }
        binding.linService.setOnClickListener {
            if (isConnecting()) {
                Toast.makeText(this@MainActivity, "Connecting... Please wait", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (isDisConnecting()) {
                stopConnect = true
                binding.showVpnState = 2
                animator.cancel()
            }
            navigateTo<MoreActivity>()
        }
    }

    private fun activeTONextVpn() {
        activeGuide(false)
        if (WaterYTils.isNetworkConnected(this)) {
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            DueUserUtils.getLoadIp()
            DueUserUtils.getLoadOthIp()
        }
        if (DueUserUtils.showDueDialog(this)) {
            return
        }
        if (checkVPNPermission()) {
            startCountdown()
        } else {
            VpnService.prepare(this).let {
                requestPermissionForResultVPN.launch(it)
            }
        }
    }

    private fun backFun() {
        onBackPressedDispatcher.addCallback(this) {
            if (binding?.showVpnGuide == true) {
                stopConnect = true
                activeGuide(false)
                return@addCallback
            }
            if (isConnecting()) {
                Toast.makeText(this@MainActivity, "Connecting... Please wait", Toast.LENGTH_SHORT)
                    .show()
                return@addCallback
            }
            if (isDisConnecting()) {
                stopConnect = true
                binding.showVpnState = 2
                animator.cancel()
                return@addCallback
            }
            finish()
            exitProcess(0)
        }
    }

    private fun startCountdown() {
        Log.e("TAG", "startCountdown: start-1")

        if (binding.showVpnState == 1) {
            return
        }
        activeGuide(false)
        binding.showVpnState = 1
        App.clickState = App.vpnState
        stopConnect = false
        Log.e("TAG", "startCountdown: start")
        animator.start()
        startActiveVpn()
    }

    fun connectEndPage() {
        if (isConnecting()) {
            navigateTo<EndActivity>()
        }
    }

    fun disConnectEndPage() {
        if (isDisConnecting()) {
            navigateTo<EndActivity>()
        }
    }

    private fun isConnecting(): Boolean {
        return App.clickState == 0 && binding.showVpnState == 1
    }

    fun isDisConnecting(): Boolean {
        return App.clickState == 2 && binding.showVpnState == 1
    }

    private fun startActiveVpn() {
        lifecycleScope.launch {
            delay(2000)
            Log.e(
                "TAG",
                "startActiveVpn: App.localStorage.clickState=${App.clickState}=$stopConnect"
            )
            if (stopConnect) {
                return@launch
            }
            if (App.vpnState == 0) {
                openVTool()
            } else {
                mService?.disconnect()
            }
        }
    }

    private fun checkVPNPermission(): Boolean {
        VpnService.prepare(this).let {
            return it == null
        }
    }

    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            startCountdown()
        } else {
            Toast.makeText(this, "Please give permission to continue", Toast.LENGTH_SHORT).show()
        }
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                mService?.registerStatusCallback(mCallback)
            } catch (e: Exception) {
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mService = null
        }
    }
    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            Log.e("TAG", "stateChanged: $state")
            lifecycleScope.launch(Dispatchers.Main) {
                when (state) {
                    "CONNECTED" -> {
                        activeGuide(false)
                        connectEndPage()
                        binding.showVpnState = 2
                        App.vpnState = 2
                        binding.tvJd.text = "connected"
                        GlobalTimer.startTimer()
                        animator.end()
                        showVpnSpeed()
                    }

                    "CONNECTING" -> {
                    }

                    "RECONNECTING" -> {
                        binding.showVpnState = 0
                        App.vpnState = 0
                        binding.tvJd.text = "connect"

                        GlobalTimer.stopTimer()
                    }

                    "NOPROCESS" -> {
                        if (App.clickState >= 0) {
                            disConnectEndPage()
                            binding.showVpnState = 0
                            App.vpnState = 0
                            App.clickState =-1
                            binding.tvJd.text = "connect"
                            animator.end()
                            GlobalTimer.stopTimer()
                            speedJob?.cancel()
                        }
                    }

                    else -> {}
                }
                Log.e(
                    "TAG",
                    "newStatus: App.vpnState=${App.vpnState}    App.localStorage.clickState=${App.clickState}"
                )
            }
        }
    }

    private fun openVTool() {
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val vpnNowBean =
                    Gson().fromJson(App.localStorage.now_service, VpnServerBean::class.java)
                val conf = this@MainActivity.assets.open("fast_ippooltest.ovpn")
                val br = BufferedReader(InputStreamReader(conf))
                val config = StringBuilder()
                var line: String?
                while (true) {
                    line = br.readLine()
                    if (line == null) break
                    if (line.contains("remote 195", true)) {
                        line = "remote ${vpnNowBean.ip} ${vpnNowBean.port}"
                    }
                    config.append(line).append("\n")
                }
                Log.e("TAG", "openVTool: $config")
                br.close()
                conf.close()
                mService?.startVPN(config.toString())
                WaterYTils.mainPing(vpnNowBean.ip)
            }.onFailure {
            }
        }
    }

    fun showVpnSpeed() {
        val fileStorageManager = FileStorageManager(this)
        speedJob?.cancel()
        speedJob = lifecycleScope.launch {
            while (isActive) {
                try {
                    val data = fileStorageManager.loadData()
                    val bean = data?.let {
                        Gson().fromJson(it, AppData::class.java)
                    }
                    val (numberDown, unitDown) = splitNumberAndUnit(bean?.water_down_v!!)
                    val (numberUp, unitUp) = splitNumberAndUnit(bean.water_up_v)
                    binding.tvDownValue.text = numberDown
                    binding.tvDownUn.text = unitDown
                    tvUpValue.text = numberUp
                    binding.tvUpUn.text = unitUp
                } catch (_: Exception) {

                }
                delay(500)
            }
        }
    }

    private fun showMainInformation() {
        if (App.localStorage.now_service.isBlank()) {
            App.localStorage.now_service = App.localStorage.best_service
            binding.mainFlag.setImageResource(R.drawable.icon_smart)
            binding.tvSmart.text = "Smart"
            return
        }
        try {
            val vpnNowBean =
                Gson().fromJson(App.localStorage.now_service, VpnServerBean::class.java)
            Log.e(
                "TAG",
                "当前连接服务器:${"${vpnNowBean.country_name}-${vpnNowBean.city}"}-${vpnNowBean.ip}",
            )
            binding.mainFlag.setImageResource(vpnNowBean.country_name.getWaterImage())
            binding.tvSmart.text = if (vpnNowBean.bestState) {
                "Smart"
            } else {
                "${vpnNowBean.country_name}-${vpnNowBean.city}"
            }
        } catch (e: Exception) {
            binding.mainFlag.setImageResource(R.drawable.icon_smart)
            binding.tvSmart.text = "Smart"
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.vpnState != 2) {
            binding.tvDownValue.text = "0"
            binding.tvDownUn.text = "bit/s"
            binding.tvUpValue.text = "0"
            binding.tvUpUn.text = "bit/s"
        } else {
            GlobalTimer.setOnTimeUpdateListener { formattedTime ->
                binding.tvTimes.text = formattedTime
            }
        }
        showMainInformation()
        if (App.jumpConnect) {
            activeTONextVpn()
            App.jumpConnect = false
        }
    }

    override fun onStop() {
        super.onStop()
        if (isConnecting()) {
            stopConnect = true
            binding.showVpnState = 0
            App.clickState = -1
            animator.cancel()
            mService?.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.clickState = -1
        App.localStorage.isCloneGuide = false
        mService?.unregisterStatusCallback(mCallback)
        animator.cancel()
        speedJob?.cancel()
        GlobalTimer.stopTimer()
    }
}