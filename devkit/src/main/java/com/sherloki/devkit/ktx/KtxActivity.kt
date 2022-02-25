package com.sherloki.devkit.ktx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anythink.splashad.api.ATSplashAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.sherloki.devkit.*

open class KtxActivity : AppCompatActivity() {

    var maxInterstitialAd: MaxInterstitialAd? = null
    var openAd: ATSplashAd? = null

    var isHandle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createMaxInterstitialAd()
        createOpenAd()
    }

    open fun onDismiss() {

    }

}