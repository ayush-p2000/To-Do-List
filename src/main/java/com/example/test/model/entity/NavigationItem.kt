/*The code in these page is written by Siyuan Peng*/

package com.example.test.model.entity

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation Bar :
 *
 * title
 * icon
 * Screen ID
 */

data class NavigationItem(
    val title : String,
    val filledIcon : ImageVector,
    val outlineIcon : ImageVector,
    val ScreenID : Int
) {
    @Composable
    fun iconStyle(selectedScreenID: Int) {
        if (selectedScreenID == ScreenID) {
            Icon(filledIcon, contentDescription = title)
        } else {
            Icon(outlineIcon, contentDescription = title)
        }
    }
}
