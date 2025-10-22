package org.wit.activities.main

import android.app.Application
import org.wit.activities.models.AthleteMemStore
import org.wit.activities.models.AthleteStore
import timber.log.Timber

class MainApp : Application() {
    lateinit var athletes: AthleteStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        athletes = AthleteMemStore()
    }
}
