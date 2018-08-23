package com.jayboat.reddo

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.jayboat.reddo.room.RedDataBase

lateinit var appContext: Context
lateinit var redDataBase: RedDataBase

class App : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        appContext = base
        redDataBase = Room.databaseBuilder(base, RedDataBase::class.java, RedDataBase.NAME).build()
    }
}