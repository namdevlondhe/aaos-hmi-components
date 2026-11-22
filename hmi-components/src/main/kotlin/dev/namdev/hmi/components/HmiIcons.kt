package dev.namdev.hmi.components

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/** Icons the kit needs that are not in material-icons-core (avoids the multi-megabyte extended set). */
object HmiIcons {
    /** Material "remove" (minus) glyph. */
    val Minus: ImageVector by lazy {
        materialIcon(name = "HmiIcons.Minus") {
            materialPath {
                moveTo(19f, 13f)
                horizontalLineTo(5f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(2f)
                close()
            }
        }
    }
}
