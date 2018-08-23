package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*

@Entity(
        tableName = "text_info",
        indices = [Index("entry_id")],
        foreignKeys = [
            ForeignKey(entity = SimpleEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["entry_id"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class TextInfo(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "entry_id")
        var entryId: Int = -1
        //todo 更多数据
)