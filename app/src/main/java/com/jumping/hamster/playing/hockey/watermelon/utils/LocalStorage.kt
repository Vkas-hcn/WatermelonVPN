package com.jumping.hamster.playing.hockey.watermelon.utils

import android.content.Context
import androidx.annotation.Keep
import java.io.File
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jumping.hamster.playing.hockey.watermelon.app.App
import java.io.FileReader
import java.io.FileWriter

@Keep
data class AppData(
    var vpn_online_data_dualLoad: String = "",

    var locak_up: Boolean = false,
    var ip_lo_dualLoad: String = "",
    var ip_gsd: String = "",
    var ip_gsd_oth: String = "",


    var water_down_v: String = "",
    var water_down_u: String = "",
    var water_up_v: String = "",
    var water_up_u: String = "",

    var best_service: String = "",
    var click_service: String = "",
    var now_service: String = "",

    var isCloneGuide:Boolean = false,

    var pingValue:String = ""
    )

class LocalStorage() {
    private val fileStorageManager = FileStorageManager(App.appContext)
    private val gson: Gson = GsonBuilder().create()
    private var appData: AppData


    init {
        val dataString = fileStorageManager.loadData()
        appData = try {
            if (dataString != null) {
                gson.fromJson(dataString, AppData::class.java)
            } else {
                AppData()
            }
        } catch (e: Exception) {
            AppData()
        }
    }
    var isCloneGuide: Boolean
        get() = appData.isCloneGuide
        set(value) {
            appData.isCloneGuide = value
            writeToFile(appData)
        }
    var pingValue: String
        get() = appData.pingValue
        set(value) {
            appData.pingValue = value
            writeToFile(appData)
        }
    var now_service: String
        get() = appData.now_service
        set(value) {
            appData.now_service = value
            writeToFile(appData)
        }
    var click_service: String
        get() = appData.click_service
        set(value) {
            appData.click_service = value
            writeToFile(appData)
        }
    var best_service: String
        get() = appData.best_service
        set(value) {
            appData.best_service = value
            writeToFile(appData)
        }
    var ip_gsd: String
        get() = appData.ip_gsd
        set(value) {
            appData.ip_gsd = value
            writeToFile(appData)
        }
    var ip_gsd_oth: String
        get() = appData.ip_gsd_oth
        set(value) {
            appData.ip_gsd_oth = value
            writeToFile(appData)
        }
    var water_down_v: String
        get() = appData.water_down_v
        set(value) {
            appData.water_down_v = value
            writeToFile(appData)
        }
    var water_down_u: String
        get() = appData.water_down_u
        set(value) {
            appData.water_down_u = value
            writeToFile(appData)
        }
    var water_up_v: String
        get() = appData.water_up_v
        set(value) {
            appData.water_up_v = value
            writeToFile(appData)
        }
    var water_up_u: String
        get() = appData.water_up_u
        set(value) {
            appData.water_up_u = value
            writeToFile(appData)
        }

    private fun writeToFile(data: AppData) {
        val dataString = gson.toJson(data)
        fileStorageManager.saveData(dataString)
    }
}