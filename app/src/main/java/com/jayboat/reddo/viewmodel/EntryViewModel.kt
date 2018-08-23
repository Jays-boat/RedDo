package com.jayboat.reddo.viewmodel

import android.arch.lifecycle.ViewModel
import com.jayboat.reddo.redDataBase
import com.jayboat.reddo.room.bean.*
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*

class EntryViewModel : ViewModel() {
    private val entryDao by lazy { redDataBase.getEntryDao() }
    private val imageDao by lazy { redDataBase.getImageDao() }
    private val todoDao by lazy { redDataBase.getTodoDao() }
    private val textInfoDao by lazy { redDataBase.getTextInfoDao() }

    val simpleEntrys by lazy { entryDao.selectSimpleEntryList() }

    val entrys by lazy { entryDao.selectEntryList() }

    fun delEntry(vararg e: SimpleEntry) = entryDao.delEntry(*e)

    fun insertSimpleEntry(vararg e: SimpleEntry) = entryDao.insertEntry(*e)

    fun insertEntry(e: Entry) {
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

        if (e.size < 2) {
            if (e.size == 1) {
                insertEntry(e[0])
            }
            return
        }

        entryDao.insertEntry(*e.map { it.simpleEntry }.toTypedArray())

        // eg. e.size = 5, start = 6 -> last = 10 -> entryId = 4.
        // adjust at line 55
        var entryId = entryDao.getLastID() - e.size
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

    fun updateSimpleEntrys(e: SimpleEntry) = entryDao.updateEntry(e)

    fun updateEntry(e: Entry) {
        e.apply {
            updateSimpleEntrys(simpleEntry)
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