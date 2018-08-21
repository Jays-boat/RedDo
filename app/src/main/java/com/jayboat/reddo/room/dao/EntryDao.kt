package com.jayboat.reddo.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.jayboat.reddo.room.bean.Entry

@Dao
interface EntryDao {
    @Insert(onConflict = REPLACE)
    fun insertEntry(vararg e: Entry)

    @Query("SELECT last_insert_rowid() FROM entry")
    fun getLastID(): Int

    @Query("SELECT * FROM entry")
    fun selectEntryList(): LiveData<List<Entry>>

    @Update
    fun updateEntry(vararg e: Entry)

    @Delete
    fun delEntry(vararg e: Entry)
}