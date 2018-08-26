package com.jayboat.reddo.ui.activity

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.utils.ImageEngine
import com.jayboat.reddo.viewmodel.EntryViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : BaseActivity() {

    val REQUEST_CHOOSE = 0
    private lateinit var urls : MutableList<Uri>
    private val entryViewModel by lazy { ViewModelProviders.of(this@EditActivity).get(EntryViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val id = intent.getIntExtra("id",-1)
        if (id != -1){
//          通过id获取viewModel里面的内容并加载
            el_edit.getListener().loadData()
        }

        iv_edit_back.setOnClickListener { finish() }
        iv_edit_down.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            val v: View? = currentFocus
            if (imm != null && v != null) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } }
        iv_edit_album.setOnClickListener {
            if (requestPermission()){
                Matisse.from(this@EditActivity)
                        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                        .countable(true)
                        .maxSelectable(9)
                        .thumbnailScale(0.80f)
                        .theme(R.style.Matisse_Zhihu)
                        .imageEngine(ImageEngine())
                        .capture(true)
                        .captureStrategy(CaptureStrategy(true,"com.jayboat.reddo.fileprovider"))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .forResult(REQUEST_CHOOSE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHOOSE && resultCode == RESULT_OK) {
            urls = Matisse.obtainResult(data)
            el_edit.getListener().addPicture(urls)
        }
    }

    private fun requestPermission() : Boolean {
        var isPermission = false
        RxPermissions(this@EditActivity)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe {
                    isPermission = when {
                        it.granted -> true
                        it.shouldShowRequestPermissionRationale -> false
                        else -> false
                    }
                }
        return isPermission
    }

    override fun onDestroy() {
        entryViewModel.insertEntry(el_edit.getListener().saveData())
        super.onDestroy()
    }
}