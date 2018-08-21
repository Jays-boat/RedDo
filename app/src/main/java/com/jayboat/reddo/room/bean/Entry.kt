package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*
import android.graphics.Color
import com.jayboat.reddo.room.TypeConverterHelper

@Entity(tableName = "entry",indices = [Index("type")])
@TypeConverters(TypeConverterHelper::class)
data class Entry(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var type: EntryType = EntryType.TODO,
        var title: String = "",
        @Embedded
        var time: RedDate = RedDate()
) {

    enum class EntryType(val color: Int){
        ESSAY(Color.parseColor("#7FB2ED")),
        TODO(Color.parseColor("#9AD25E")),
        AGENDA(Color.parseColor("#E07A75")),
        DAILY(Color.parseColor("#FAC538"));
    }

    data class RedDate(
        var year: Int,
        var month: Int,
        var day: Int,
        var hour: Int,
        var minute: Int,
        var second: Int
    ){ @Ignore constructor() : this(-1, -1, -1, -1, -1, -1) }

    fun resetId() {
        id = 0
    }
}