package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*
import android.graphics.Color
import com.jayboat.reddo.room.TypeConverterHelper
import com.jayboat.reddo.utils.nowDate

@Entity(tableName = "entry", indices = [Index("type")])
@TypeConverters(TypeConverterHelper::class)
data class SimpleEntry(
        var type: EntryType = EntryType.TODO,
        var title: String = "",
        var detail: String? = null,
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @Embedded
        var time: RedDate = nowDate
) {

    enum class EntryType(val color: Int) {
        ESSAY(Color.parseColor("#7FB2ED")),
        TODO(Color.parseColor("#9AD25E")),
        AGENDA(Color.parseColor("#E07A75")),
        DAILY(Color.parseColor("#FAC538"));
    }

    data class RedDate(
            var year: Int = -1,
            var month: Int = -1,
            var day: Int = -1,
            var hour: Int = -1,
            var minute: Int = -1,
            var second: Int = -1
    )

    fun resetId() {
        id = 0
    }
}