package com.jayboat.reddo.utils

import android.annotation.SuppressLint
import android.widget.Toast
import com.jayboat.reddo.appContext

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/26 15:39
 * Description: com.jayboat.reddo.utils
 */
private var mToast: Toast? = null

@SuppressLint("ShowToast")
fun show(text: String, time: Int = Toast.LENGTH_SHORT) {
    if (time != Toast.LENGTH_SHORT && time != Toast.LENGTH_LONG) {
        return
    }

    if (mToast == null) {
        mToast = Toast.makeText(appContext, text, time)
    } else {
        mToast?.setText(text)
    }

    mToast?.show()
}