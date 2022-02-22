package com.xxxxxxh.c1.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.sherloki.devkit.showBannerAd
import com.sherloki.devkit.showInsertAd
import com.sherloki.devkit.showNativeAd
import com.sherloki.devkit.showOpenAd
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.base.BaseActivity
import com.xxxxxxh.c1.utils.GlideEngine
import com.xxxxxxh.c1.utils.PictureSelectorUiUtils
import com.xxxxxxh.c1.widget.dlg.DialogCallBack
import com.xxxxxxh.c1.widget.dlg.DialogUtils
import kotlinx.android.synthetic.main.activity_main.*
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack
import lolodev.permissionswrapper.wrapper.PermissionWrapper


class MainActivity : BaseActivity(), DialogCallBack {

    val pers = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var isExit = false
    private var exitDlg: AlertDialog? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        ad1.showNativeAd()
        ad2.showBannerAd()
        ad3.showNativeAd()
        PermissionWrapper.Builder(this)
            .addPermissions(pers)
            .addPermissionsGoSettings(true)
            .addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    stickers.setOnClickListener {
                        showInsertAd(tag = "inter_filter")
                        openGallery(0)
                    }
                    slimming.setOnClickListener {
                        showInsertAd(tag = "inter_slim")
                        openGallery(1)
                    }
                    camera.setOnClickListener {
                        showInsertAd(tag = "inter_camera")
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
            .setSelectorUIStyle(PictureSelectorUiUtils.get().setCustomUiStyle())
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
                        intent = Intent(this@MainActivity, ImageActivity::class.java)
                        intent.putExtra("url", url)
                        startActivity(intent)
                    }
                }

                override fun onCancel() {}
            })
    }

    override fun onBackPressed() {
        if (!isExit) {
            exitDlg = DialogUtils.createExitDlg(
                this, "Are you sure to exit the application?", b1 = true, b2 = true, callBack = this
            )
            exitDlg!!.show()
        }
//        super.onBackPressed()
    }


    override fun btn1() {
        isExit = true
        if (exitDlg != null && exitDlg!!.isShowing)
            exitDlg!!.dismiss()
    }

    override fun btn2() {
        if (exitDlg != null && exitDlg!!.isShowing)
            exitDlg!!.dismiss()
    }
}