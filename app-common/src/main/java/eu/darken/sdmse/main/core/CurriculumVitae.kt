package eu.darken.sdmse.main.core

import android.content.Context
import android.content.pm.PackageInfo
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.sdmse.common.BuildConfigWrap
import eu.darken.sdmse.common.coroutine.AppScope
import eu.darken.sdmse.common.coroutine.DispatcherProvider
import eu.darken.sdmse.common.datastore.createValue
import eu.darken.sdmse.common.datastore.value
import eu.darken.sdmse.common.debug.logging.Logging.Priority.INFO
import eu.darken.sdmse.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.sdmse.common.debug.logging.Logging.Priority.WARN
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.getPackageInfo
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.VersionFormatException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurriculumVitae @Inject constructor(
    @ApplicationContext private val context: Context,
    @AppScope private val appScope: CoroutineScope,
    json: Json,
    private val dispatcherProvider: DispatcherProvider,
) {

    private val Context.dataStore by preferencesDataStore(name = "curriculum_vitae")

    private val dataStore: DataStore<Preferences>
        get() = context.dataStore

    private val usPkgInfo: PackageInfo by lazy { context.getPackageInfo() }

    private val _updateHistory = dataStore.createValue("stats.update.history", emptyList<String>(), json)
    private val _installedFirst = dataStore.createValue<Instant?>("stats.install.first", null, json)
    val installedAt = _installedFirst.flow.filterNotNull()
    private val _launchedLast = dataStore.createValue<Instant?>("stats.launched.last", null, json)
    private val _launchedCount = dataStore.createValue("stats.launched.count", 0)
    private val _launchedCountBeta = dataStore.createValue("stats.launched.beta.count", 0)

    fun updateAppLaunch() = appScope.launch(dispatcherProvider.IO) {
        log(TAG, VERBOSE) { "updateAppLaunch()" }
        updateInstalledAt()
        updateLaunchTime()
        updateLaunchCount()
        if (BuildConfigWrap.BUILD_TYPE != BuildConfigWrap.BuildType.RELEASE) {
            updateLaunchCountBeta()
        }
        updateVersionHistory()
    }

    private suspend fun updateInstalledAt() {
        val savedInstalledAt = _installedFirst.value()
        log(TAG, VERBOSE) { "updateInstalledAt(): saved: $savedInstalledAt" }

        val systemInstalledAt = usPkgInfo.firstInstallTime.let { Instant.ofEpochMilli(it) }
        log(TAG, VERBOSE) { "updateInstalledAt(): system: $systemInstalledAt" }

        if (savedInstalledAt == null) {
            log(TAG) { "updateInstalledAt(): Saving install time: $systemInstalledAt" }
            _installedFirst.value(systemInstalledAt)
        } else {
            log(TAG, VERBOSE) { "updateInstalledAt(): Keeping current install date." }
        }
    }

    private suspend fun updateLaunchTime() {
        val oldLaunchTime = _launchedLast.value()
        log(TAG) { "Last launch time was $oldLaunchTime" }
        _launchedLast.value(Instant.now())
    }

    private suspend fun updateLaunchCount() {
        val newLaunchCount = _launchedCount.value() + 1
        log(TAG) { "Launch count is $newLaunchCount" }
        _launchedCount.value(newLaunchCount)
    }

    private suspend fun updateLaunchCountBeta() {
        val newLaunchCount = _launchedCountBeta.value() + 1
        log(TAG) { "Launch BETA count is $newLaunchCount" }
        _launchedCountBeta.value(newLaunchCount)
    }

    val history = _updateHistory.flow.map { versions ->
        versions.mapNotNull {
            try {
                Version.parse(it, false)
            } catch (_: VersionFormatException) {
                log(TAG, WARN) { "Invalid version format: $it out of $versions" }
                null
            }
        }
    }

    private suspend fun updateVersionHistory() {
        val history = _updateHistory.value()
        log(TAG) { "Current version history is $history" }

        val lastVersion = history.lastOrNull()
        val current = usPkgInfo.versionName!!
        if (lastVersion != current) {
            val versionHistory = history + current
            log(TAG) { "Update happened, new version history is $versionHistory" }
            _updateHistory.value(versionHistory)
        }
    }

    private val _openedLast = dataStore.createValue<Instant?>("stats.opened.last", null, json)
    private val _openedCount = dataStore.createValue("stats.opened.count", 0)

    fun updateAppOpened() = appScope.launch {
        log(TAG, VERBOSE) { "updateAppOpened()" }
        updateOpenedTime()
        updateOpenedCount()
    }

    private suspend fun updateOpenedTime() {
        val oldOpenedTime = _openedLast.value()
        log(TAG) { "Last open was $oldOpenedTime" }
        _openedLast.value(Instant.now())
    }

    private suspend fun updateOpenedCount() {
        val newOpenedcount = _openedCount.value() + 1
        log(TAG) { "Open count is $newOpenedcount" }
        _openedCount.value(newOpenedcount)
    }

    // A single anniversary celebration window: which anniversary it is ([ordinal], 1 = first),
    // the calendar year that window is anchored in ([nominalYear]) and the date it started on.
    data class AnniversaryOccurrence(
        val ordinal: Int,
        val nominalYear: Int,
        val date: LocalDate,
    )

    companion object {
        internal val TAG = logTag("Debug", "CurriculumVitae")

        // The anniversary celebration window is the nominal anniversary date plus the following 14
        // days, so an install date that falls on a day the user doesn't open the app isn't missed.
        // Anchored on the NOMINAL date, not the calendar year: a late-December anniversary keeps the
        // same occurrence into early January. Feb-29 installs celebrate on Feb 28 in non-leap years
        // (withYear resolves the date that way) and on Feb 29 in leap years.
        fun anniversaryOccurrenceOf(installDate: LocalDate, today: LocalDate): AnniversaryOccurrence? {
            for (candidateYear in listOf(today.year, today.year - 1)) {
                val nominalDate = installDate.withYear(candidateYear)
                val days = ChronoUnit.DAYS.between(nominalDate, today)
                val ordinal = candidateYear - installDate.year
                if (days in 0..14 && ordinal >= 1) {
                    return AnniversaryOccurrence(
                        ordinal = ordinal,
                        nominalYear = candidateYear,
                        date = nominalDate,
                    )
                }
            }
            return null
        }
    }
}