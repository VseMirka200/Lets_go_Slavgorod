package com.example.slavgorodbus.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slavgorodbus.R
import com.example.slavgorodbus.ui.viewmodel.AppTheme
import com.example.slavgorodbus.ui.viewmodel.NotificationMode
import com.example.slavgorodbus.ui.viewmodel.NotificationSettingsViewModel
import com.example.slavgorodbus.ui.viewmodel.ThemeViewModel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel(),
    notificationSettingsViewModel: NotificationSettingsViewModel = viewModel(),
    onNavigateToAbout: () -> Unit
) {
    val currentAppTheme by themeViewModel.currentTheme.collectAsState()
    var showThemeDropdown by remember { mutableStateOf(false) }
    val themeOptions = remember { AppTheme.entries.toTypedArray() }

    val currentNotificationMode by notificationSettingsViewModel.currentNotificationMode.collectAsState()
    var showNotificationModeDropdown by remember { mutableStateOf(false) }
    val notificationModeOptions = remember { NotificationMode.entries.toTypedArray() }

    var showSelectDaysDialog by remember { mutableStateOf(false) }
    val selectedDaysFromVM by notificationSettingsViewModel.selectedNotificationDays.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_screen_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_section_theme_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ThemeSettingsCard(
                currentAppTheme = currentAppTheme,
                showThemeDropdown = showThemeDropdown,
                onShowThemeDropdownChange = { showThemeDropdown = it },
                themeOptions = themeOptions,
                onThemeSelected = { theme ->
                    themeViewModel.setTheme(theme)
                }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_section_notifications_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            NotificationSettingsCard(
                currentNotificationMode = currentNotificationMode,
                showNotificationModeDropdown = showNotificationModeDropdown,
                onShowNotificationModeDropdownChange = { showNotificationModeDropdown = it },
                notificationModeOptions = notificationModeOptions,
                onNotificationModeSelected = { mode ->
                    if (mode == NotificationMode.SELECTED_DAYS) {
                        showSelectDaysDialog = true
                    } else {
                        notificationSettingsViewModel.setNotificationMode(mode)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_section_about_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AboutSettingsCard(
                onNavigateToAbout = onNavigateToAbout
            )
        }
    }

    if (showSelectDaysDialog) {
        SelectDaysDialog(
            currentlySelectedDays = selectedDaysFromVM,
            onDismissRequest = { showSelectDaysDialog = false },
            onConfirm = { newSelectedDays ->
                showSelectDaysDialog = false
                notificationSettingsViewModel.setSelectedNotificationDays(newSelectedDays)
                Log.d("SettingsScreen", "Selected days confirmed: $newSelectedDays")
            }
        )
    }
}

@Composable
fun ThemeSettingsCard(
    currentAppTheme: AppTheme,
    showThemeDropdown: Boolean,
    onShowThemeDropdownChange: (Boolean) -> Unit,
    themeOptions: Array<AppTheme>,
    onThemeSelected: (AppTheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowThemeDropdownChange(true) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        contentDescription = stringResource(R.string.settings_appearance_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.settings_appearance_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(
                            when (currentAppTheme) {
                                AppTheme.SYSTEM -> R.string.theme_system
                                AppTheme.LIGHT -> R.string.theme_light
                                AppTheme.DARK -> R.string.theme_dark
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(R.string.settings_select_theme_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(
                expanded = showThemeDropdown,
                onDismissRequest = { onShowThemeDropdownChange(false) },
            ) {
                themeOptions.forEach { theme ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    when (theme) {
                                        AppTheme.SYSTEM -> R.string.theme_system
                                        AppTheme.LIGHT -> R.string.theme_light
                                        AppTheme.DARK -> R.string.theme_dark
                                    }
                                ),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onThemeSelected(theme)
                            onShowThemeDropdownChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsCard(
    currentNotificationMode: NotificationMode,
    showNotificationModeDropdown: Boolean,
    onShowNotificationModeDropdownChange: (Boolean) -> Unit,
    notificationModeOptions: Array<NotificationMode>,
    onNotificationModeSelected: (NotificationMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowNotificationModeDropdownChange(true) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.settings_notification_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.settings_notification_mode_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(
                            when (currentNotificationMode) {
                                NotificationMode.WEEKDAYS -> R.string.notification_mode_weekdays
                                NotificationMode.ALL_DAYS -> R.string.notification_mode_all_days
                                NotificationMode.SELECTED_DAYS -> R.string.notification_mode_selected_days
                                NotificationMode.DISABLED -> R.string.notification_mode_disabled
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(R.string.settings_select_notification_mode_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(
                expanded = showNotificationModeDropdown,
                onDismissRequest = { onShowNotificationModeDropdownChange(false) },
            ) {
                notificationModeOptions.forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    when (mode) {
                                        NotificationMode.WEEKDAYS -> R.string.notification_mode_weekdays
                                        NotificationMode.ALL_DAYS -> R.string.notification_mode_all_days
                                        NotificationMode.SELECTED_DAYS -> R.string.notification_mode_selected_days
                                        NotificationMode.DISABLED -> R.string.notification_mode_disabled
                                    }
                                ),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onNotificationModeSelected(mode)
                            onShowNotificationModeDropdownChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AboutSettingsCard(
    onNavigateToAbout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToAbout() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.settings_about_icon_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.about_screen_title),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectDaysDialog(
    currentlySelectedDays: Set<DayOfWeek>,
    onDismissRequest: () -> Unit,
    onConfirm: (Set<DayOfWeek>) -> Unit
) {
    val daysOfWeek = remember { DayOfWeek.entries.toList() }
    val tempSelectedDays = remember { mutableStateOf(currentlySelectedDays) }

    LaunchedEffect(currentlySelectedDays) {
        tempSelectedDays.value = currentlySelectedDays
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.dialog_select_days_title)) },
        text = {
            Column {
                daysOfWeek.forEach { day ->
                    val isChecked = tempSelectedDays.value.contains(day)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentSelection = tempSelectedDays.value.toMutableSet()
                                if (isChecked) {
                                    currentSelection.remove(day)
                                } else {
                                    currentSelection.add(day)
                                }
                                tempSelectedDays.value = currentSelection
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = day.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempSelectedDays.value) }) {
                Text(stringResource(R.string.dialog_button_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_button_cancel))
            }
        }
    )
}