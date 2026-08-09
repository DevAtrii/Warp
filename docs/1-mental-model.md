# Mental Model & Architecture

Understanding WARP requires understanding its core paradigm: **State → AST Tree → Native Platform Renderers**.

WARP decouples your Kotlin widget code from native platform UI frameworks (Android Glance & iOS SwiftUI / WidgetKit) by producing a lightweight, serializable Abstract Syntax Tree (AST) called **`WarpNode`**.

---

## The Core Pipeline

```
┌────────────────────────────────────────────────────────┐
│                   1. KMP Widget Code                   │
│   Compose DSL -> Builds WarpNode AST Tree              │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                2. State & Action Engine                │
│   @Serializable State <---> WarpWidgetStateStore       │
└─────────────┬───────────────────────────┬──────────────┘
              │                           │
┌─────────────▼───────────────┐ ┌─────────▼──────────────┐
│     3a. Android Glance      │ │     3b. iOS SwiftUI    │
│  WarpNode -> Glance Composables│ │ WarpNode -> SwiftUIRenderer│
│     -> RemoteViews          │ │     -> WidgetKit       │
└─────────────────────────────┘ └────────────────────────┘
```

---

## The 4 Pillars of a WARP Widget

Every widget built with WARP consists of four distinct, type-safe components:

### 1. State Model (`WarpState`)
The state model represents all dynamic data rendered by the widget. It MUST be annotated with `@Serializable` from `kotlinx.serialization`.

```kotlin
@Serializable
data class WeatherState(
    val city: String = "San Francisco",
    val temperatureC: Int = 22,
    val condition: String = "Sunny",
)
```

### 2. Actions (`WarpAction`)
Actions represent user interactions (such as button taps, chip selections, or toggle switches). Actions are declared as `@Serializable` sealed classes or data objects.

```kotlin
@Serializable
sealed class WeatherAction {
    @Serializable data object Refresh : WeatherAction()
    @Serializable data class SelectCity(val city: String) : WeatherAction()
}
```

### 3. Click Handler (`WarpClickHandler`)
Click handlers receive user actions, execute background logic or API calls, and update the persistent state using `updateWarpWidgetState`:

```kotlin
class WeatherClickHandler : WarpClickHandler<WeatherState, WeatherAction> {
    override suspend fun onClick(
        context: PlatformContext,
        action: WeatherAction,
        widgetId: WarpWidgetId,
    ) {
        updateWarpWidgetState<WeatherState>(context, widgetId) { current ->
            when (action) {
                WeatherAction.Refresh -> fetchLatestWeather(current)
                is WeatherAction.SelectCity -> current.copy(city = action.city)
            }
        }
    }
}
```

### 4. Widget Specification (`WarpWidget`)
The `WarpWidget` object binds state, click handler, and UI together:

```kotlin
object WeatherWarpWidget : WarpWidget<WeatherState> {
    override val id: String = "weather_widget"
    override val initialState: WeatherState = WeatherState()
    override val clickHandler: WarpClickHandler<WeatherState, *>? = WeatherClickHandler()

    @Composable
    override fun Content(state: WeatherState) {
        WarpTheme {
            WarpColumn(modifier = WarpModifier.fillMaxSize().padding(12)) {
                WarpText(text = state.city, style = WarpTextStyle(fontSize = 18, fontWeight = WarpFontWeight.Bold))
                WarpText(text = "${state.temperatureC}°C - ${state.condition}")
                WarpSpacer(height = 8)
                WarpButton(text = "Refresh", onClick = WeatherAction.Refresh)
            }
        }
    }
}
```

---

## State Scoping: Shared vs. Instance

WARP supports two levels of state persistence depending on your widget requirements:

| State Scope | Scope Behavior | Android Storage | iOS Storage |
| :--- | :--- | :--- | :--- |
| `WarpWidgetStateScope.Shared` | All instances of the widget share the **same** global state. | `SharedPreferences` | App Group `NSUserDefaults` |
| `WarpWidgetStateScope.Instance` | Each pinned instance of the widget on the home screen maintains its **own independent** state. | Glance `GlanceId` DataStore | Instance Prefix in App Group `NSUserDefaults` |

### Selecting State Scope

By default, WARP widgets use `Shared` state scope. To specify instance-level scoping:

```kotlin
object WeatherWarpWidget : WarpWidget<WeatherState> {
    override val id = "weather_widget"
    override val stateScope = WarpWidgetStateScope.Instance
    // ...
}
```

---

## Platform Rendering Engines

### Android (Glance AppWidget)
1. `WarpGlanceWidgetReceiver` receives OS update broadcasts.
2. Reads state from `WarpWidgetStateStore`.
3. Invokes `@Composable Content(state)` to generate the `WarpNode` AST tree.
4. Translates `WarpNode` AST to Android Glance Composables (`Column`, `Row`, `Text`, `Button`), which Android converts into system `RemoteViews`.

### iOS (WidgetKit & SwiftUI)
1. `WarpWidgetBridge` triggers `WidgetCenter.shared.reloadTimelines(ofKind: widget.id)`.
2. Swift `Provider` reads state JSON from shared App Group `NSUserDefaults`.
3. Calls KMP `renderWarpWidgetToNodeJson()` to get the AST JSON string.
4. `WarpSwiftUIRenderer.swift` parses the JSON and renders native SwiftUI views (`VStack`, `HStack`, `Text`, `Button`, `ZStack`).
