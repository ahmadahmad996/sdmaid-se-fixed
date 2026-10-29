package eu.darken.sdmse.main.ui.onboarding.privacy

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.sdmse.common.SdmSeLinks
import eu.darken.sdmse.common.WebpageTool
import eu.darken.sdmse.common.coroutine.DispatcherProvider
import eu.darken.sdmse.common.datastore.value
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.uix.ViewModel4
import eu.darken.sdmse.common.updater.UpdateChecker
import eu.darken.sdmse.main.core.GeneralSettings
import eu.darken.sdmse.main.ui.navigation.OnboardingSetupRoute
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class OnboardingPrivacyViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val generalSettings: GeneralSettings,
    private val webpageTool: WebpageTool,
    private val updateChecker: UpdateChecker,
) : ViewModel4(dispatcherProvider = dispatcherProvider, tag = TAG) {

    val state: StateFlow<State> = combine(
        generalSettings.isUpdateCheckEnabled.flow,
        flow { emit(updateChecker.isCheckSupported()) },
    ) { isUpdateCheckEnabled, isUpdateCheckSupported ->
        State(
            isUpdateCheckEnabled = isUpdateCheckEnabled,
            isUpdateCheckSupported = isUpdateCheckSupported,
        )
    }.safeStateIn(
        initialValue = State(),
        onError = { State() },
    )

    fun onContinue() {
        navTo(OnboardingSetupRoute)
    }

    fun goPrivacyPolicy() {
        log(TAG) { "goPrivacyPolicy()" }
        webpageTool.open(SdmSeLinks.PRIVACY_POLICY)
    }

    fun toggleUpdateCheck() {
        log(TAG) { "toggleUpdateCheck()" }
        launch { generalSettings.isUpdateCheckEnabled.value(!generalSettings.isUpdateCheckEnabled.value()) }
    }

    data class State(
        val isUpdateCheckEnabled: Boolean = true,
        val isUpdateCheckSupported: Boolean = false,
    )

    companion object {
        private val TAG = logTag("Onboarding", "Privacy", "ViewModel")
    }
}
