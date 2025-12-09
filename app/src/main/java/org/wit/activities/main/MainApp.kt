package org.wit.activities.main

import android.app.Application
import com.google.firebase.FirebaseApp
import org.wit.activities.models.AthleteJSONStore
import org.wit.activities.models.AthleteStore
import timber.log.Timber

class MainApp : Application() {

    lateinit var athletes: AthleteStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Athlete Tracker started")

        FirebaseApp.initializeApp(this)

        athletes = AthleteJSONStore(applicationContext)
    }
}
