package com.jayboat.reddo.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.jayboat.reddo.room.bean.Image

@Dao
interface ImageDao {

    @Insert(onConflict = REPLACE)
    fun insertImages(imgs: List<Image>)

    @Query("DELETE FROM image WHERE entry_id = :entryId")
    fun delImgs(entryId: Int)

}