package com.jayboat.reddo.room.bean

import android.arch.persistence.room.*

@Entity(
    indices = [Index("entry_id")],
    tableName = "essays_text",
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["id"],
            childColumns = ["entry_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class EssayText (
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "entry_id")
        var entryId: Int = -1,
        var detail: String = ""
)

data class Essay(
        @Embedded
        var text: EssayText? = null,
        @Relation(parentColumn = "entry_id", entityColumn = "entry_id", entity = Image::class)
        var list: List<Image> = listOf()
)