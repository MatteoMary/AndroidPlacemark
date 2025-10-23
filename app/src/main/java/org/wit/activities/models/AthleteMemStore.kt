package org.wit.activities.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class AthleteMemStore : AthleteStore {

    val athletes = ArrayList<AthleteModel>()

    override fun findAll(): List<AthleteModel> {
        return athletes
    }

    override fun create(athlete: AthleteModel) {
        athlete.id = getId()
        athletes.add(athlete)
        logAll()
    }

    override fun update(athlete: AthleteModel) {
        var foundAthlete: AthleteModel? = athletes.find { p -> p.id == athlete.id }
        if (foundAthlete != null) {
            foundAthlete.title = athlete.title
            foundAthlete.description = athlete.description
            foundAthlete.role = athlete.role
            logAll()
        }
    }

    override fun delete(athlete: AthleteModel) {
        athletes.removeIf { it.id == athlete.id }
        logAll()
    }
    private fun logAll() {
        athletes.forEach { i("$it") }
    }
}