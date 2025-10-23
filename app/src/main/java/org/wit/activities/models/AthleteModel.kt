package org.wit.activities.models

import android.R
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AthleteModel(
    var id: Long = 0,
    var name: String = "",
    var description: String = "",
    var role: String = "All-rounder",
    var group: String = "",
    var personalBest: String = "",
    var country: String = "",
    var trainingGroup: String = "",
    var isActive: Boolean = true
) : Parcelable
