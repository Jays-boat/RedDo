package com.jayboat.reddo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.haibin.calendarview.Calendar
import com.jayboat.reddo.redDataBase
import com.jayboat.reddo.room.bean.*
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class EntryViewModel : ViewModel() {
    private val entryDao by lazy { redDataBase.getEntryDao() }
    private val imageDao by lazy { redDataBase.getImageDao() }
    private val todoDao by lazy { redDataBase.getTodoDao() }
    private val textInfoDao by lazy { redDataBase.getTextInfoDao() }

    val simpleEntrys by lazy { entryDao.selectSimpleEntryList() }

    val entrys by lazy { entryDao.selectEntryList() }

    val todoEntrys by lazy { entryDao.selectEntryListWithType(TODO) }

    val essayEntrys by lazy { entryDao.selectEntryListWithType(ESSAY) }

    val agendaEntrys by lazy { entryDao.selectEntryListWithType(AGENDA) }

    val dailyEntrys by lazy { entryDao.selectEntryListWithType(DAILY) }

    fun getEntrysByDate(redDate: SimpleEntry.RedDate): LiveData<List<Entry>> {
        val (y, m, d) = redDate
        return entryDao.selectEntryListWithDate(y, m, d)
    }

    fun getEntrysByDate(date: Calendar): LiveData<List<Entry>> = entryDao.selectEntryListWithDate(date.year, date.month, date.day)

    fun delEntry(vararg e: SimpleEntry) = Schedulers.io().scheduleDirect { entryDao.delEntry(*e) }

    fun insertSimpleEntry(vararg e: SimpleEntry) = Schedulers.io().scheduleDirect { entryDao.insertEntry(*e) }

    fun insertEntry(e: Entry) = Schedulers.io().scheduleDirect {
        e.apply {
            entryDao.insertEntry(simpleEntry)
            when (simpleEntry.type) {
                ESSAY, AGENDA -> {
                    imageDao.insertImages(imgList)
                    textInfoDao.insertTextInfoList(textInfoList)
                }
                TODO -> todoDao.insertTodoList(todoList)
                DAILY -> {
                }
            }
        }
    }

    fun insertEntrys(vararg e: Entry) {
        Observable.create<Int> { ob ->
            if (e.size < 2) {
                if (e.size == 1) {
                    e[0].apply {
                        entryDao.insertEntry(simpleEntry)
                        when (simpleEntry.type) {
                            ESSAY, AGENDA -> {
                                imageDao.insertImages(imgList)
                                textInfoDao.insertTextInfoList(textInfoList)
                            }
                            TODO -> todoDao.insertTodoList(todoList)
                            DAILY -> { }
                        }
                    }
                }
                return@create
            }
            entryDao.insertEntry(*e.map { it.simpleEntry }.toTypedArray())
            ob.onNext(entryDao.getLastID())
        }.subscribeOn(Schedulers.io())
        .subscribe { id ->
            var entryId = id - e.size
            val textInfoList = mutableListOf<TextInfo>()
            val imageList = mutableListOf<Image>()
            val todoList = mutableListOf<Todo>()
            e.forEach { entry ->
                entryId++
                when (entry.simpleEntry.type) {
                    ESSAY, AGENDA -> {
                        entry.textInfoList.forEach { it.entryId = entryId }
                        entry.imgList.forEach { it.entryId = entryId }
                        textInfoList.addAll(entry.textInfoList)
                        imageList.addAll(entry.imgList)
                    }
                    TODO -> {
                        entry.todoList.forEach { it.entryId = entryId }
                        todoList.addAll(entry.todoList)
                    }
                    DAILY -> {
                    }
                }
            }

            textInfoDao.insertTextInfoList(textInfoList)
            imageDao.insertImages(imageList)
            todoDao.insertTodoList(todoList)
        }
    }

    fun updateSimpleEntrys(e: SimpleEntry) = Schedulers.io().scheduleDirect { entryDao.updateEntry(e) }

    fun updateEntry(e: Entry) = Schedulers.io().scheduleDirect {
        e.apply {
            entryDao.updateEntry(simpleEntry)
            when (simpleEntry.type) {
                ESSAY, AGENDA -> {
                    imageDao.delImgs(simpleEntry.id)
                    textInfoDao.delTextInfoList(simpleEntry.id)
                    imageDao.insertImages(imgList)
                    textInfoDao.insertTextInfoList(textInfoList)
                }
                TODO -> {
                    todoDao.delTodoList(simpleEntry.id)
                    todoDao.insertTodoList(todoList)
                }
                DAILY -> {
                }
            }
        }
    }

}