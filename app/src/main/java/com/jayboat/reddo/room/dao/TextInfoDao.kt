package com.jayboat.reddo.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.jayboat.reddo.room.bean.TextInfo

@Dao
interface TextInfoDao {

    @Insert(onConflict = REPLACE)
    fun insertTextInfoList(infoList: List<TextInfo>)

    @Query("DELETE FROM text_info WHERE entry_id = :entryId")
    fun delTextInfoList(entryId: Int)

}