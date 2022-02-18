package com.xxxxxxh.c1.ui

import android.content.Intent
import android.view.View
import com.xxxxxxh.c1.R
import kotlinx.android.synthetic.main.activity_image.*
import net.basicmodel.base.BaseActivity

class ImageActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_image
    }

    override fun init() {
        iv_option1.setOnClickListener {

        }
        iv_option2.setOnClickListener {

        }
        natural_iv.setOnClickListener {
            option_confirm.visibility = View.VISIBLE
        }
        cancel.setOnClickListener {
            option_confirm.visibility = View.GONE
        }
        confirm.setOnClickListener {
            option_confirm.visibility = View.GONE
        }
        user.setOnClickListener {
            startActivity(Intent(this, LoginMainActivity::class.java))
        }
    }
}