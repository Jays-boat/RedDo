package com.jayboat.reddo.room.bean

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class Entry(
        @Embedded
        var simpleEntry: SimpleEntry = SimpleEntry(),
        @Relation(parentColumn = "id", entityColumn = "entry_id", entity = TextInfo::class)
        var textInfoList: List<TextInfo> = listOf(),
        @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Image::class)
        var imgList: List<Image> = listOf(),
        @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Todo::class)
        var todoList: List<Todo> = listOf()
) {
    val isFinished
        get() = simpleEntry.type == SimpleEntry.EntryType.TODO && todoList.all { it.isDone || !it.isActivate }
}