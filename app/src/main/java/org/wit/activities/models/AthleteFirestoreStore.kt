package org.wit.activities.models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class AthleteFirestoreStore : AthleteStore {

    private val db = FirebaseFirestore.getInstance()
    private val athletesCollection = db.collection("athletes")


    override fun findAll(callback: (List<AthleteModel>) -> Unit) {
        athletesCollection
            .get()
            .addOnSuccessListener { result ->

                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(AthleteModel::class.java)?.apply {
                        id = doc.id
                    }
                }
                Log.i("AthleteFirestoreStore", "Loaded ${list.size} athletes from Firestore")
                callback(list)
            }
            .addOnFailureListener { e ->
                Log.e("AthleteFirestoreStore", "findAll failed", e)
                callback(emptyList())
            }
    }

    override fun create(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        val doc = athletesCollection.document()
        athlete.id = doc.id

        doc.set(athlete)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }

        callback(true)
    }


    override fun update(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        val id = athlete.id
        if (id.isBlank()) {
            callback(false)
            return
        }

        athletesCollection.document(id)
            .set(athlete)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }

        callback(true)
    }


    override fun delete(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        val id = athlete.id
        if (id.isBlank()) {
            callback(false)
            return
        }

        athletesCollection.document(id)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }

        callback(true)
    }

}
