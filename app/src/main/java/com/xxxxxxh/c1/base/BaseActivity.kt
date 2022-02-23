package com.xxxxxxh.c1.base

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sherloki.devkit.ktx.KtxActivity
import com.sherloki.devkit.showOpenAd
import com.xxxxxxh.c1.R
import org.xutils.x

abstract class BaseActivity : KtxActivity() {

    private var isBackground = false

    private var showAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        x.Ext.init(this.application)
        init()
    }

    abstract fun getLayoutId(): Int

    abstract fun init()

    override fun onStop() {
        super.onStop()
        isBackground = isBackground()
    }

    override fun onResume() {
        super.onResume()
        if (isBackground) {
            isBackground = false
            Log.i("xxxxxxH", "showAd")
            val v = this.findViewById<ViewGroup>(android.R.id.content)
            (v.getTag(R.id.open_ad_view_id) as? FrameLayout)?.let {
                showOpenAd(it)
            } ?: kotlin.run {
                val f = FrameLayout(this)
                f.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                v.addView(f)
                v.setTag(R.id.open_ad_view_id, f)
                showOpenAd(f)
            }
        }
    }

    private fun isBackground(): Boolean {
        val activityManager = this
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
            .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == this.packageName) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }
}