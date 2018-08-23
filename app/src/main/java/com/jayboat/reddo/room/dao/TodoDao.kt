package com.jayboat.reddo.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.jayboat.reddo.room.bean.Todo

@Dao
interface TodoDao {

    @Insert(onConflict = REPLACE)
    fun insertTodoList(imgs: List<Todo>)

    @Query("DELETE FROM todo WHERE entry_id = :entryId")
    fun delTodoList(entryId: Int)

}