package com.xxxxxxh.c1.ui

import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.xxxxxxh.c1.R
import com.xxxxxxh.c1.widget.sticker.DrawableSticker
import kotlinx.android.synthetic.main.activity_sticker.*
import net.basicmodel.adapter.StickerAdapter
import net.basicmodel.base.BaseActivity
import net.basicmodel.entity.ResourceEntity
import net.basicmodel.utils.ResourceManager
import java.io.File

class StickerActivity : BaseActivity() {

    var data: ArrayList<ResourceEntity> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_sticker
    }

    override fun init() {
        val url = intent.getStringExtra("url") as String
        Glide.with(this).load(url).into(show_edit_iv)
        data = ResourceManager.get()
            .getResourceByFolder(this, R.mipmap::class.java, "mipmap", "sticker")
        recycler.layoutManager = GridLayoutManager(this, 4)
        val adapter = StickerAdapter(data)
        recycler.adapter = adapter
        adapter.setOnItemClickListener { _, _, p ->
            val selectSticker = data[p]
            val drawable = ContextCompat.getDrawable(this, selectSticker.id)
            sticker_view.addSticker(DrawableSticker(drawable))
        }
        cancel.setOnClickListener {
            finish()
        }
        save.setOnClickListener {
            Thread {
                val file =
                    File(Environment.getExternalStorageDirectory().absolutePath + File.separator + System.currentTimeMillis() + "_sticker.jpg")
                sticker_view.save(file)
                finish()
            }.start()
        }
    }
}