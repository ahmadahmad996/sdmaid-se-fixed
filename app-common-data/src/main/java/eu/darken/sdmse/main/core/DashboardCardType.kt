package eu.darken.sdmse.main.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DashboardCardType {
    @SerialName("CORPSEFINDER") CORPSEFINDER,
    @SerialName("SYSTEMCLEANER") SYSTEMCLEANER,
    @SerialName("APPCLEANER") APPCLEANER,
    @SerialName("STATS") STATS,
}
