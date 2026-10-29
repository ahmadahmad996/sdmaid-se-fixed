package eu.darken.sdmse.main.core

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ViewList
import androidx.compose.material.icons.twotone.BarChart
import androidx.compose.material.icons.twotone.Recycling
import androidx.compose.ui.graphics.vector.ImageVector
import eu.darken.sdmse.common.compose.icons.Ghost
import eu.darken.sdmse.common.compose.icons.SdmIcons
import eu.darken.sdmse.common.R as CommonR

@get:StringRes
val DashboardCardType.labelRes: Int
    get() = when (this) {
        DashboardCardType.CORPSEFINDER -> CommonR.string.corpsefinder_tool_name
        DashboardCardType.SYSTEMCLEANER -> CommonR.string.systemcleaner_tool_name
        DashboardCardType.APPCLEANER -> CommonR.string.appcleaner_tool_name
        DashboardCardType.STATS -> CommonR.string.stats_label
    }

val DashboardCardType.icon: ImageVector
    get() = when (this) {
        DashboardCardType.CORPSEFINDER -> SdmIcons.Ghost
        DashboardCardType.SYSTEMCLEANER -> Icons.AutoMirrored.TwoTone.ViewList
        DashboardCardType.APPCLEANER -> Icons.TwoTone.Recycling
        DashboardCardType.STATS -> Icons.TwoTone.BarChart
    }
