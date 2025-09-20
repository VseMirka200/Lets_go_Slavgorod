# ОТЧЕТ: АРХИТЕКТУРА СИСТЕМЫ ПРОВЕРКИ ОБНОВЛЕНИЙ

## 📋 ОБЗОР

Система проверки обновлений работает через **GitHub API** и интегрирована в приложение для автоматической и ручной проверки новых версий.

## 🔄 КАК ЭТО РАБОТАЕТ

### **1. Источник обновлений: GitHub API**

**URL API:** `https://api.github.com/repos/VseMirka200/Lets_go_Slavgorod/releases/latest`

**Что происходит:**
- Приложение отправляет HTTP GET запрос к GitHub API
- Получает JSON с информацией о последнем релизе
- Извлекает версию, описание и ссылку на скачивание

**Пример ответа GitHub API:**
```json
{
  "tag_name": "v1.0.4",
  "name": "Release 1.0.4",
  "body": "Исправления и улучшения...",
  "assets": [
    {
      "browser_download_url": "https://github.com/VseMirka200/Lets_go_Slavgorod/releases/download/v1.0.4/app-release.apk"
    }
  ]
}
```

### **2. Процесс проверки обновлений**

#### **Шаг 1: Проверка интернета**
```kotlin
private fun isInternetAvailable(): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}
```

#### **Шаг 2: HTTP запрос к GitHub**
```kotlin
val url = URL(GITHUB_API_URL)
val connection = url.openConnection() as HttpURLConnection
connection.requestMethod = "GET"
connection.setRequestProperty("User-Agent", USER_AGENT)
connection.connectTimeout = REQUEST_TIMEOUT
connection.readTimeout = REQUEST_TIMEOUT
```

#### **Шаг 3: Парсинг ответа**
```kotlin
val responseCode = connection.responseCode
when (responseCode) {
    HttpURLConnection.HTTP_OK -> {
        val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonResponse)
        
        val latestVersion = jsonObject.getString("tag_name").removePrefix("v")
        val downloadUrl = jsonObject.getJSONArray("assets")
            .getJSONObject(0)
            .getString("browser_download_url")
        val releaseNotes = jsonObject.getString("body")
    }
}
```

#### **Шаг 4: Сравнение версий**
```kotlin
// Преобразуем версию в числовой код для сравнения
// "1.0.3" -> 10003, "1.0.4" -> 10004
val versionCode = if (versionParts.size >= 3) {
    versionParts[0].toInt() * 10000 + 
    versionParts[1].toInt() * 100 + 
    versionParts[2].toInt()
} else {
    versionParts[0].toInt() * 10000 + 
    versionParts[1].toInt() * 100
}

// Сравниваем с текущей версией
val currentVersion = context.packageManager
    .getPackageInfo(context.packageName, 0).versionCode

if (versionCode > currentVersion) {
    // Есть обновление!
}
```

## 🏗️ АРХИТЕКТУРА СИСТЕМЫ

### **1. Основные компоненты**

#### **UpdateManager.kt**
- **Назначение:** Основной класс для работы с обновлениями
- **Функции:**
  - Проверка интернет-соединения
  - HTTP запросы к GitHub API
  - Парсинг JSON ответов
  - Сравнение версий
  - Запуск загрузки через браузер

#### **UpdatePreferences.kt**
- **Назначение:** Управление настройками обновлений
- **Функции:**
  - Сохранение настроек пользователя
  - Кэширование информации об обновлениях
  - Отслеживание времени последней проверки

#### **UpdateSettingsViewModel.kt**
- **Назначение:** Управление состоянием UI
- **Функции:**
  - Связь между UI и бизнес-логикой
  - Обработка пользовательских действий
  - Управление состоянием загрузки

### **2. Потоки данных**

```
GitHub API → UpdateManager → UpdatePreferences → UpdateSettingsViewModel → UI
     ↓              ↓              ↓                    ↓
  JSON ответ → Парсинг → Сохранение → Отображение пользователю
```

## ⚙️ РЕЖИМЫ РАБОТЫ

### **1. Автоматическая проверка**

**Когда запускается:**
- При запуске приложения (через 5 секунд)
- Только если включена в настройках

