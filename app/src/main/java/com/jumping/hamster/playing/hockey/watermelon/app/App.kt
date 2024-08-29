package com.jumping.hamster.playing.hockey.watermelon.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.jumping.hamster.playing.hockey.watermelon.utils.LocalStorage
import android.os.Process
import de.blinkt.openvpn.core.Raoliu

class App : Application() {
    companion object {
        var vpnState = 0
        var clickState: Int = -1
        lateinit var appContext: Context
        lateinit var localStorage: LocalStorage
        var jumpConnect = false
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        localStorage = LocalStorage()
        Raoliu.initOpenContext(this)
        if (!isMainProcess(this)) {
        }
    }

    private fun isMainProcess(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myPid = Process.myPid()
        val packageName = context.packageName

        val runningAppProcesses = activityManager.runningAppProcesses ?: return false

        for (processInfo in runningAppProcesses) {
            if (processInfo.pid == myPid && processInfo.processName == packageName) {
                return true
            }
        }
        return false
    }
}