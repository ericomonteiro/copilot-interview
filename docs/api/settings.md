# Settings API

Reference documentation for the Settings system.

## SettingsRepository

The `SettingsRepository` provides access to persistent user settings.

### Interface

```kotlin
class SettingsRepository(private val database: Database) {
    suspend fun getSetting(key: String): String?
    suspend fun setSetting(key: String, value: String)
    fun getSettingFlow(key: String): Flow<String?>
    suspend fun deleteSetting(key: String)
    suspend fun getAllSettings(): Map<String, String>
}
```

## Methods

### getSetting

Retrieve a setting value by key.

```kotlin
suspend fun getSetting(key: String): String?
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `key` | `String` | Setting key |

**Returns:** `String?` - The setting value or null if not found

**Example:**

```kotlin
val apiKey = settingsRepository.getSetting(SettingsKeys.API_KEY)
```

---

### setSetting

Store a setting value.

```kotlin
suspend fun setSetting(key: String, value: String)
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `key` | `String` | Setting key |
| `value` | `String` | Setting value |

**Example:**

```kotlin
settingsRepository.setSetting(SettingsKeys.API_KEY, "your-api-key")
```

---

### getSettingFlow

Get a reactive Flow of a setting value.

```kotlin
fun getSettingFlow(key: String): Flow<String?>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `key` | `String` | Setting key |

**Returns:** `Flow<String?>` - Reactive stream of the setting value

**Example:**

```kotlin
settingsRepository.getSettingFlow(SettingsKeys.API_KEY)
    .collect { apiKey ->
        // React to changes
    }
```

---

### deleteSetting

Remove a setting.

```kotlin
suspend fun deleteSetting(key: String)
```

---

### getAllSettings

Get all settings as a map.

```kotlin
suspend fun getAllSettings(): Map<String, String>
```

---

## Settings Keys

All setting keys are defined in `SettingsKeys`:

```kotlin
object SettingsKeys {
    const val API_KEY = "api_key"
    const val SELECTED_MODEL = "selected_model"
    const val DEFAULT_LANGUAGE = "default_language"
    const val HIDE_FROM_CAPTURE = "hide_from_capture"
    const val WINDOW_WIDTH = "window_width"
    const val WINDOW_HEIGHT = "window_height"
    const val WINDOW_X = "window_x"
    const val WINDOW_Y = "window_y"
}
```

### Key Descriptions

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `api_key` | `String` | `null` | Gemini API key |
| `selected_model` | `String` | `gemini-2.5-flash` | AI model to use |
| `default_language` | `String` | `Kotlin` | Default programming language |
| `hide_from_capture` | `Boolean` | `true` | Stealth mode enabled |
| `window_width` | `Float` | `800` | Window width in dp |
| `window_height` | `Float` | `600` | Window height in dp |
| `window_x` | `Float` | `100` | Window X position |
| `window_y` | `Float` | `100` | Window Y position |

---

## Database Schema

Settings are stored in SQLDelight:

```sql
CREATE TABLE settings (
    key TEXT PRIMARY KEY NOT NULL,
    value TEXT NOT NULL,
    updated_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

-- Queries
getSetting:
SELECT value FROM settings WHERE key = ?;

setSetting:
INSERT OR REPLACE INTO settings (key, value, updated_at) 
VALUES (?, ?, strftime('%s', 'now'));

deleteSetting:
DELETE FROM settings WHERE key = ?;

getAllSettings:
SELECT key, value FROM settings;
```

---

## Usage Examples

### In ViewModel

```kotlin
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _apiKey = MutableStateFlow<String?>(null)
    val apiKey: StateFlow<String?> = _apiKey.asStateFlow()
    
    init {
        viewModelScope.launch {
            _apiKey.value = settingsRepository.getSetting(SettingsKeys.API_KEY)
        }
    }
    
    fun updateApiKey(key: String) {
        viewModelScope.launch {
            settingsRepository.setSetting(SettingsKeys.API_KEY, key)
            _apiKey.value = key
        }
    }
}
```

### In Composable

```kotlin
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val apiKey by viewModel.apiKey.collectAsState()
    
    TextField(
        value = apiKey ?: "",
        onValueChange = { viewModel.updateApiKey(it) },
        label = { Text("API Key") }
    )
}
```

### Window State Persistence

```kotlin
// Save window state
LaunchedEffect(windowState.size, windowState.position) {
    delay(500) // Debounce
    settingsRepository.setSetting(
        SettingsKeys.WINDOW_WIDTH, 
        windowState.size.width.value.toString()
    )
    settingsRepository.setSetting(
        SettingsKeys.WINDOW_HEIGHT, 
        windowState.size.height.value.toString()
    )
}

// Load window state
val savedState = runBlocking {
    val width = settingsRepository.getSetting(SettingsKeys.WINDOW_WIDTH)
        ?.toFloatOrNull() ?: 800f
    val height = settingsRepository.getSetting(SettingsKeys.WINDOW_HEIGHT)
        ?.toFloatOrNull() ?: 600f
    WindowSavedState(width, height)
}
```

---

## Best Practices

### 1. Use Constants

Always use `SettingsKeys` constants instead of string literals:

```kotlin
// ✅ Good
settingsRepository.getSetting(SettingsKeys.API_KEY)

// ❌ Bad
settingsRepository.getSetting("api_key")
```

### 2. Handle Nulls

Settings may not exist, always handle null:

```kotlin
val model = settingsRepository.getSetting(SettingsKeys.SELECTED_MODEL)
    ?: "gemini-2.5-flash"
```

### 3. Type Conversion

Settings are stored as strings, convert as needed:

```kotlin
val hideFromCapture = settingsRepository
    .getSetting(SettingsKeys.HIDE_FROM_CAPTURE)
    ?.toBoolean() ?: true

val windowWidth = settingsRepository
    .getSetting(SettingsKeys.WINDOW_WIDTH)
    ?.toFloatOrNull() ?: 800f
```

### 4. Debounce Frequent Updates

For settings that change frequently (like window position):

```kotlin
LaunchedEffect(value) {
    delay(500) // Wait for changes to settle
    settingsRepository.setSetting(key, value)
}
```