**Процесс:**
```kotlin
// В BusApplication.onCreate()
startAutomaticUpdateCheck()

private fun startAutomaticUpdateCheck() {
    applicationScope.launch {
        // Проверяем настройки пользователя
        val autoUpdateEnabled = updatePreferences.autoUpdateCheckEnabled.firstOrNull() ?: true
        
        if (!autoUpdateEnabled) return@launch
        
        // Ждем 5 секунд после запуска
        delay(5000)
        
        // Проверяем обновления
        val result = updateManager.checkForUpdatesWithResult()
        
        // Сохраняем результат
        if (result.success && result.update != null) {
            updatePreferences.setAvailableUpdate(...)
        }
    }
}
```

### **2. Ручная проверка**

**Когда запускается:**
- По нажатию кнопки "Проверить" в настройках
- В любое время по запросу пользователя

**Процесс:**
```kotlin
fun checkForUpdates() {
    viewModelScope.launch {
        _isCheckingUpdates.value = true
        _updateCheckError.value = null
        
        try {
            val result = updateManager.checkForUpdatesWithResult()
            // Обработка результата...
        } finally {
            _isCheckingUpdates.value = false
        }
    }
}
```

## 🔒 БЕЗОПАСНОСТЬ И НАДЕЖНОСТЬ

### **1. Обработка ошибок**

**Сетевые ошибки:**
- Таймаут запроса (10 секунд)
- Отсутствие интернета
- Ошибки HTTP (404, 403, 500, etc.)

**Ошибки парсинга:**
- Некорректный JSON
- Отсутствующие поля
- Неверный формат версии

### **2. Лимиты GitHub API**

**Ограничения:**
- 60 запросов в час для неавторизованных запросов
- 5000 запросов в час для авторизованных

**Обработка:**
```kotlin
HttpURLConnection.HTTP_FORBIDDEN -> {
    UpdateResult(success = false, error = "Превышен лимит запросов к GitHub API")
}
```

### **3. User-Agent**

**Назначение:** Идентификация приложения для GitHub
```kotlin
private const val USER_AGENT = "SlavgorodBus/1.0.3"
```

## 📱 ПОЛЬЗОВАТЕЛЬСКИЙ ОПЫТ

### **1. Уведомления об обновлениях**

**Автоматические:**
- Проверка при запуске приложения
- Сохранение информации о доступных обновлениях
- Отображение в настройках

**Ручные:**
- Кнопка "Проверить" в настройках
- Мгновенная обратная связь
- Отображение ошибок

### **2. Загрузка обновлений**

**Процесс:**
```kotlin
fun downloadUpdate(version: AppVersion) {
    val intent = Intent(Intent.ACTION_VIEW, version.downloadUrl.toUri())
    context.startActivity(intent)
}
```

**Что происходит:**
- Открывается браузер
- Пользователь скачивает APK файл
- Устанавливает обновление вручную

## 🎯 ПРЕИМУЩЕСТВА СИСТЕМЫ

### **1. Надежность**
- ✅ Проверка интернет-соединения
- ✅ Таймауты для запросов
- ✅ Обработка всех типов ошибок
- ✅ Логирование для отладки

### **2. Производительность**
- ✅ Асинхронные операции
- ✅ Не блокирует UI
- ✅ Кэширование результатов
- ✅ Минимальное использование ресурсов

### **3. Безопасность**
- ✅ Проверка подлинности через GitHub
- ✅ Безопасная загрузка через HTTPS
- ✅ Валидация версий
- ✅ User-Agent для идентификации

### **4. Удобство**
- ✅ Автоматическая проверка
- ✅ Ручная проверка по требованию
- ✅ Настройки пользователя
- ✅ Понятные сообщения об ошибках

## 🔧 ТЕХНИЧЕСКИЕ ДЕТАЛИ

### **1. Зависимости**
- **Сеть:** `HttpURLConnection` (встроенный в Android)
- **JSON:** `org.json.JSONObject` (встроенный в Android)
- **Корутины:** `kotlinx.coroutines` для асинхронности
- **DataStore:** Для сохранения настроек

### **2. Разрешения**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### **3. Минимальные требования**
- Android API 21+ (Android 5.0)
- Интернет-соединение
- Доступ к GitHub API

## 🎉 ЗАКЛЮЧЕНИЕ

Система проверки обновлений представляет собой надежное и эффективное решение для автоматического обновления приложения через GitHub. Она обеспечивает:

- **Автоматическую проверку** при запуске приложения
- **Ручную проверку** по требованию пользователя
- **Безопасную загрузку** через официальный GitHub
- **Гибкие настройки** для пользователя
- **Обработку ошибок** и сетевых проблем

Система полностью интегрирована в приложение и готова к использованию! 🚀
