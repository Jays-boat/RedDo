package com.jayboat.reddo.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.jayboat.reddo.room.TypeConverterHelper
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import io.reactivex.Flowable

@Dao
interface EntryDao {
    @Insert()
    fun insertEntry(e: SimpleEntry)

    @Insert()
    fun insertEntry(vararg e: SimpleEntry)

    @Query("SELECT id FROM entry ORDER BY id DESC LIMIT 1")
    fun getLastID(): Int

    @Query("SELECT * FROM entry")
    fun selectSimpleEntryList(): Flowable<List<SimpleEntry>>

    @Query("SELECT * FROM entry")
    @Transaction
    fun selectEntryList(): LiveData<List<Entry>>

    @Query("SELECT * FROM entry where year = :year AND month = :month AND day = :day")
    @Transaction
    fun selectEntryListWithDate(year: Int, month: Int, day: Int): LiveData<List<Entry>>

    @Query("SELECT * FROM entry where type = :t")
    @Transaction
    @TypeConverters(TypeConverterHelper::class)
    fun selectEntryListWithType(t: SimpleEntry.EntryType): LiveData<List<Entry>>

    @Update
    fun updateEntry(e: SimpleEntry)

    @Delete
    fun delEntry(vararg e: SimpleEntry)
}