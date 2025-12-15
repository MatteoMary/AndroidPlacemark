package org.wit.activities.models

interface AthleteStore {
    fun findAll(callback: (List<AthleteModel>) -> Unit)
    fun create(athlete: AthleteModel, callback: (Boolean) -> Unit)
    fun update(athlete: AthleteModel, callback: (Boolean) -> Unit)
    fun delete(athlete: AthleteModel, callback: (Boolean) -> Unit)
}
