package com.jumping.hamster.playing.hockey.watermelon.utils

import android.content.DialogInterface
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import com.jumping.hamster.playing.hockey.watermelon.ui.ma.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale
import kotlin.system.exitProcess

object DueUserUtils {
    private suspend fun fetchIpFromUrl(urlString: String, onContentFetched: (String) -> Unit) {
        try {
            val url = URL(urlString)
            val inputStream = url.openStream()
            val content = inputStream.bufferedReader().use { it.readText() }
            onContentFetched(content)
        } catch (e: Exception) {
            Log.e("TAG", "fetchIpFromUrl error: ${e.message}")
        }
    }

    suspend fun getLoadIp() = withContext(Dispatchers.IO) {
        fetchIpFromUrl("https://api.infoip.io/") { content ->
            val countryCode = extractCountryCode(content)
            App.localStorage.ip_gsd = countryCode
            Log.e("TAG", "App ip -1:${App.localStorage.ip_gsd} ")
        }
    }

    suspend fun getLoadOthIp() = withContext(Dispatchers.IO) {
        fetchIpFromUrl("https://ipinfo.io/json") { content ->
            val countryCode = extractCountryCodeFromJson(content)
            App.localStorage.ip_gsd_oth = countryCode
            Log.e("TAG", "App ip -2:${App.localStorage.ip_gsd_oth} ")
        }
    }

    private fun extractCountryCode(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)
        return jsonObject.getString("country_short")
    }

    private fun extractCountryCodeFromJson(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)
        return jsonObject.getString("country")

    }

    fun showDueDialog(activity: MainActivity): Boolean {
        if (checkIp()) {
            illegalUserDialog(activity)
            return true
        }
        return false
    }

    private fun checkIp(): Boolean {
        val ipData = fetchIpData()
        return isIllegalIp(ipData)
    }

    private fun fetchIpData(): String {
        return App.localStorage.ip_gsd.ifEmpty {
            App.localStorage.ip_gsd_oth
        }
    }

    private fun isIllegalIp(ipData: String): Boolean {
        return ipData == "IR" || ipData == "CN" || ipData == "HK" || ipData == "MO" || checkLocaleForIllegalIp()
    }

    private fun checkLocaleForIllegalIp(): Boolean {
        val locale = Locale.getDefault()
        val language = locale.language
        return language == "zh" || language == "fa"
    }

    private fun illegalUserDialog(activity: MainActivity) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
            .setTitle("Tip")
            .setMessage("Due to the policy reason, this service is not available in your country")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("confirm") { dialog: DialogInterface?, which: Int ->
                activity.finish()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.setOnKeyListener { _, keyCode, event ->
            return@setOnKeyListener keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
        }
        alertDialog.show()
    }
}