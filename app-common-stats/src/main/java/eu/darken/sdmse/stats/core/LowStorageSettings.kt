package eu.darken.sdmse.stats.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.sdmse.common.datastore.createValue
import eu.darken.sdmse.common.debug.logging.logTag
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Settings for the low-storage warning, the widget's low-space flag and the storage forecast.
 *
 * These used to live in the (now removed) analyzer tool settings. They are kept here because the
 * low-space monitor, the home-screen widget and the storage forecast are all part of the stats
 * feature set and must survive without the analyzer tool.
 *
 * The DataStore name is intentionally kept as `settings_analyzer` so existing installs keep their
 * configured threshold and notification state across the upgrade.
 */
@Singleton
class LowStorageSettings @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.dataStore by preferencesDataStore(name = "settings_analyzer")

    val dataStore: DataStore<Preferences>
        get() = context.dataStore

    /**
     * When free space drops to or below this, storage counts as running out.
     * `null` means automatic, see [LowStorage.resolveThreshold].
     */
    val lowStorageThresholdBytes = dataStore.createValue<Long?>("storage.low.threshold.bytes", null)

    /** Pro-gated: warn before the device runs out of space. */
    val lowSpaceNotificationEnabled = dataStore.createValue("storage.low.notification.enabled", false)

    /**
     * Transition latch: `true` means "a crossing into the warning band may notify".
     *
     * Runtime state, not a preference.
     */
    val lowSpaceNotificationArmed = dataStore.createValue("storage.low.notification.armed", true)

    val hintLowSpaceDismissed = dataStore.createValue("hint.lowspace.dismissed", false)

    companion object {
        internal val TAG = logTag("Stats", "LowStorageSettings")

        const val LOW_STORAGE_THRESHOLD_MIN = 100L * 1024 * 1024
        const val LOW_STORAGE_THRESHOLD_MAX = 32L * 1024 * 1024 * 1024
    }
}
