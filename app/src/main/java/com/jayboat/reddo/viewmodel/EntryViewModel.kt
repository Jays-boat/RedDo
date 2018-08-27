package com.jayboat.reddo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.haibin.calendarview.Calendar
import com.jayboat.reddo.redDataBase
import com.jayboat.reddo.room.bean.*
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class EntryViewModel : ViewModel() {
    private val entryDao by lazy { redDataBase.getEntryDao() }
    private val imageDao by lazy { redDataBase.getImageDao() }
    private val todoDao by lazy { redDataBase.getTodoDao() }
    private val textInfoDao by lazy { redDataBase.getTextInfoDao() }

    val schemeDates by lazy {
        val liveData = MutableLiveData<Map<String, Calendar>>()
        simpleEntrys.observeOn(Schedulers.io())
                .map { list ->
                    val map = mutableMapOf<String, Calendar>()
                    list.asSequence().groupBy { it.time.year to (it.time.month to it.time.day) }
                            .map { timeMap ->
                                Calendar().apply {
                                    timeMap.key.let {
                                        year = it.first
                                        month = it.second.first
                                        day = it.second.second
                                    }
                                    timeMap.value.groupBy { it.type }.forEach { t, tList ->
                                        addScheme(t.color, tList.size.toString())
                                    }
                                }
                            }.forEach {
                                map[it.toString()] = it
                            }
                    return@map map
                }.observeOn(AndroidSchedulers.mainThread()).subscribe {
                    liveData.value = it
                }
        return@lazy liveData
    }

    val simpleEntrys by lazy { entryDao.selectSimpleEntryList() }

    val entrys by lazy { entryDao.selectEntryList() }

    val todoEntrys by lazy { entryDao.selectEntryListWithType(TODO) }

    val essayEntrys by lazy { entryDao.selectEntryListWithType(ESSAY) }

    val agendaEntrys by lazy { entryDao.selectEntryListWithType(AGENDA) }

    val dailyEntrys by lazy { entryDao.selectEntryListWithType(DAILY) }

    fun getEntryById(id: Int) = entryDao.selectEntryWithId(id)

    fun getEntrysByDate(redDate: SimpleEntry.RedDate) = entryDao.selectEntryListWithDate(redDate.year, redDate.month, redDate.day)

    fun getEntrysByDate(date: Calendar) = entryDao.selectEntryListWithDate(date.year, date.month, date.day)

    private fun parseTime(str: String): Array<Int> {
        val t = arrayOf(0, 0, 0)
        var sb = StringBuilder()
        str.forEach {
            if (sb.isBlank()) {
                if (it.isDigit()) {
                    sb.append(it)
                }
                return@forEach
            }
            when (it) {
                'y' -> {
                    t[0] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                'm' -> {
                    t[1] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                'd' -> {
                    t[2] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                else -> sb.append(it)
            }
        }
        return t
    }

    /**
     * 文本输入格式：
     * ${num1}y${num2}m${num3}d ${keyword1} ${keyword2}...
     */
    fun searchEntrys(onNext: Consumer<List<Entry>>) = Observer<String> { str ->
        if (str.isNullOrBlank()) {
            return@Observer
        }
        Observable.create<List<Entry>> { ob ->
            fun String.parseDateAble() = with(toLowerCase()) {
                if (matches(Regex("([0-9]+y)?([0-9]+m)?([0-9]+d)?"))) {
                    val t = parseTime(this)
                    entryDao.selectEntryOnceMatchDate(t[0], t[1], t[2])
                } else {
                    listOf()
                }
            }

            val keyList = str!!.split(" ")
            ob.onNext(entrys.value?.asSequence()?.filter { e ->
                keyList.any {
                    Log.d("why","${e.simpleEntry.title} ${it} ${e.simpleEntry.detail}")
                    Log.d("why", (e.simpleEntry.title.contains(it, true)
                            || e.simpleEntry.detail?.contains(it, true) ?: false).toString())
                    e.simpleEntry.title.contains(it, true)
                            || e.simpleEntry.detail?.contains(it, true) ?: false
                }
            }?.plus(keyList[0].parseDateAble())
                    ?.sortedBy { it.simpleEntry.time.day }
                    ?.sortedBy { it.simpleEntry.time.month }
                    ?.sortedBy { it.simpleEntry.time.year }
                    ?.toList()
                    ?: listOf())
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext)
    }

    fun delEntry(vararg e: SimpleEntry) = Schedulers.io().scheduleDirect { entryDao.delEntry(*e) }

    fun delTodoList(todoList: List<Todo>) = Schedulers.io().scheduleDirect { todoDao.delTodoList(todoList) }

    fun insertSimpleEntry(vararg e: SimpleEntry) = Schedulers.io().scheduleDirect { entryDao.insertEntry(*e) }

    fun createEntry(useEntry: LiveData<Entry>.() -> Unit) {
        Schedulers.io().scheduleDirect {
            entryDao.insertEntry(SimpleEntry())
            val id = entryDao.getLastID()
            AndroidSchedulers.mainThread().scheduleDirect {
                entryDao.selectEntryWithId(id).useEntry()
            }
        }
    }


    fun insertEntry(e: Entry) = Observable.create<Int>{
        entryDao.insertEntry(e.simpleEntry)
        it.onNext(entryDao.getLastID())
    }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe { id ->
        e.apply {
            when (simpleEntry.type) {
                ESSAY, AGENDA -> {
                    textInfoList.forEach { it.entryId = id }
                    imgList.forEach { it.entryId = id }
                    textInfoDao.insertTextInfoList(textInfoList)
                    imageDao.insertImages(imgList)
                }
                TODO -> {
                    todoList.forEach { it.entryId = id }
                    todoDao.insertTodoList(todoList)
                }
                DAILY -> {
                }
            }
        }
    }

    fun insertEntrys(vararg e: Entry) {
        Observable.create<Int> { ob ->
            if (e.size < 2) {
                if (e.size == 1) {
                    insertEntry(e[0])
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
                    imgList.forEach { it.entryId = simpleEntry.id }
                    textInfoList.forEach { it.entryId = simpleEntry.id }
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

    fun updateTodo(todo: Todo) = Schedulers.io().scheduleDirect {
        if (todo.id == 0) {
            todoDao.insertTodo(todo)
        } else {
            todoDao.updateTodo(todo)
        }
    }
}
