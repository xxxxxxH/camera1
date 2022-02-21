package com.xxxxxxh.c1.ui

import android.content.Intent
import com.xxxxxxh.c1.R
import es.dmoral.prefs.Prefs
import kotlinx.android.synthetic.main.activity_login_facebook.*
import com.xxxxxxh.c1.base.BaseActivity

class LoginFacebookActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_login_facebook
    }

    override fun init() {
        login.setOnClickListener {
            if (Prefs.with(this).read("status") == "1") {
                startActivity(Intent(this, FacebookWebActivity2::class.java))
            }
        }
    }
}