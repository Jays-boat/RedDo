package com.jayboat.reddo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jayboat.reddo.Const
import com.jayboat.reddo.R
import com.jayboat.reddo.adapter.ShowItemAdapter
import com.jayboat.reddo.base.BaseActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : BaseActivity()  {

    lateinit var type:String
    var mData = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        type = Const.TYPE_ALL

        rv_main.apply {
            adapter = ShowItemAdapter(mData, type)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        upDateTime()

        iv_main_calendar.setOnClickListener{
            val intent = Intent(this@MainActivity,CalendarActivity::class.java)
            startActivity(intent)
        }
        gp_main.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
//                R.id.rb_essay -> rb_essay.
            }

        }
    }

    private fun upDateTime() {
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onNext(t: Long) {
                        val time = System.currentTimeMillis()
                        val format = SimpleDateFormat("M/ddE HH:mm", Locale.CHINA)
                        val nowTime = format.format(Date(time))
                        if (nowTime != tv_main_date.text.toString())
                            tv_main_date.text = nowTime
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })

    }
}