package org.wit.activities.main

import android.app.Application
import org.wit.activities.models.AthleteModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val athletes = ArrayList<AthleteModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Athlete started")
//        athletes.add(AthleteModel("One", "About one..."))
//        athletes.add(AthleteModel("Two", "About two..."))
//        athletes.add(AthleteModel("Three", "About three..."))
    }
}
