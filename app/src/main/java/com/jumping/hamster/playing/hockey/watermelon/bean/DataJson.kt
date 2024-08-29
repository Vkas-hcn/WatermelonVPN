package com.jumping.hamster.playing.hockey.watermelon.bean

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jumping.hamster.playing.hockey.watermelon.R
import com.jumping.hamster.playing.hockey.watermelon.app.App
import java.io.IOException

object DataJson {
    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun getAllData(): MutableList<VpnServerBean>? {
        return try {
            val json = getJsonDataFromAsset(App.appContext, "vpnJson.json")
            val vpnAllListBean = Gson().fromJson<VpnBean>(json, VpnBean::class.java)
            vpnAllListBean?.data?.let { data ->
                if (data.server_list.isEmpty()) {
                    return null
                }
                getBestData()
                val vpnBeatBean =
                    Gson().fromJson(App.localStorage.best_service, VpnServerBean::class.java)
                val filteredServices = data.server_list.toMutableList()
                filteredServices.add(0, vpnBeatBean)
                return filteredServices
            } ?: run {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getBestData() {
        val vpnAllListBean = Gson().fromJson(
            getJsonDataFromAsset(App.appContext, "vpnJson.json"),
            VpnBean::class.java
        )
        if (vpnAllListBean != null && vpnAllListBean.data != null && vpnAllListBean.data.smart_list.isNotEmpty()) {
            val vpnBean: VpnServerBean = vpnAllListBean.data.smart_list.random()
            vpnBean.bestState = true
            vpnBean.country_name = "Smart"
            App.localStorage.best_service = Gson().toJson(vpnBean)
        }
        try {
            val vpnNowBean =
                Gson().fromJson(App.localStorage.now_service, VpnServerBean::class.java)
            if (vpnNowBean.bestState) {
                App.localStorage.now_service = App.localStorage.best_service
            }
        } catch (_: Exception) {

        }

    }

    fun String.getWaterImage(): Int {
        return when (this) {
            "United States" -> R.drawable.unitedstates
            "Australia" -> R.drawable.australia
            "Canada" -> R.drawable.canada
            "China" -> R.drawable.canada
            "France" -> R.drawable.france
            "Germany" -> R.drawable.germany
            "Hong Kong" -> R.drawable.hongkong
            "India" -> R.drawable.india
            "Japan" -> R.drawable.japan
            "koreasouth" -> R.drawable.koreasouth
            "Singapore" -> R.drawable.singapore
            "Brazil" -> R.drawable.brazil
            "United Kingdom" -> R.drawable.unitedkingdom
            "Ireland" -> R.drawable.ireland
            "Netherlands" -> R.drawable.netherlands
            else -> R.drawable.icon_smart
        }
    }

    const val llJJ = """
    {
        "code": 200,
        "msg": "获取服务器列表成功",
        "data": {
            "smart_list": [
                {
                    "mode": "ss",
                    "ip": "1.1.1.1",
                    "port": 2,
                    "user_name": "21211",
                    "user_pwd": "bbbbb",
                    "encrypt": "bbbbb",
                    "city": "Los Angeles",
                    "country_name": "United States",
                    "country_code": "US"
                }
            ],
            "server_list": [
                {
                    "mode": "ss",
                    "ip": "1.1.1.1",
                    "port": 3,
                    "user_name": "21211",
                    "user_pwd": "bbbbb",
                    "encrypt": "bbbbb",
                    "city": "Los Angeles",
                    "country_name": "United States",
                    "country_code": "US"
                },
                {
                    "mode": "ss",
                    "ip": "4.4.4.4",
                    "port": 2,
                    "user_name": "21211",
                    "user_pwd": "bbbbb",
                    "encrypt": "bbbbb",
                    "city": "Singapore",
                    "country_name": "Singapore",
                    "country_code": "SG"
                }
            ]
        }
    }
"""

}