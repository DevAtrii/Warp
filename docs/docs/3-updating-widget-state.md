---
icon: lucide/refresh-cw
---

# Updating Widget State & Timelines

State in **WARP** is typed, serializable, and persisted across process restarts. This guide explains how to read and update widget state, discover active widget placements on the home screen, manage state scoping, handle periodic system timeline updates using `onUpdate`, and configure host update intervals on Android and iOS.

---

## 1. Overview of State Management

WARP widget state is defined as a `@Serializable` Kotlin data class. State payload blobs are serialized as JSON and saved under preference keys derived from your `WarpWidget.id`.

```
┌────────────────────────────────────────────────────────┐
│                   State Mutators                       │
├──────────────────┬──────────────────┬──────────────────┤
│ User Taps        │ Periodic System  │ Main Application │
│ (Action Handler) │ Updates (onUpdate│ (ViewModels /    │
│                  │ Timeline)        │ Background Tasks)│
└────────┬─────────┴────────┬─────────┴────────┬─────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌────────────────────────────────────────────────────────┐
│               updateWarpWidgetState()                  │
│       (JSON Encoding + Platform Storage + Reload)      │
└───────────────────────────┬────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌───────────────────────┐       ┌───────────────────────┐
│ Android SharedPreferences     │ iOS App Group         │
│ & Glance Recompose    │       │ UserDefaults & Reload │
└───────────────────────┘       └───────────────────────┘
```

---

## 2. Interactive State Updates (`updateWarpWidgetState`)

When a user taps a button or interactive element inside your widget, your custom `WarpActionHandler` receives the event inside `onAction()`. 

Use `updateWarpWidgetState(session, widget)` to mutate the current state using a `(S) -> S` transform:

```kotlin title="CounterWarpActionHandler.kt"
class CounterWarpActionHandler(
    private val session: WarpWidgetSession,
) : WarpActionHandler<CounterActions>(CounterActions.serializer()) {

    override suspend fun onAction(action: CounterActions) {
        updateWarpWidgetState(session, CounterWarpWidget) { state ->
            when (action) {
                CounterActions.Increment -> state.copy(count = state.count + 1)
                CounterActions.Decrement -> state.copy(count = state.count - 1)
                CounterActions.Reset -> state.copy(count = 0)
            }
        }
    }
}
```

### Action Handler Overloads

WARP provides two primary overloads for `updateWarpWidgetState`:

```kotlin title="updateWarpWidgetState Signatures"
// 1. Session-scoped update 
suspend fun <S : Any> updateWarpWidgetState(
    session: WarpWidgetSession,
    widget: WarpWidget<S>,
    transform: (S) -> S,
)

// 2. Explicit Context + WidgetId update 
suspend fun <S : Any> updateWarpWidgetState(
    context: PlatformContext,
    widget: WarpWidget<S>,
    id: WarpWidgetId,
    transform: (S) -> S,
)
```

!!! tip "Click Scope Resolution"
    When updating via `updateWarpWidgetState(session, widget)`, WARP automatically checks `WarpWidgetClickScope.current()` to target the exact widget placement that dispatched the user click, even during cold starts.

---

## 3. Shared vs Instance State Scoping

WARP supports two state scoping models controlled by `WarpWidget.stateScope`:

```kotlin title="State Scope Declaration"
object CounterWarpWidget : WarpWidget<CounterState>(CounterState.serializer()) {
    override val id: String = "CounterWidget"
    override val iosGroupId: String = "group.com.example.app"
    
    // Pick Shared or Instance scoping
    override val stateScope: WarpWidgetStateScope = WarpWidgetStateScope.Shared
    ...
}
```

| Scope Model | Behavior | Storage Details |
| :--- | :--- | :--- |
| **`WarpWidgetStateScope.Shared`** *(Default)* | Every widget instance added to the home screen mirrors the exact same state payload. Updating state in one widget automatically updates all instances. | **Android**: Prefs are mirrored to all active `GlanceId` instances.<br>**iOS**: App Group `UserDefaults` key = `"$kind.$key"`. |
| **`WarpWidgetStateScope.Instance`** | Each widget placement maintains its own isolated, independent state payload. | **Android**: Each placed Glance widget ID has its own prefs blob.<br>**iOS**: State is scoped per **widget family** (`systemSmall`, `systemMedium`, etc.). |

---

## 4. Discovering Widget Placements (`listWarpWidgetIds`)

To query active home screen widget placements on both Android and iOS, use `listWarpWidgetIds`:

```kotlin title="listWarpWidgetIds Signature"
suspend fun listWarpWidgetIds(
    context: PlatformContext,
    widget: WarpWidgetHostApi,
): List<WarpWidgetId>
```

### Reading State from Main App

Use `listWarpWidgetIds` together with `readWarpWidgetState` to inspect the saved state of active widgets from your main app UI or ViewModels:

```kotlin title="Reading Active Widget State"
import com.atriidev.warp_widget.api.PlatformContext
import com.atriidev.warp_widget.listWarpWidgetIds
import com.atriidev.warp_widget.readWarpWidgetState

/** Reads state for the first active widget instance (or falls back to defaultState) */
suspend fun readCounterWidgetState(context: PlatformContext): CounterState {
    val ids = listWarpWidgetIds(context, CounterWarpWidget)
    if (ids.isEmpty()) return CounterWarpWidget.defaultState()
    return readWarpWidgetState(context, CounterWarpWidget, ids.first())
}
```

