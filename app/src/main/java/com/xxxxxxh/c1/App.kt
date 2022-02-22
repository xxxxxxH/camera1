package com.xxxxxxh.c1

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.sherloki.devkit.ktx.Ktx
import com.xxxxxxh.c1.utils.ForegroundCallbacks


class App : MultiDexApplication() {
    companion object{
        var instance:App?=null
            private set
    }

    var listenre:ForegroundCallbacks.Listener?=null
        set

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Ktx.initialize(this)
    }

    override fun onCreate() {
        super.onCreate()
        Ktx.getInstance().initStartUp()
        instance = this
        val curProcess = getProcessName(this, Process.myPid())

        if (!TextUtils.equals(curProcess, "com.xxxxxxh.c1")) {
            return
        }
        initAppStatusListener()
    }

    private fun initAppStatusListener() {
        ForegroundCallbacks.init(this).addListener(listenre)
    }

    private fun getProcessName(cxt: Context, pid: Int): String? {
        val am: ActivityManager = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
            ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }
}