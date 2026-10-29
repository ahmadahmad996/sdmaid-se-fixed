package eu.darken.sdmse.common.compose.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import eu.darken.sdmse.common.compose.preview.Preview2
import eu.darken.sdmse.common.compose.preview.PreviewWrapper

@Composable
fun SettingsPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    iconTint: Color? = null,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    focusKey: String? = null,
) {
    SettingsBaseItem(
        icon = icon,
        iconPainter = iconPainter,
        iconTint = iconTint,
        title = title,
        onClick = onClick,
        onLongClick = onLongClick,
        focusKey = focusKey,
        modifier = modifier,
        subtitle = subtitle,
        value = value,
        enabled = enabled,
    )
}

@Preview2
@Composable
private fun SettingsPreferenceItemPreview() {
    PreviewWrapper {
        SettingsPreferenceItem(
            icon = Icons.TwoTone.Settings,
            title = "Settings",
            subtitle = "General settings",
            onClick = {},
            value = "Value",
        )
    }
}

@Composable
private fun SettingsPreferenceItemLongValuePreview() {
    PreviewWrapper {
        SettingsPreferenceItem(
            icon = Icons.TwoTone.Settings,
            title = "Wykrywanie systemu operacyjnego",
            value = "Automatyczne (domyślnie)",
            subtitle = "Automatyzacja oparta na usłudze ułatwień dostępu może się nie powieść, " +
                "jeśli SD Maid nie wykryje poprawnie systemu operacyjnego.",
            onClick = {},
        )
    }
}
