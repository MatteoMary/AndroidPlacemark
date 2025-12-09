package org.wit.activities.adapters

import org.wit.activities.models.AthleteModel

interface AthleteListener {
    fun onAthleteClick(athlete: AthleteModel)
}
