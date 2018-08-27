package com.jayboat.reddo.ui.activity

import android.Manifest.permission.*
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.utils.*
import com.jayboat.reddo.viewmodel.EntryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.popup_more.view.*
import org.jetbrains.anko.startActivity

class EditActivity : BaseActivity() {

    val REQUEST_CHOOSE = 0
    private lateinit var urls: MutableList<Uri>
    private val entryViewModel by lazy { ViewModelProviders.of(this@EditActivity).get(EntryViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        el_edit.type = when (intent.getStringExtra("type")) {
            TYPE_ESSAY -> SimpleEntry.EntryType.ESSAY
            TYPE_DAILY -> SimpleEntry.EntryType.DAILY
            TYPE_AGENDA -> SimpleEntry.EntryType.AGENDA
            else -> SimpleEntry.EntryType.TODO
        }.also {
            if (it == SimpleEntry.EntryType.TODO){
                iv_edit_album.visibility = View.GONE
            }
        }

        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            entryViewModel.getEntryById(id).observe(this, Observer {
                if (it == null) {
                    return@Observer
                }
                el_edit.loadData(it)
                el_edit.type = it.simpleEntry.type
                if (it.simpleEntry.type == SimpleEntry.EntryType.TODO) {
                    startActivity<EditTodoActivity>("id" to it.simpleEntry.id)
                    finish()
                }

            })
        }
        el_edit.addViewModel(entryViewModel)
        iv_edit_back.setOnClickListener {
            if (el_edit.type == SimpleEntry.EntryType.AGENDA) {
                editTime(el_edit.data)
            } else {
                finish()
            }
        }
        iv_edit_down.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            val v: View? = currentFocus
            if (imm != null && v != null) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        iv_edit_album.setOnClickListener { _ ->
            RxPermissions(this@EditActivity)
                    .request(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA)
                    .subscribe {
                        if (it) {
                            Matisse.from(this@EditActivity)
                                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                                    .countable(true)
                                    .maxSelectable(9)
                                    .thumbnailScale(0.80f)
                                    .theme(R.style.Matisse_Zhihu)
                                    .imageEngine(ImageEngine())
                                    .capture(true)
                                    .captureStrategy(CaptureStrategy(true, "com.jayboat.reddo.fileprovider"))
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                    .forResult(REQUEST_CHOOSE)
                        } else {
                            show("sorry不能使用相册的图片哦_(:з」∠)_")
                        }
                    }
        }
        iv_edit_more.setOnClickListener {
            getPopupWindow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHOOSE && resultCode == RESULT_OK) {
            urls = Matisse.obtainResult(data)
            el_edit.addPicture(urls)
        }
    }


    override fun onDestroy() {
        val dataToSave = el_edit.saveData()
        if (intent.getIntExtra("id", -1) == -1 && !dataToSave.simpleEntry.detail.isNullOrBlank()) {
            entryViewModel.insertEntry(dataToSave)
        } else {
            entryViewModel.updateEntry(dataToSave)
        }
        super.onDestroy()
    }

    private fun getPopupWindow() {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.popup_more, null)
        PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .apply {
                    isOutsideTouchable = true
                    isFocusable = true
                    setBackgroundDrawable(ColorDrawable())
                    animationStyle = R.style.anim_bottom
                    showAtLocation(iv_edit_more, Gravity.BOTTOM, 0, 0)
                    view.apply {
                        tv_choose_todo.setOnClickListener {
                            iv_edit_album.visibility = View.GONE
                            el_edit.changeType(SimpleEntry.EntryType.TODO)
                            dismiss()
                        }
                        tv_choose_agenda.setOnClickListener {
                            iv_edit_album.visibility = View.VISIBLE
                            el_edit.changeType(SimpleEntry.EntryType.AGENDA)
                            dismiss()
                        }
                        tv_choose_essay.setOnClickListener {
                            iv_edit_album.visibility = View.VISIBLE
                            el_edit.changeType(SimpleEntry.EntryType.ESSAY)
                            dismiss()
                        }
                        tv_choose_daily.setOnClickListener {
                            dismiss()
                            startActivity(Intent(this@EditActivity, DailyCameraActivity::class.java))
                            finish()
                        }
                    }
                }
    }

    private fun editTime(data: Entry) {
        TimePickerBuilder(this@EditActivity, OnTimeSelectListener { date, _ ->
            data.simpleEntry.time = dateToRedDate(date)
            if (data.simpleEntry.id != 0){
                entryViewModel.updateEntry(data)
            } else {
                entryViewModel.insertEntry(data)
            }
            finish()
        })
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setDate(redDateToDate(data.simpleEntry.time))
                .setTitleText("选择日程开始时间哦 :>")
                .setTitleColor(ContextCompat.getColor(this,R.color.calendar_weekend))
                .setCancelColor(ContextCompat.getColor(this,R.color.weak_word_gray))
                .setSubmitColor(ContextCompat.getColor(this,R.color.calendar_rv_text))
                .build()
                .show()
    }

}