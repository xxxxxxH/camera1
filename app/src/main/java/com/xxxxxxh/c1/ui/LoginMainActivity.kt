package com.xxxxxxh.c1.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.xxxxxxh.c1.R
import es.dmoral.prefs.Prefs
import net.basicmodel.base.BaseActivity
import net.basicmodel.entity.StatusEntity
import net.basicmodel.utils.Constant
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x


class LoginMainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_login_main
    }

    override fun init() {
        val params = RequestParams(Constant.STATUS_URL)
        params.addQueryStringParameter("id", "2")
        x.http().get(params, object : Callback.CommonCallback<String> {
            override fun onSuccess(result: String?) {
                result?.let {
                    val entity = Gson().fromJson<StatusEntity>(it, StatusEntity::class.java)
                    if (!TextUtils.isEmpty(entity.status)) {
                        Prefs.with(this@LoginMainActivity).write("status", entity.status)
                    }
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

            }

            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onFinished() {

            }

        })

        findViewById<CardView>(R.id.card_login).setOnClickListener {
            startActivity(Intent(this, LoginFacebookActivity::class.java))
        }
    }

    fun loginFb(view: View) {
        startActivity(Intent(this, LoginFacebookActivity::class.java))
    }
}