package com.xxxxxxh.c1.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.ToastUtils
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
import kotlin.system.exitProcess


class MainActivity : BaseActivity(), DialogCallBack {

    val pers = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private var isExit = false
    private var exitDlg: AlertDialog? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        ad1.showNativeAd()
        ad2.showBannerAd()
        XXPermissions.with(this)
            .permission(pers)
            .request(object : OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all){
                        stickers.setOnClickListener {
                            val a = showInsertAd(tag = "inter_filter")
                            if (!a) {
                                openGallery(0)
                            }
                        }
                        slimming.setOnClickListener {
                            val a = showInsertAd(tag = "inter_slim")
                            if (!a) {
                                openGallery(1)
                            }
                        }
                        camera.setOnClickListener {
                            val a = showInsertAd(tag = "inter_camera")
                            if (!a) {
                                openCamera()
                            }
                        }
                    }else{
                        ToastUtils.showToast(this@MainActivity,"some permissions were not granted normally")
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    ToastUtils.showToast(this@MainActivity,"no permissions")
                    finish()
                }
            })
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
        XXPermissions.with(this)
            .permission(Manifest.permission.CAMERA)
            .request(object :OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    PictureSelector.create(this@MainActivity)
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

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    ToastUtils.showToast(this@MainActivity,"You cannot use the camera without permission")
                }
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
        exitProcess(0)
//        finish()
    }

    override fun btn2() {
        if (exitDlg != null && exitDlg!!.isShowing)
            exitDlg!!.dismiss()
    }
}