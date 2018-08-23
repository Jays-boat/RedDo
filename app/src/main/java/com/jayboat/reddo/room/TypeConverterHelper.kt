package com.jayboat.reddo.room

import android.arch.persistence.room.TypeConverter
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType

class TypeConverterHelper {
    @TypeConverter
    fun entryType2int(type: EntryType) = type.ordinal

    @TypeConverter
    fun int2EntryType(index: Int) = EntryType.values()[index]


}