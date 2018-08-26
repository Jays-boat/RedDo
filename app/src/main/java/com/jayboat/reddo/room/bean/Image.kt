package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*

@Entity(
        tableName = "image",
        indices = [Index("entry_id", unique = false)],
        foreignKeys = [
            ForeignKey(entity = SimpleEntry::class,
                    parentColumns = ["id"],
                    childColumns = ["entry_id"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class Image(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "entry_id")
        var entryId: Int = -1,
        @ColumnInfo(name = "x_location")
        var xLocation: Float = -1f,
        @ColumnInfo(name = "y_location")
        var yLocation: Float = -1f,
        @ColumnInfo(name = "width")
        var width: Int = -1,
        @ColumnInfo(name = "height")
        var height: Int = -1,
        @ColumnInfo(name = "uri")
        var uri: String = ""
)