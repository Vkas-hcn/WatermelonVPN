package de.blinkt.openvpn.core

import android.content.Context
import android.net.VpnService
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object Raoliu {
    private fun saveData(data: String) {
        try {
            val file = File(openContext.filesDir, "watermelon.json")
            FileWriter(file).use { it.write(data) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    lateinit var openContext: Context
    fun initOpenContext(context: Context) {
        openContext = context
    }

    private fun loadData(): String? {
        return try {
            val file = File(openContext.filesDir, "watermelon.json")
            if (file.exists()) {
                FileReader(file).use { it.readText() }
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getSpeedData(upData: String, downData: String, statistics: String) {
        try {
            loadData()
            val jsonData = loadData()
            val bean = Gson().fromJson(jsonData, AppData::class.java)
            bean.water_down_v = downData
            bean.water_up_v = upData
            saveData(Gson().toJson(bean))
        } catch (e: Exception) {

        }
    }

//    private fun getFlowData(): Boolean? {
//        val num =  loadData()?.let {
//            getOnlineControlKeyCore(it)
//        }
//        Log.e("TAG", "是否扰流-open: ${num}")
//        return num
//    }
//    private fun getOnlineControlKeyCore(jsonString: String): Boolean? {
//        return try {
//            val jsonObject = JSONObject(jsonString)
//            jsonObject.getBoolean("water_control_key")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    fun brand(builder: VpnService.Builder, myPackageName: String) {
//        if(getFlowData()==true){
//            (listOf(myPackageName) + listGmsPackages())
//                .iterator()
//                .forEachRemaining {
//                    runCatching { builder.addDisallowedApplication(it) }
//                }
//        }
    }
//    private fun listGmsPackages(): List<String> {
//        return listOf(
//            "com.google.android.gms",
//            "com.google.android.ext.services",
//            "com.google.process.gservices",
//            "com.android.vending",
//            "com.google.android.gms.persistent",
//            "com.google.android.cellbroadcastservice",
//            "com.google.android.packageinstaller",
//            "com.google.android.gms.location.history",
//        )
//    }
}