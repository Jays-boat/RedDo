package com.jayboat.reddo.ui.activity

import android.Manifest.permission.*
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.utils.show
import com.jayboat.reddo.viewmodel.EntryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_daily_camera.*
import org.jetbrains.anko.editText
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DailyCameraActivity : BaseActivity() {
    var isPermissionGranted = false

    fun saveBitmap(dir: String, b: Bitmap): String {
        val dataTake = System.currentTimeMillis()
        val jpegName = "$dir/picture_$dataTake.jpg"
        var bos: BufferedOutputStream? = null
        try {
            val file = File(jpegName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            bos = BufferedOutputStream(FileOutputStream(file))
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            return jpegName
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        } finally {
            bos?.close()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_camera)

        jcamera.apply {
            setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER)
            setSaveVideoPath(getExternalFilesDir("video")?.path)
            setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE)
            setErrorLisenter(object : ErrorListener {
                override fun AudioPermissionError() {
                    show("见鬼了，一切正常但是我不能录像(」゜ロ゜)」")
                }

                override fun onError() {
                    runOnUiThread {
                        show("你这点权限，我很难帮你办事啊╮(╯3╰)╭")
                    }
                }
            })
            setJCameraLisenter(object : JCameraListener {
                override fun recordSuccess(url: String, firstFrame: Bitmap) {
                    val bitmapName = saveBitmap(
                            getExternalFilesDir("video/preview").path,
                            firstFrame
                    )
                    AlertDialog.Builder(this@DailyCameraActivity).apply {
                        setTitle("成功记录下来了呢~")
                        val et = editText {
                            hint = "给这段日常起个名吧~"
                        }
                        removeView(et)
                        setView(et)
                        setPositiveButton("存进柜子里") { _, _ ->
                            ViewModelProviders.of(this@DailyCameraActivity)
                                    .get(EntryViewModel::class.java)
                                    .insertSimpleEntry(
                                            SimpleEntry(
                                                    SimpleEntry.EntryType.DAILY,
                                                    et.text.toString().takeIf { it.isNotBlank() }
                                                            ?: "假装有标题(⊃д⊂)",
                                                    "$bitmapName|$url"
                                            )
                                    )
                            finish()
                        }
                        setNeutralButton("消失在岁月") { _, _ ->
                            finish()
                        }
                        setCancelable(false)
                    }.show()
                }

                override fun captureSuccess(bitmap: Bitmap?) {}
            })
            setLeftClickListener { finish() }
        }

        RxPermissions(this).request(
                WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA
        ).subscribe {
            if (it) {
                jcamera.onResume()
            } else {
                show("你这点权限，我很难帮你办事啊╮(╯3╰)╭")
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionGranted) {
            jcamera.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        jcamera.onPause()
    }
}
