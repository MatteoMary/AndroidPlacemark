package org.wit.activities.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AthleteModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var role: String = "All-rounder"
) : Parcelable