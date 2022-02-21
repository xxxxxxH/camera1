package com.xxxxxxh.c1.ui

import android.annotation.SuppressLint
import android.content.Intent
import com.sherloki.devkit.showOpenAd
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun init() {
        showOpenAd(activitySplashRl)
//        startActivity(Intent(this, MainActivity::class.java))
    }

}