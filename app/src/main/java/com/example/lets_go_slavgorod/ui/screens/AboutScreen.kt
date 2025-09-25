package com.example.lets_go_slavgorod.ui.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.navigation.NavController
import com.example.lets_go_slavgorod.BuildConfig
import com.example.lets_go_slavgorod.R
import com.example.lets_go_slavgorod.ui.components.SettingsSwipeableContainer
import com.example.lets_go_slavgorod.ui.navigation.Screen
import androidx.core.net.toUri

/**
 * Экран "О программе" - отображает информацию о приложении и разработчике
 * 
 * Содержит:
 * - Название приложения и версию
 * - Информацию о разработчике
 * - Ссылки на GitHub и Telegram
 * - Раздел поддержки разработчика с кнопками для благодарности
 * 
 * @param onBackClick callback для обработки нажатия кнопки "Назад"
 * @param modifier модификатор для настройки внешнего вида
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController? = null,
    onBackClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current

    val developerSectionTitleText = stringResource(id = R.string.developer_section_title) // "Разработал: VseMirka200"

    val developerGitHubUrl = stringResource(id = R.string.developer_github_url_value)
    val linkTextGitHub = stringResource(id = R.string.link_text_github)

    // Строки для обратной связи через Telegram
    val feedbackSectionTitle = stringResource(id = R.string.feedback_section_title)
    val feedbackLinkText = stringResource(id = R.string.feedback_link_text)
    val feedbackTelegramBotUsername = stringResource(id = R.string.feedback_telegram_bot_username)

    val appVersion = BuildConfig.VERSION_NAME
    val feedbackTelegramUrl = "https://t.me/$feedbackTelegramBotUsername"

    SettingsSwipeableContainer(
        onSwipeToNext = {
            // Свайп влево - переход к избранному
            Log.d("AboutScreen", "Swipe left detected, navigating to FavoriteTimes")
            if (navController != null) {
                try {
                    navController.navigate(Screen.FavoriteTimes.route)
                    Log.d("AboutScreen", "Navigation to FavoriteTimes completed")
                } catch (e: Exception) {
                    Log.e("AboutScreen", "Navigation to FavoriteTimes failed", e)
                }
            } else {
                Log.e("AboutScreen", "navController is null, cannot navigate")
            }
        },
        onSwipeToPrevious = {
            // Свайп вправо - переход к настройкам
            Log.d("AboutScreen", "Swipe right detected, navigating to Settings")
            if (navController != null) {
                try {
                    navController.navigate(Screen.Settings.route)
                    Log.d("AboutScreen", "Navigation to Settings completed")
                } catch (e: Exception) {
                    Log.e("AboutScreen", "Navigation to Settings failed", e)
                }
            } else {
                Log.e("AboutScreen", "navController is null, cannot navigate")
            }
        },
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.about_screen_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
            Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                    .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            ) {
            // Раздел Информация о приложении
                Text(
                text = "Информация о приложении",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AppInfoCard(
                appName = stringResource(id = R.string.app_name),
                developer = developerSectionTitleText,
                version = appVersion
            )

            Spacer(Modifier.height(24.dp))

                // Раздел Ссылки
                Text(
                text = "Ссылки",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinksCard(
                navController = navController,
                githubUrl = developerGitHubUrl,
                vkUrl = "https://vk.com/vsemirka200",
                telegramUrl = feedbackTelegramUrl
            )
                
            Spacer(Modifier.height(24.dp))

                // Раздел Обратная связь
                Text(
                text = "Обратная связь",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FeedbackCard(navController = navController)
            
            Spacer(Modifier.height(24.dp))
            
            // Раздел Поддержка разработчика
            Text(
                text = "Поддержка разработчика",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SupportCard(navController = navController)
        }
    }
    }
}

/**
 * Карточка с информацией о приложении
 */
@Composable
private fun AppInfoCard(
    appName: String,
    developer: String,
    version: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Информация о приложении",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = developer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            Text(
                        text = "Версия: $version",
                style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Карточка со ссылками
 */
@Composable
private fun LinksCard(
    navController: NavController?,
    githubUrl: String,
    vkUrl: String,
    telegramUrl: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LinkItem(
                navController = navController,
                text = "GitHub",
                url = githubUrl,
                icon = Icons.Default.Link
            )
            Spacer(Modifier.height(12.dp))
            LinkItem(
                navController = navController,
                text = "Вконтакте",
                url = vkUrl,
                icon = Icons.Default.Link
            )
        }
    }
}

