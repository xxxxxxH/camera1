package com.xxxxxxh.c1.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object CommonUtils {

    fun saveBitmap(context: Context, name: String, bitmap: Bitmap) {
        val path =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + name + ".jpg"
        val f = File(path)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                out.flush()
                out.close()
            }
        }
    }

}