package com.jumping.hamster.playing.hockey.watermelon.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import com.jumping.hamster.playing.hockey.watermelon.ui.ma.MainActivity
import kotlin.system.exitProcess
import java.io.BufferedReader
import java.io.InputStreamReader
object WaterYTils {
    inline fun <reified T : Activity> Activity.navigateTo(
        finishCurrent: Boolean = false,
        extras: Intent.() -> Unit = {}
    ) {
        val intent = Intent(this, T::class.java)
        intent.extras()
        startActivity(intent)
        if (finishCurrent) {
            finish()
        }
    }

    fun isNetworkConnected(activity: MainActivity): Boolean {
        val mConnectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return !mNetworkInfo.isAvailable
        }
        noInternetDialog(activity)
        return true
    }

    private fun noInternetDialog(activity: MainActivity) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
            .setTitle("Tip")
            .setMessage("No network available. Please check your connection.")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.setOnKeyListener { _, keyCode, event ->
            return@setOnKeyListener keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
        }
        alertDialog.show()
    }

    fun splitNumberAndUnit(input: String): Pair<String, String> {
        try {
            val regex = """(\d+)\s*([a-zA-Z]+/[a-zA-Z]+)""".toRegex()
            val matchResult = regex.find(input)
            return if (matchResult != null) {
                val (number, unit) = matchResult.destructured
                number to unit
            } else {
                "0" to "bit/s"
            }
        } catch (e: Exception) {
            return "0" to "bit/s"
        }
    }




    fun pingIPAddress(ip: String): String? {
        val command = if (System.getProperty("os.name").startsWith("Windows")) {
            "ping -n 1 $ip"
        } else {
            "ping -c 1 $ip"
        }

        return try {
            val process = ProcessBuilder(*command.split(" ").toTypedArray()).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }
            val pingTime = if (System.getProperty("os.name").startsWith("Windows")) {
                """时间=(\d+)ms""".toRegex().find(output)?.groups?.get(1)?.value
            } else {
                """time=(\d+) ms""".toRegex().find(output)?.groups?.get(1)?.value
            }

            pingTime ?: "time out"
        } catch (e: Exception) {
            "time out"
        }
    }

    fun mainPing(ipAddress:String) {
        val pingValue = pingIPAddress(ipAddress)
        Log.e("TAG", "Ping值: $pingValue")
        App.localStorage.pingValue =  if(pingValue==null){
            "Ping Time out"
        }else{
            "Ping ${pingValue}ms"
        }
    }

}