package com.xxxxxxh.c1.ui

import android.annotation.SuppressLint
import android.content.Intent
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun init() {
        startActivity(Intent(this, MainActivity::class.java))
    }

}