package com.jayboat.reddo.room.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.jayboat.reddo.room.bean.Todo

@Dao
interface TodoDao {

    @Insert(onConflict = REPLACE)
    fun insertTodo(todo: Todo)

    @Insert(onConflict = REPLACE)
    fun insertTodoList(todoList: List<Todo>)

    @Delete
    fun delTodoList(todoList: List<Todo>)

    @Query("DELETE FROM todo WHERE entry_id = :entryId")
    fun delTodoList(entryId: Int)

    @Update
    fun updateTodo(todo: Todo)
}