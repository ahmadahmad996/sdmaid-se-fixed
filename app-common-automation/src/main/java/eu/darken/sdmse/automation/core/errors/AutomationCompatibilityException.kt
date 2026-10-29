package eu.darken.sdmse.automation.core.errors

import eu.darken.sdmse.automation.R
import eu.darken.sdmse.common.ca.CaString
import eu.darken.sdmse.common.ca.caString
import eu.darken.sdmse.common.ca.toCaString
import eu.darken.sdmse.common.error.HasLocalizedError
import eu.darken.sdmse.common.error.LocalizedError
import eu.darken.sdmse.setup.SetupModule
import eu.darken.sdmse.setup.SetupRoute
import eu.darken.sdmse.setup.SetupScreenOptions

open class AutomationCompatibilityException(
    override val message: String = "SD Maid couldn’t figure out the screen layout. If this keeps happening, your language or setup might not be fully supported. Check for updates or try again.",
    private val additionalHint: CaString? = null,
    val suggestAdbSetup: Boolean = false,
) : AutomationException(), HasLocalizedError {

    override fun getLocalizedError(): LocalizedError = LocalizedError(
        throwable = this,
        label = R.string.automation_error_compatibility_title.toCaString(),
        description = caString {
            val sb = StringBuilder()
            sb.append(getString(R.string.automation_error_compatibility_body))
            additionalHint?.let { hint ->
                sb.append("\n\n")
                sb.append(hint.get(this))
            }
            if (suggestAdbSetup) {
                sb.append("\n\n")
                sb.append(getString(R.string.automation_error_compatibility_adb_hint))
            }
            sb.toString()
        },
        fixActionLabel = eu.darken.sdmse.common.R.string.setup_title.toCaString().takeIf { suggestAdbSetup },
        fixActionRoute = SetupRoute(
            options = SetupScreenOptions(
                showCompleted = true,
                typeFilter = setOf(SetupModule.Type.SHIZUKU),
            ),
        ).takeIf { suggestAdbSetup },
    )

}
