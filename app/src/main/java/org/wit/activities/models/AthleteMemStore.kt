package org.wit.activities.models

class AthleteMemStore : AthleteStore {

    private val athletes = mutableListOf<AthleteModel>()

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
        callback(athletes.removeIf { it.id == athlete.id })
    }
}
