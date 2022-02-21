package net.basicmodel.ui

import com.bumptech.glide.Glide
import com.xxxxxxh.c1.R
import kotlinx.android.synthetic.main.activity_slimming.*
import com.xxxxxxh.c1.base.BaseActivity

class SlimmingActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_slimming
    }

    override fun init() {
        val url = intent.getStringExtra("url") as String
        Glide.with(this).load(url).into(slimming_pv)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener { finish() }
    }
}