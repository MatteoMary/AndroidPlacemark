package org.wit.activities.main

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.wit.activities.models.AthleteFirestoreStore
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

        athletes = AthleteFirestoreStore()

        val db = FirebaseFirestore.getInstance()

        db.collection("athletes")
            .add(mapOf(
                "name" to "TEST ATHLETE",
                "ownerUsername" to "debug",
                "createdAt" to System.currentTimeMillis()
            ))
            .addOnSuccessListener {
                Log.i("FIRESTORE_TEST", "Write SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_TEST", "Write FAILED", e)
            }

    }
}
