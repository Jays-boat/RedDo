package com.jayboat.reddo.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/*
 by Cynthia at 2018/8/22
 */
class DateViewModel : ViewModel() {

    var nowDate: MutableLiveData<String> = MutableLiveData()

    init {
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val time = System.currentTimeMillis()
                    val format = SimpleDateFormat("M/ddE HH:mm", Locale.CHINA)
                    val nowTime = format.format(Date(time))
                    if (nowTime != nowDate.toString())
                        nowDate.value = nowTime
                }
    }

}

