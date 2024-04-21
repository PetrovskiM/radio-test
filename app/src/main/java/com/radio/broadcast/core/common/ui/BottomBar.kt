package com.radio.broadcast.core.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
internal fun BottomBar(
    screens: ImmutableList<TopLevelDestination>,
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = DIVIDER))
        NavigationBar {
            screens.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { navigateToTopLevelDestination(screen) },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (isSelected) {
                                    screen.selectedRes
                                } else {
                                    screen.unselectedRes
                                }
                            ),
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = stringResource(id = screen.labelRes))
                    },
                )
            }
        }
    }
}

private const val DIVIDER = 0.32f

fun NavController.navigateToBottomBarDestination(topLevelDestination: TopLevelDestination) {
    navigate(topLevelDestination.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}
