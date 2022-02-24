package com.sherloki.devkit

import android.util.Log
import android.view.ViewGroup
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.splash.api.ATNativeSplash
import com.anythink.nativead.splash.api.ATNativeSplashListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.sherloki.devkit.ktx.KtxActivity

fun KtxActivity.showOpenAd(viewGroup: ViewGroup, tag: String = ""): Boolean {
    if (configEntity.isOpenAdReplacedByInsertAd()) {
        return showInsertAd(tag = tag)
    } else {
        loadOpenAdImpl(viewGroup, tag = tag)
        return true
    }
}

private fun KtxActivity.loadOpenAdImpl(viewGroup: ViewGroup, tag: String = "") {
    var splashAd: ATSplashAd? = null
    splashAd = ATSplashAd(this, getString(R.string.open_ad_id), null, object : ATSplashAdListener {
        override fun onAdLoaded() {
            Log.e("loadOpenAdImpl", "onAdLoaded")
            splashAd?.show(this@loadOpenAdImpl, viewGroup)
        }

        override fun onNoAdError(p0: AdError?) {
            Log.e("loadOpenAdImpl", "onNoAdError $p0")
        }

        override fun onAdShow(p0: ATAdInfo?) {
            Log.e("loadOpenAdImpl", "onAdShow $p0")
        }

        override fun onAdClick(p0: ATAdInfo?) {
            Log.e("loadOpenAdImpl", "onAdClick")
        }

        override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
            Log.e("loadOpenAdImpl", "onAdDismiss")
        }
    }, 5000)

    splashAd.setLocalExtra(
        mutableMapOf<String, Any>(
            ATAdConst.KEY.AD_WIDTH to globalWidth,
            ATAdConst.KEY.AD_HEIGHT to (globalHeight * 0.85).toInt()
        )
    )
    splashAd.loadAd()
}

fun KtxActivity.showInsertAd(showByPercent: Boolean = false, isForce: Boolean = false, tag: String = ""): Boolean {
    if (isForce) {
        showInsertAdImpl(tag)
        return true
    } else {
        if (configEntity.isCanShowInsertAd()) {
            if ((showByPercent && configEntity.isCanShowByPercent()) || (!showByPercent)) {
                if (System.currentTimeMillis() - adLastTime > configEntity.insertAdOffset() * 1000) {
                    if (adShownList.getOrNull(adShownIndex) == true) {
                        showInsertAdImpl(tag)
                        return true
                    }
                    adShownIndex++
                    if (adShownIndex >= adShownList.size) {
                        adShownIndex = 0
                    }
                }
            }
        }
        return false
    }
}

fun KtxActivity.maxInterstitialAdCreator(loadOnly: Boolean = true, tag: String = "") =
    MaxInterstitialAd(getString(R.string.insert_ad_id), this).apply {
        "MaxInterstitialAd maxInterstitialAdCreator".loge()
        setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                "MaxInterstitialAd onAdLoaded".loge()
                if (!loadOnly) {
                    showAd(tag)
                }
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                "MaxInterstitialAd onAdDisplayed".loge()
            }

            override fun onAdHidden(ad: MaxAd?) {
                "MaxInterstitialAd onAdHidden".loge()
                adLastTime = System.currentTimeMillis()
            }

            override fun onAdClicked(ad: MaxAd?) {
                "MaxInterstitialAd onAdClicked".loge()
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                "MaxInterstitialAd onAdLoadFailed $adUnitId $error".loge()
                adLastTime = System.currentTimeMillis()
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                "MaxInterstitialAd onAdDisplayFailed".loge()
                adLastTime = System.currentTimeMillis()
            }
        })
        loadAd()
    }

private fun KtxActivity.showInsertAdImpl(tag: String = "") {
    if (maxInterstitialAd?.isReady == true) {
        "MaxInterstitialAd showInsertAdImpl0".loge()
        maxInterstitialAd?.showAd(tag)
    } else {
        "MaxInterstitialAd showInsertAdImpl1".loge()
        maxInterstitialAd?.destroy()
        maxInterstitialAd = null
        maxInterstitialAd = maxInterstitialAdCreator(false, tag)
    }
}

fun ViewGroup.showNativeAd() {
    MaxNativeAdLoader(context.getString(R.string.native_ad_id), context).apply {
        setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                super.onNativeAdLoadFailed(p0, p1)
                "MaxNativeAdLoader onNativeAdLoadFailed $p0 $p1".loge()
            }

            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd?) {
                "MaxNativeAdLoader onNativeAdLoaded".loge()
                super.onNativeAdLoaded(p0, p1)
                p0?.let {
                    removeAllViews()
                    addView(it)
                }
            }
        })
        loadAd()
    }
}

//<com.applovin.mediation.ads.MaxAdView
//android:id="@+id/activitySplashMav"
//android:layout_width="match_parent"
//android:layout_height="50dp"
//maxads:adUnitId="@string/ad_id_banner" />

fun MaxAdView.showBannerAd() {
    setListener(object : MaxAdViewAdListener {
        override fun onAdExpanded(ad: MaxAd?) {
            "MaxAdView onAdExpanded".loge()
        }

        override fun onAdCollapsed(ad: MaxAd?) {
            "MaxAdView onAdCollapsed".loge()
        }

        override fun onAdLoaded(ad: MaxAd?) {
            "MaxAdView onAdLoaded".loge()
        }

        override fun onAdDisplayed(ad: MaxAd?) {
            "MaxAdView onAdDisplayed".loge()
        }

        override fun onAdHidden(ad: MaxAd?) {
            "MaxAdView onAdHidden".loge()
        }

        override fun onAdClicked(ad: MaxAd?) {
            "MaxAdView onAdClicked".loge()
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError) {
            "MaxAdView onAdLoadFailed $adUnitId $error".loge()
        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            "MaxAdView onAdDisplayFailed $ad $error".loge()
        }
    })
    loadAd()
}