package com.xxxxxxh.c1.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.xutils.x

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        x.Ext.init(this.application)
        init()
    }

    abstract fun getLayoutId(): Int

    abstract fun init()
}