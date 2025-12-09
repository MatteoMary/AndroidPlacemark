package org.wit.activities.models

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File
import java.lang.reflect.Type
import kotlin.random.Random

private const val JSON_FILE = "athletes.json"
private val gson = GsonBuilder().setPrettyPrinting().create()
private val listType: Type =
    object : com.google.gson.reflect.TypeToken<MutableList<AthleteModel>>() {}.type

fun generateRandomId(): Long = Random.nextLong()

class AthleteJSONStore(private val context: Context) : AthleteStore {

    private var athletes: MutableList<AthleteModel> = mutableListOf()

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("athletes")

    init {
        if (exists(JSON_FILE)) {
            safeDeserialize()
        }

        fetchFromCloud()
    }

    override fun findAll(): List<AthleteModel> = athletes

    override fun create(athlete: AthleteModel) {
        if (athlete.id == 0L) {
            athlete.id = generateRandomId()
        }
        athletes.add(athlete)
        serialize()
        syncUpsert(athlete)
    }

    override fun update(athlete: AthleteModel) {
        val found = athletes.find { it.id == athlete.id }
        if (found != null) {
            found.name = athlete.name
            found.description = athlete.description
            found.role = athlete.role
            found.group = athlete.group
            found.personalBestSeconds = athlete.personalBestSeconds
            found.country = athlete.country
            found.isActive = athlete.isActive
            found.ownerUsername = athlete.ownerUsername
            found.event = athlete.event
            serialize()
            syncUpsert(found)
        }
    }

    override fun delete(athlete: AthleteModel) {
        athletes.removeIf { it.id == athlete.id }
        serialize()
        syncDelete(athlete)
    }


    private fun serialize() {
        val json = gson.toJson(athletes, listType)
        write(JSON_FILE, json)
    }

    private fun safeDeserialize() {
        try {
            val json = read(JSON_FILE)
            athletes = gson.fromJson(json, listType) ?: mutableListOf()
        } catch (e: Exception) {
            Timber.e(e, "Error deserializing local JSON, starting with empty list")
            athletes = mutableListOf()
        }
    }

    private fun exists(filename: String): Boolean =
        File(context.filesDir, filename).exists()

    private fun write(filename: String, data: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE)
            .use { it.write(data.toByteArray()) }
    }

    private fun read(filename: String): String =
        context.openFileInput(filename).bufferedReader().use { it.readText() }

    private fun fetchFromCloud() {
        collection.get()
            .addOnSuccessListener { snapshot ->
                val cloudAthletes = snapshot.toObjects(AthleteModel::class.java)
                Timber.i("Loaded ${cloudAthletes.size} athletes from Firestore")

                athletes.clear()
                athletes.addAll(cloudAthletes)
                serialize()
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error fetching athletes from Firestore")
            }
    }

    private fun syncUpsert(athlete: AthleteModel) {
        collection
            .document(athlete.id.toString())
            .set(athlete)
            .addOnSuccessListener {
                Timber.i("Synced athlete ${athlete.id} to Firestore")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error syncing athlete ${athlete.id} to Firestore")
            }
    }

    private fun syncDelete(athlete: AthleteModel) {
        collection
            .document(athlete.id.toString())
            .delete()
            .addOnSuccessListener {
                Timber.i("Deleted athlete ${athlete.id} from Firestore")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error deleting athlete ${athlete.id} from Firestore")
            }
    }
}
