package com.jayboat.reddo.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.EssayText
import com.jayboat.reddo.room.bean.Image
import com.jayboat.reddo.room.dao.EntryDao
import com.jayboat.reddo.room.dao.EssayDao

@Database(entities = [Entry::class, EssayText::class,Image::class], version = 1)
abstract class RedDataBase : RoomDatabase() {
    abstract fun getEssayDao(): EssayDao
    abstract fun getEntryDao(): EntryDao
    companion object {
        const val NAME = "red_do_data_base"
    }
}