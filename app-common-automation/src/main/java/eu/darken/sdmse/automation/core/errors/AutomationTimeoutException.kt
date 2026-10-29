package eu.darken.sdmse.automation.core.errors

import eu.darken.sdmse.automation.R
import eu.darken.sdmse.common.ca.toCaString
import eu.darken.sdmse.common.error.HasLocalizedError
import eu.darken.sdmse.common.error.LocalizedError
import kotlinx.coroutines.TimeoutCancellationException

open class AutomationTimeoutException(
    cause: TimeoutCancellationException,
) : AutomationException(
    "SD Maid couldn't complete the necessary steps within the timelimit. This could mean that the app needs an adjustment for your device.",
    cause,
), HasLocalizedError {

    override fun getLocalizedError(): LocalizedError = LocalizedError(
        throwable = this,
        label = R.string.automation_error_timeout_title.toCaString(),
        description = R.string.automation_error_timeout_body.toCaString(),
    )

}
