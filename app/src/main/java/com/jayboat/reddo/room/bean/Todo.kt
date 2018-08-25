package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*

@Entity(
        tableName = "todo",
        indices = [Index("entry_id",unique = false)],
        foreignKeys = [
            ForeignKey(entity = SimpleEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["entry_id"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class Todo(
        var describe: String = "",
        var isActivate: Boolean = true,
        var isDone: Boolean = false,
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "entry_id")
        var entryId: Int = -1
)