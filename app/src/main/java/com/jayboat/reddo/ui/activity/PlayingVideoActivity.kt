package com.jayboat.reddo.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.MediaController
import android.widget.PopupWindow
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.utils.show
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_playing_vedio.*
import kotlinx.android.synthetic.main.popup_edit.view.*


class PlayingVideoActivity : BaseActivity() {

    private val entryViewModel by lazy { ViewModelProviders.of(this@PlayingVideoActivity).get(EntryViewModel::class.java) }
    private lateinit var data: Entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing_vedio)

        val id = intent.getIntExtra("id",-1)
        if (id == -1){
            show("似乎打开方式不对诶，新录制一个吧ヽ(￣▽￣)ﾉ")
            startActivity(Intent(this@PlayingVideoActivity,DailyCameraActivity::class.java))
            finish()
        } else {
            entryViewModel.getEntryById(id).observe(this, Observer {
                if (it == null)
                    return@Observer
                data = it
                val url = it.simpleEntry.detail?.split("|")?.get(1)
                vv_playing.setVideoPath(url)
                tv_title.text = it.simpleEntry.title
            })
        }

        vv_playing.setMediaController(MediaController(this))
        vv_playing.setOnPreparedListener { vv_playing.start() }

        ib_back.setOnClickListener { finish() }
        tv_title.setOnClickListener {
            vv_playing.pause()
            showPopup()
        }
    }

    private fun showPopup(){
        val view = LayoutInflater.from(this).inflate(R.layout.popup_edit, null)
        PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .apply {
                    isOutsideTouchable = true
                    isFocusable = true
                    setBackgroundDrawable(ColorDrawable())
                    animationStyle = R.style.anim_popup
                    showAtLocation(vv_playing, Gravity.CENTER, 0, 0)
                    view.btn_edit_cancel.setOnClickListener {
                        vv_playing.resume()
                        dismiss()
                    }
                    view.btn_edit_sure.setOnClickListener{
                        val title = view.tv_edit_title.text.toString()
                        data.simpleEntry.title = title
                        entryViewModel.updateEntry(data)
                        tv_title.text = title
                        view.tv_edit_title.setText("")
                        view.tv_edit_title.clearFocus()
                        vv_playing.resume()
                        dismiss()
                    }
                }
    }
}
