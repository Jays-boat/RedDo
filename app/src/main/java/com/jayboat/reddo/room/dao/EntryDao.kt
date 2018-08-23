package com.jayboat.reddo.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry

@Dao
interface EntryDao {
    @Insert()
    fun insertEntry(e: SimpleEntry)

    @Insert()
    fun insertEntry(vararg e: SimpleEntry)

    @Query("SELECT last_insert_rowid() FROM entry")
    fun getLastID(): Int

    @Query("SELECT * FROM entry")
    fun selectSimpleEntryList(): LiveData<List<SimpleEntry>>

    @Query("SELECT * FROM entry")
    @Transaction
    fun selectEntryList(): LiveData<List<Entry>>

    @Update
    fun updateEntry(e: SimpleEntry)

    @Delete
    fun delEntry(vararg e: SimpleEntry)
}