package com.sherloki.devkit.ktx

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.anythink.core.api.ATSDK
import com.anythink.core.api.NetTrafficeCallback
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.sherloki.devkit.R
import com.sherloki.devkit.loge
import com.tencent.mmkv.MMKV
import kotlin.system.measureTimeMillis

class Ktx private constructor(application: Application) {

    companion object {
        @Volatile
        private var INSTANCE: Ktx? = null

        fun initialize(application: Application) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Ktx(application).apply { INSTANCE = this }
            }


        fun getInstance() =
            INSTANCE ?: throw NullPointerException("Have you invoke initialize() before?")
    }

    val app = application

    fun initStartUp() {
        measureTimeMillis {
            MMKV.initialize(app)
            MobileAds.initialize(app) { initializationStatus -> initializationStatus.adapterStatusMap }
            AppLovinSdk.getInstance(app).mediationProvider = AppLovinMediationProvider.MAX
            AppLovinSdk.initializeSdk(app) {

            }
            initOther()
        }.let {
            "application initTime -> ${it}".loge()
        }
    }

    private fun initOther(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            if (app.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }

        Stetho.initializeWithDefaults(app)
        ATSDK.setNetworkLogDebug(true)
        ATSDK.integrationChecking(app)

        ATSDK.checkIsEuTraffic(app, object : NetTrafficeCallback {

            override fun onResultCallback(isEU: Boolean) {
                if (isEU && ATSDK.getGDPRDataLevel(app) == ATSDK.UNKNOWN) {
//                    ATSDK.showGdprAuth(app)
                }
                Log.e("Demoapplication", "onResultCallback:$isEU")
            }

            override fun onErrorCallback(errorMsg: String?) {
                Log.e("Demoapplication", "onErrorCallback:$errorMsg")
            }
        })
        val excludelist = mutableListOf<String>()
        excludelist.add("com.exclude.myoffer1")
        excludelist.add("com.exclude.myoffer2")
        ATSDK.setExcludeMyOfferPkgList(excludelist)

        Log.e("Demoapplication", "isChinaSDK:" + ATSDK.isCnSDK())
        Log.e("Demoapplication", "SDKVersionName:" + ATSDK.getSDKVersionName())

        val custommap: MutableMap<String, Any> = HashMap()
        custommap["key1"] = "initCustomMap1"
        custommap["key2"] = "initCustomMap2"
        ATSDK.initCustomMap(custommap)

        val subcustommap: MutableMap<String, Any> = HashMap()
        subcustommap["key1"] = "initPlacementCustomMap1"
        subcustommap["key2"] = "initPlacementCustomMap2"
        ATSDK.initPlacementCustomMap("b5aa1fa4165ea3", subcustommap) //native  facebook

        ATSDK.setChannel("testChannle")
        ATSDK.setSubChannel("testSubChannle")
        ATSDK.init(
            app,
            app.getString(R.string.app_id),
            app.getString(R.string.app_key)
        )
    }
}