package org.wit.activities.models

import android.content.Context

class AthleteJSONStore(private val context: Context) : AthleteStore {

    private var athletes = mutableListOf<AthleteModel>()

    override fun findAll(callback: (List<AthleteModel>) -> Unit) {
        callback(athletes)
    }

    override fun create(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        athletes.add(athlete)
        callback(true)
    }

    override fun update(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        val index = athletes.indexOfFirst { it.id == athlete.id }
        if (index >= 0) {
            athletes[index] = athlete
            callback(true)
        } else callback(false)
    }

    override fun delete(athlete: AthleteModel, callback: (Boolean) -> Unit) {
        val removed = athletes.removeIf { it.id == athlete.id }
        callback(removed)
    }
}
