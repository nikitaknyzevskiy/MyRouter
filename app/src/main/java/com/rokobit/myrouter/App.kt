package com.rokobit.myrouter

import android.app.Application
import com.rokobit.myrouter.data.MyDatabase
import ua.knyaz.androidme.database.MeDataBase

lateinit var myDatabase: MyDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        myDatabase = MeDataBase.Builder<MyDatabase>().apply {
            this.context(this@App)
            this.database(MyDatabase::class.java)
            this.name("MyRouter")
        }.build()
    }

}