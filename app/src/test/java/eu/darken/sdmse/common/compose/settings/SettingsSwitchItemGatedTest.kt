package eu.darken.sdmse.common.compose.settings

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import eu.darken.sdmse.common.compose.preview.PreviewWrapper
import org.junit.Assert.assertEquals
import org.junit.Test
import testhelpers.compose.BaseComposeRobolectricTest

class SettingsSwitchItemGatedTest : BaseComposeRobolectricTest() {

    @Test
    fun `row tap invokes onCheckedChange with the toggled value`() {
        var received: Boolean? = null
        composeRule.setContent {
            PreviewWrapper {
                SettingsSwitchItem(
                    title = "Normal feature",
                    subtitle = null,
                    checked = false,
                    onCheckedChange = { received = it },
                )
            }
        }

        composeRule.onNodeWithText("Normal feature").performClick()
        composeRule.runOnIdle { assertEquals(true, received) }
    }
}
