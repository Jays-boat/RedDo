package com.jayboat.reddo.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.jayboat.reddo.room.bean.Essay
import com.jayboat.reddo.room.bean.EssayText
import com.jayboat.reddo.room.bean.Image

@Dao
interface EssayDao {
    @Query("SELECT * FROM essays_text")
    @Transaction
    fun getEssayList(): LiveData<List<Essay>>

    @Insert()
    fun insertEssayText(vararg text: EssayText)

    @Insert()
    fun insertImages(imgs: List<Image>)

    @Update()
    fun updateEssayText(vararg text: EssayText)

    @Update()
    fun updateImages(imgs: List<Image>)

    @Delete()
    fun delEssayText(vararg text: EssayText)

    @Delete
    fun delImages(imgs: List<Image>)
}