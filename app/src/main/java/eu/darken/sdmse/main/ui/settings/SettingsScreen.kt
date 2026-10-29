package eu.darken.sdmse.main.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ViewList
import androidx.compose.material.icons.twotone.Apps
import androidx.compose.material.icons.twotone.Recycling
import androidx.compose.material.icons.automirrored.twotone.ContactSupport
import androidx.compose.material.icons.twotone.BarChart
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.PhoneAndroid
import androidx.compose.material.icons.twotone.Policy
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.SettingsBackupRestore
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.material.icons.twotone.Update
import androidx.compose.material3.MaterialTheme
import eu.darken.sdmse.common.compose.layout.SdmScaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import eu.darken.sdmse.common.compose.preview.Preview2
import eu.darken.sdmse.common.compose.preview.PreviewWrapper
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.darken.sdmse.R
import eu.darken.sdmse.common.BuildConfigWrap
import eu.darken.sdmse.common.R as CommonR
import eu.darken.sdmse.common.compose.icons.Ghost
import eu.darken.sdmse.common.compose.layout.SdmTooltipIconButton
import eu.darken.sdmse.common.compose.icons.SdmIcons
import eu.darken.sdmse.common.compose.settings.SettingsCategoryHeader
import eu.darken.sdmse.common.compose.settings.SettingsPreferenceItem
import eu.darken.sdmse.common.error.ErrorEventHandler
import eu.darken.sdmse.common.navigation.NavigationEventHandler
import eu.darken.sdmse.appcleaner.ui.AppCleanerSettingsRoute
import eu.darken.sdmse.corpsefinder.ui.CorpseFinderSettingsRoute
import eu.darken.sdmse.exclusion.ui.ExclusionsListRoute
import eu.darken.sdmse.main.ui.navigation.AcknowledgementsRoute
import eu.darken.sdmse.main.ui.navigation.BackupRestoreRoute
import eu.darken.sdmse.main.ui.navigation.GeneralSettingsRoute
import eu.darken.sdmse.main.ui.navigation.SupportRoute
import eu.darken.sdmse.setup.SetupRoute
import eu.darken.sdmse.stats.ui.StatsSettingsRoute
import eu.darken.sdmse.systemcleaner.ui.SystemCleanerSettingsRoute
import eu.darken.sdmse.setup.SetupScreenOptions

@Composable
fun SettingsScreenHost(
    vm: SettingsViewModel = hiltViewModel(),
) {
    ErrorEventHandler(vm)
    NavigationEventHandler(vm)

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is SettingEvents.ShowVersionInfo -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.info,
                        actionLabel = context.getString(CommonR.string.general_copy_action),
                        duration = SnackbarDuration.Indefinite,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        vm.copyVersionInfos()
                    }
                }
            }
        }
    }

    SettingsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateUp = vm::navUp,
        onExclusionsClick = { vm.navTo(ExclusionsListRoute) },
        onGeneralSettingsClick = { vm.navTo(GeneralSettingsRoute) },
        onSetupClick = { vm.navTo(SetupRoute(options = SetupScreenOptions(showCompleted = true))) },
        onSupportClick = { vm.navTo(SupportRoute) },
        onAcknowledgementsClick = { vm.navTo(AcknowledgementsRoute) },
        onPrivacyClick = vm::openPrivacyPolicy,
        onChangelogClick = { vm.openWebsite("https://sdmse.darken.eu/changelog") },
        onChangelogLongClick = vm::showVersionInfos,
        onToolSettingsClick = { tool ->
            val route = when (tool) {
                "CorpseFinder" -> CorpseFinderSettingsRoute
                "SystemCleaner" -> SystemCleanerSettingsRoute
                "AppCleaner" -> AppCleanerSettingsRoute
                else -> return@SettingsScreen
            }
            vm.navTo(route)
        },
        onStatsClick = { vm.navTo(StatsSettingsRoute) },
        onBackupRestoreClick = { vm.navTo(BackupRestoreRoute) },
    )
}

@Composable
internal fun SettingsScreen(
    state: SettingsViewModel.State = SettingsViewModel.State(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateUp: () -> Unit = {},
    onExclusionsClick: () -> Unit = {},
    onGeneralSettingsClick: () -> Unit = {},
    onSetupClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onAcknowledgementsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onChangelogClick: () -> Unit = {},
    onChangelogLongClick: () -> Unit = {},
    onToolSettingsClick: (String) -> Unit = {},
    onStatsClick: () -> Unit = {},
    onBackupRestoreClick: () -> Unit = {},
) {
    SdmScaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(CommonR.string.general_settings_title)) },
                navigationIcon = {
                    SdmTooltipIconButton(
                        icon = Icons.AutoMirrored.TwoTone.ArrowBack,
                        label = stringResource(CommonR.string.general_navigate_up_action),
                        onClick = onNavigateUp,
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            // Tools category
            item { SettingsCategoryHeader(text = stringResource(CommonR.string.settings_category_tools_label)) }


            // Device category
            item { SettingsCategoryHeader(text = stringResource(CommonR.string.general_device_label)) }

            item {
                SettingsPreferenceItem(
                    icon = Icons.TwoTone.SettingsBackupRestore,
                    title = stringResource(R.string.backup_restore_title),
                    subtitle = stringResource(R.string.backup_restore_desc),
                    onClick = onBackupRestoreClick,
                )
            }
            item {
                val setupTint: Color? = if (state.setupDone == false) MaterialTheme.colorScheme.tertiary else null
                SettingsPreferenceItem(
                    icon = Icons.TwoTone.PhoneAndroid,
                    iconTint = setupTint,
                    title = stringResource(CommonR.string.setup_title),
                    subtitle = stringResource(R.string.setup_forcedshow_summary),
                    onClick = onSetupClick,
                )
            }

            // Other category
            item { SettingsCategoryHeader(text = stringResource(R.string.settings_category_other_label)) }

            item {
                SettingsPreferenceItem(
                    icon = Icons.AutoMirrored.TwoTone.ContactSupport,
                    title = stringResource(R.string.settings_support_label),
                    onClick = onSupportClick,
                )
            }
            item {
                SettingsPreferenceItem(
                    icon = Icons.TwoTone.Update,
                    title = stringResource(R.string.changelog_label),
                    subtitle = BuildConfigWrap.VERSION_DESCRIPTION,
                    onClick = onChangelogClick,
                    onLongClick = onChangelogLongClick,
                )
            }
            item {
                SettingsPreferenceItem(
                    icon = Icons.TwoTone.Favorite,
                    title = stringResource(R.string.settings_acknowledgements_label),
                    subtitle = stringResource(CommonR.string.general_thank_you_label),
                    onClick = onAcknowledgementsClick,
                )
            }
            item {
                SettingsPreferenceItem(
                    icon = Icons.TwoTone.Policy,
                    title = stringResource(R.string.settings_privacy_policy_label),
                    subtitle = stringResource(R.string.settings_privacy_policy_desc),
                    onClick = onPrivacyClick,
                )
            }
        }
    }
}

@Preview2
@Composable
private fun SettingsScreenPreview() {
    PreviewWrapper {
        SettingsScreen()
    }
}
