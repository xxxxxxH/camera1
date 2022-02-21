package com.sherloki.devkit.ktx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.sherloki.devkit.R
import com.sherloki.devkit.loge
import com.sherloki.devkit.maxInterstitialAdCreator

open class KtxActivity : AppCompatActivity() {

    var maxInterstitialAd: MaxInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        maxInterstitialAd = maxInterstitialAdCreator(true)
    }

}