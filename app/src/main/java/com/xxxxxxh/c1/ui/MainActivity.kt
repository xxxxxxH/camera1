package com.xxxxxxh.c1.ui

import android.Manifest
import android.content.Intent
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_main.*
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack
import lolodev.permissionswrapper.wrapper.PermissionWrapper
import net.basicmodel.base.BaseActivity
import net.basicmodel.ui.SlimmingActivity


class MainActivity : BaseActivity() {

    val pers = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        PermissionWrapper.Builder(this)
            .addPermissions(pers)
            .addPermissionsGoSettings(true)
            .addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    stickers.setOnClickListener {
                        openGallery(0)
                    }
                    slimming.setOnClickListener {
                        openGallery(1)
                    }
                    camera.setOnClickListener {
                        openCamera()
                    }
                }

                override fun onDenied(p0: String?) {
                    finish()
                }

            }).build().request()
    }

    private fun openGallery(targetAc: Int) {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine().createGlideEngine())
            .setMaxSelectNum(1)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.let {
                        val url = result[0].realPath
                        var intent: Intent? = null
                        when (targetAc) {
                            0 -> {
                                intent = Intent(this@MainActivity, StickerActivity::class.java)
                                intent.putExtra("url", url)
                            }
                            1 -> {
                                intent = Intent(this@MainActivity, SlimmingActivity::class.java)
                                intent.putExtra("url", url)
                            }
                            else -> return@let
                        }
                        startActivity(intent)
                    }
                }

                override fun onCancel() {}
            })
    }

    private fun openCamera() {
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.let {
                        val url = result[0].realPath
                        intent = Intent(this@MainActivity, StickerActivity::class.java)
                        intent.putExtra("url", url)
                        startActivity(intent)
                    }
                }

                override fun onCancel() {}
            })
    }
}