### Updating All Active Placements

To push state updates from your app to all active widget instances on the home screen:

```kotlin title="Updating All Active Instances"
import com.atriidev.warp_widget.api.PlatformContext
import com.atriidev.warp_widget.listWarpWidgetIds
import com.atriidev.warp_widget.updateWarpWidgetState

/** Applies [transform] to every active home screen widget placement */
suspend fun updateAllCounterWidgetInstances(
    context: PlatformContext,
    transform: (CounterState) -> CounterState,
) {
    listWarpWidgetIds(context, CounterWarpWidget).forEach { id ->
        updateWarpWidgetState(context, CounterWarpWidget, id, transform)
    }
}
```

---

## 5. Periodic System Timeline Refresh (`onUpdate`)

System widget engines periodically wake widgets to refresh data. WARP exposes this lifecycle hook via `WarpWidget.onUpdate`:

```kotlin title="Overriding onUpdate in WarpWidget"
object CounterWarpWidget : WarpWidget<CounterState>(CounterState.serializer()) {
    override val id: String = "CounterWidget"
    override val iosGroupId: String = "group.com.example.app"

    override suspend fun defaultState(): CounterState = CounterState()

    @Composable
    override fun Content(session: WarpWidgetSession, state: CounterState) {
        // Compose UI
    }

    /** Called when the host OS triggers a periodic timeline update */
    override fun onUpdate(
        previous: Duration,
        current: Duration,
        session: WarpWidgetSession,
    ) {
        WarpLogger.d("CounterWarpWidget", "Periodic timeline refresh: previous=$previous, current=$current")
    }
}
```

---

## 6. Configuring System Host Update Intervals

You can control how frequently each operating system triggers timeline updates for your widget by modifying host configuration parameters on Android and iOS:

=== "Android Host Configuration"

    On Android, update frequency is declared in your XML AppWidgetProvider metadata via `android:updatePeriodMillis`.
    
    ```xml title="shared/src/androidMain/res/xml/my_app_widget_info.xml" hl_lines="8"
    <appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
        android:initialLayout="@layout/glance_default_loading_layout"
        android:minWidth="180dp"
        android:minHeight="90dp"
        android:minResizeWidth="90dp"
        android:minResizeHeight="90dp"
        android:resizeMode="horizontal|vertical"
        android:updatePeriodMillis="1800000" /> <!-- 30 minutes in milliseconds -->
    ```
    
    !!! note "Android Update Limits"
        Android enforces a minimum update interval of 15 minutes (`900000ms`). Setting `updatePeriodMillis` to a smaller value will be clamped by the OS.

=== "iOS Host Configuration"

    On iOS, update timing is defined in Swift inside your `TimelineProvider` (`getTimeline` method) by calculating the next update date and providing a `TimelineReloadPolicy` (`.after(...)`, `.atEnd`, or `.never`).
    
    ```swift title="CounterWidget.swift" hl_lines="1-2 15-28 35-38"
    private let UPDATE_SAFETY_MARGIN_MILLIS: Int64 = 60 * 1000 // 1 minute safety margin
    private let UPDATE_HOUR: Int = 1 // Update interval in hours

    struct CounterWidgetProvider: TimelineProvider {
        func placeholder(in context: Context) -> CounterWidgetEntry { entry(from: context) }
        func getSnapshot(in context: Context, completion: @escaping (CounterWidgetEntry) -> Void) { completion(entry(from: context)) }

        func getTimeline(
            in context: Context,
            completion: @escaping (Timeline<CounterWidgetEntry>) -> Void
        ) {
            let now = Date()
            let calendar = Calendar.current

            // Calculate next update timestamp based on UPDATE_HOUR
            let currentHour = calendar.component(.hour, from: now)
            let nextUpdateHour = ((currentHour / UPDATE_HOUR) + 1) * UPDATE_HOUR

            let nextHour: Date
            if nextUpdateHour >= 24 {
                nextHour = calendar.date(byAdding: .day, value: 1, to: calendar.startOfDay(for: now))!
            } else {
                nextHour = calendar.date(bySettingHour: nextUpdateHour, minute: 0, second: 0, of: now)!
            }

            let entry = CounterWidgetEntry(
                date: now,
                displayWidth: context.displaySize.width,
                displayHeight: context.displaySize.height
            )

            // Pass policy telling WidgetKit to reload after nextHour
            completion(Timeline(entries: [entry], policy: .after(nextHour)))
        }
    }
    ```

---


## 7. State & Timeline API Cheat Sheet

| API Function | Role & Usage |
| :--- | :--- |
| **`updateWarpWidgetState(session, widget) { S -> S }`** | Update state payload from inside an `ActionHandler` using active `WarpWidgetSession`. |
| **`updateWarpWidgetState(context, widget, id) { S -> S }`** | Programmatically update state for a specific `WarpWidgetId` from host app. |
| **`listWarpWidgetIds(context, widget)`** | Retrieve list of active `WarpWidgetId` placements on the home screen. |
| **`readWarpWidgetState(context, widget, id)`** | Read and decode saved state `S` for a given instance ID. |
| **`reloadWarpWidget(context, widget, id)`** | Request OS re-render of widget UI without modifying preference data. |
| **`onUpdate(previous, current, session)`** | Lifecycle callback executed during periodic system timeline refreshes. |
