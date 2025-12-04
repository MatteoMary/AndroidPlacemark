package org.wit.activities.models

import android.R
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


enum class Country(val code: String, val displayName: String) {
    IRELAND("IRL", "Ireland"),
    UNITED_KINGDOM("GBR", "United Kingdom"),
    UNITED_STATES("USA", "United States"),
    FRANCE("FRA", "France"),
    GERMANY("GER", "Germany");

}

@Parcelize
data class AthleteModel(
    var id: Long = 0,
    var name: String = "",
    var description: String = "",
    var role: String = "All-rounder",
    var group: String = "",
    var personalBest: String = "",
    var country: String = "",
    var isActive: Boolean = true
) : Parcelable

