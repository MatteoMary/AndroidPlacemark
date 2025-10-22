package org.wit.activities.models

interface AthleteStore {
    fun findAll(): List<AthleteModel>
    fun create(athlete: AthleteModel)
    fun update(athlete: AthleteModel)
}
