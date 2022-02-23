package com.xxxxxxh.c1.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.sherloki.devkit.*
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun init() {
        lifecycleScope.requestConfig {
            if (isLogin) {
                jumpToMain()
            } else {
                if (configEntity.needLogin()) {
                    if (configEntity.needDeepLink() && configEntity.faceBookId().isNotBlank()) {
                        initFaceBook(configEntity.faceBookId()) {
                            "initFaceBook $it".loge()
                            it?.let {
                                login.isVisible = true
                            } ?: kotlin.run {
                                jumpToMain()
                            }
                        }
                    } else {
                        login.isVisible = true
                    }
                } else {
                    jumpToMain()
                }
            }
        }

        login.setOnClickListener {
            startActivity(Intent(this@SplashActivity, FaceBookActivity::class.java))
            finish()
        }

        activitySplashMav.showBannerAd()
    }

    private fun jumpToMain() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}