/**
 * Элемент ссылки в карточке
 */
@Composable
private fun LinkItem(
    navController: NavController?,
    text: String,
    url: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (navController != null) {
                    // Открываем ссылку в WebView внутри приложения
                    val route = Screen.WebView.createRoute(url, text)
                    navController.navigate(route)
                } else {
                    // Fallback: открываем в браузере
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("AboutScreen", "Could not open URL: $url", e)
                    }
                }
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    }
}

/**
 * Карточка поддержки разработчика
 */
@Composable
private fun SupportCard(
    navController: NavController?
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Если приложение вам нравится, вы можете поддержать его разработку:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Кнопки поддержки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка "Донат"
                OutlinedButton(
                    onClick = {
                        if (navController != null) {
                            // Открываем ссылку в WebView внутри приложения
                            val route = Screen.WebView.createRoute("https://pay.cloudtips.ru/p/9bc2de2e", "Поддержать разработчика")
                            navController.navigate(route)
                        } else {
                            // Fallback: открываем в браузере
                            val intent = Intent(Intent.ACTION_VIEW, "https://pay.cloudtips.ru/p/9bc2de2e".toUri())
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("AboutScreen", "Could not open CloudTips", e)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Поддержать")
                }
                
                // Кнопка "Оценить"
                OutlinedButton(
                    onClick = {
                        if (navController != null) {
                            // Открываем ссылку в WebView внутри приложения
                            val route = Screen.WebView.createRoute("https://github.com/VseMirka200/lets_go_slavgorod", "GitHub")
                            navController.navigate(route)
                        } else {
                            // Fallback: открываем в браузере
                            val intent = Intent(Intent.ACTION_VIEW, "https://github.com/VseMirka200/lets_go_slavgorod".toUri())
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("AboutScreen", "Could not open GitHub", e)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Оценить")
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💡 Способы поддержки:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Оставить отзыв в Telegram\n• Поставить звезду на GitHub\n• Поделиться с друзьями\n• Сообщить об ошибках",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Карточка обратной связи
 */
@Composable
private fun FeedbackCard(
    navController: NavController?
) {
    val context = LocalContext.current
    val feedbackTelegramBotUsername = stringResource(id = R.string.feedback_telegram_bot_username)
    val telegramBotUrl = "https://t.me/$feedbackTelegramBotUsername"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Есть вопросы или предложения? Напишите нам в Telegram!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Кнопка обратной связи
            Button(
                onClick = {
                    try {
                        // Пытаемся открыть в приложении Telegram
                        val telegramIntent = Intent(Intent.ACTION_VIEW, telegramBotUrl.toUri())
                        telegramIntent.setPackage("org.telegram.messenger")
                        context.startActivity(telegramIntent)
                    } catch (e: Exception) {
                        try {
                            // Fallback: открываем в браузере
                            val intent = Intent(Intent.ACTION_VIEW, telegramBotUrl.toUri())
                            context.startActivity(intent)
                        } catch (e2: Exception) {
                            Log.e("AboutScreen", "Could not open Telegram bot", e2)
                            // Показываем сообщение пользователю
                            android.widget.Toast.makeText(
                                context,
                                "Не удалось открыть Telegram. Установите приложение Telegram.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Feedback,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Обратная связь")
            }
            
            Spacer(Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💬 Что можно сообщить:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Ошибки в приложении\n" +
                                "• Предложения по улучшению\n" +
                                "• Вопросы по расписанию",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}


/**
 * Компонент для отображения кликабельной ссылки
 * 
 * Отображает текст как ссылку с подчеркиванием и открывает URL при нажатии.
 * Оптимизирован для вертикального списка ссылок с выравниванием по левому краю.
 * 
 * @param text текст ссылки для отображения
 * @param url URL для открытия при нажатии
 * @param modifier модификатор для настройки внешнего вида
 */
@Composable
private fun ClickableLinkText(
    text: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        ),
        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
        modifier = modifier
            .clickable {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            try {
                localContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.w("AboutScreen", "No application can handle this URL: $url", e)
                android.widget.Toast.makeText(
                    localContext,
                    localContext.getString(R.string.error_no_browser) ?: "Нет браузера",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e("AboutScreen", "Could not open URL: $url", e)
                android.widget.Toast.makeText(
                    localContext,
                    localContext.getString(R.string.error_cant_open_link) ?: "Не удалось открыть ссылку",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
}
