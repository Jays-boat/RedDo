package com.jayboat.reddo.ui.activity

import android.Manifest.permission.*
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.cjt2325.cameralibrary.util.FileUtil
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.utils.dp
import com.jayboat.reddo.utils.show
import com.jayboat.reddo.viewmodel.EntryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_daily_camera.*
import org.jetbrains.anko.*

class DailyCameraActivity : BaseActivity() {
    var isPermissionGranted = false

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
                    show("你这点权限，我很难帮你办事啊╮(╯3╰)╭")
                }
            })
            setJCameraLisenter(object : JCameraListener {
                override fun recordSuccess(url: String?, firstFrame: Bitmap?) {
                    val bitmapName = FileUtil.saveBitmap(
                            getExternalFilesDir("video/preview")?.path,
                            firstFrame
                    )
                    Dialog(this@DailyCameraActivity).apply {
                        setCancelable(false)
                        setContentView(verticalLayout {
                            lparams(matchParent, matchParent).apply {
                                padding = dp(8f).toInt()
                                minimumWidth = dp(150f).toInt()
                            }
                            val title = editText {
                                hint = "写点什么记录一下吧"
                            }.lparams(matchParent, wrapContent)
                            button {
                                background = null
                                text = "保存"
                                setOnClickListener { _ ->
                                    ViewModelProviders.of(this@DailyCameraActivity)
                                            .get(EntryViewModel::class.java)
                                            .insertSimpleEntry(
                                                    SimpleEntry(
                                                            SimpleEntry.EntryType.DAILY,
                                                            title.text.toString().takeIf { it.isNotBlank() }
                                                                    ?: "假装有标题(⊃д⊂)",
                                                            "$bitmapName|$url"
                                                    )
                                            )
                                    finish()
                                }
                            }.lparams(matchParent, wrapContent)
                        })
                        show()
                    }
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
