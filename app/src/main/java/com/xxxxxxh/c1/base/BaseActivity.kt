package com.xxxxxxh.c1.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sherloki.devkit.ktx.KtxActivity
import com.xxxxxxh.c1.App
import com.xxxxxxh.c1.utils.ForegroundCallbacks
import org.xutils.x

abstract class BaseActivity : KtxActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        x.Ext.init(this.application)
        init()
//        App.instance!!.listenre = this
    }

    abstract fun getLayoutId(): Int

    abstract fun init()
}