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
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var role: String = "All-rounder",
    var group: String = "",
    var personalBestSeconds: Int? = null,
    var country: Country = Country.IRELAND,
    var isActive: Boolean = true,
    var ownerUsername: String = "",
    var event: String = "",
    var dateOfBirth: Long? = null,
    var location: Location = Location()


) : Parcelable

@Parcelize
data class Location(var lat: Double = 52.2464568,
                    var lng: Double = -7.1263403,
                    var zoom: Float = 15f) : Parcelable


