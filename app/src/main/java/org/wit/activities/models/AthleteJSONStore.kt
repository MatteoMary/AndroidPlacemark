package org.wit.activities.models

import android.content.Context
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

    init {
        if (exists(JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<AthleteModel> = athletes

    override fun create(athlete: AthleteModel) {
        athlete.id = generateRandomId()
        athletes.add(athlete)
        serialize()
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
            serialize()
        }
    }

    override fun delete(athlete: AthleteModel) {
        athletes.removeIf { it.id == athlete.id }
        serialize()
    }

    private fun serialize() {
        val json = gson.toJson(athletes, listType)
        write(JSON_FILE, json)
    }

    private fun deserialize() {
        val json = read(JSON_FILE)
        athletes = gson.fromJson(json, listType) ?: mutableListOf()
    }

    private fun exists(filename: String): Boolean =
        File(context.filesDir, filename).exists()

    private fun write(filename: String, data: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { it.write(data.toByteArray()) }
    }

    private fun read(filename: String): String =
        context.openFileInput(filename).bufferedReader().use { it.readText() }
}
