# How WARP Works Under the Hood

This document explains the technical inner workings, compilation pipeline, serialization format, and platform execution flow of the **Warp Abstraction & Rendering Pipeline**.

---

## Technical Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      KMP Declarative Code                       │
│    @Composable Content() -> Produces WarpNode AST Tree          │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                   ┌─────────────┴─────────────┐
                   │  kotlinx.serialization    │
                   │  JSON Serialization Engine │
                   └─────────────┬─────────────┘
                                 │
         ┌───────────────────────┴───────────────────────┐
         │                                               │
┌────────▼──────────────────────┐      ┌─────────────────▼─────────────┐
│    Android Glance Engine      │      │     iOS Swift Bridge SPM      │
│ 1. WarpGlanceWidgetReceiver   │      │ 1. WarpWidgetBridge SPM       │
│ 2. AST -> Glance Composables  │      │ 2. App Group NSUserDefaults   │
│ 3. System RemoteViews         │      │ 3. WarpSwiftUIRenderer.swift  │
└───────────────────────────────┘      └───────────────────────────────┘
```

---

## 1. The `WarpNode` AST (Abstract Syntax Tree)

When you write a WARP UI using `@Composable` primitives (`WarpColumn`, `WarpText`, `WarpButton`), WARP does not render directly to Android `View` or iOS `UIView` objects. Instead, the Compose compiler executes a lightweight AST builder that emits a hierarchical `WarpNode` tree.

### AST Node Types
- `WarpNode.Column`: Holds child nodes, vertical/horizontal alignments.
- `WarpNode.Row`: Holds child nodes, horizontal/vertical alignments.
- `WarpNode.Box`: Holds stacked child nodes, content alignment.
- `WarpNode.Text`: Holds text string, `WarpTextStyle`.
- `WarpNode.Button`: Holds text string, `WarpAction`, enabled state.
- `WarpNode.Image`: Holds `WarpAsset` (Resource / URL), scale mode.
- `WarpNode.ProgressIndicator`: Holds float progress, style mode.
- `WarpNode.Spacer`: Holds width/height dimensions.

Every node carries a `WarpModifier` chain that encodes padding, dimensions, background colors, corner radii, borders, and click actions.

---

## 2. Android Glance Rendering Pipeline

On Android, home screen widgets are driven by `AppWidgetProvider` and system `RemoteViews`. Android Glance is Jetpack's modern library for building `RemoteViews` with Compose syntax.

### Steps on Android:
1. **Receiver Invocation**: `WarpGlanceWidgetReceiver` handles `ACTION_APPWIDGET_UPDATE` system broadcasts.
2. **State Retrieval**: Reads `@Serializable` state object from `SharedPreferences` / DataStore.
3. **AST Construction**: Invokes `@Composable Content(state)` to generate the `WarpNode` tree.
4. **Glance Mapping**: `WarpGlanceRenderer` recursively maps `WarpNode` tree into Jetpack Glance composables:
   - `WarpNode.Column` → `androidx.glance.layout.Column`
   - `WarpNode.Row` → `androidx.glance.layout.Row`
   - `WarpNode.Text` → `androidx.glance.text.Text`
   - `WarpNode.Button` → `androidx.glance.Button`
5. **System Display**: Glance compiles the composables into Android `RemoteViews` for the system launcher.
6. **Action Dispatch**: Button taps trigger a Glance `ActionCallback` (`WarpGlanceActionCallback`), which deserializes the action and delegates execution to `WarpClickHandler`.

---

## 3. iOS SwiftUI / WidgetKit Pipeline

iOS widgets require native SwiftUI views compiled into a WidgetKit extension. Since Kotlin Multiplatform code cannot output SwiftUI structs directly at runtime, WARP uses a high-performance **Swift Bridge & JSON Renderer**.

### Steps on iOS:
1. **State Persistence**: When `updateWarpWidgetState` is called in KMP, state is serialized to JSON and stored in shared App Group `NSUserDefaults`.
2. **Timeline Reload**: KMP calls `WarpWidgetBridge.shared().reloadTimelinesOfKind(widgetId)` to notify iOS `WidgetCenter`.
3. **AST JSON Export**: During WidgetKit timeline evaluation, iOS calls Kotlin `renderWarpWidgetToNodeJson(widgetId, family)` to receive the serialized `WarpNode` AST tree as a JSON string.
4. **SwiftUI Rendering**: `WarpSwiftUIRenderer.swift` (from `warpWidgetKit` SPM package) parses the JSON string and renders native SwiftUI views:
   - `WarpNode.Column` → `VStack`
   - `WarpNode.Row` → `HStack`
   - `WarpNode.Box` → `ZStack`
   - `WarpNode.Text` → `Text`
   - `WarpNode.Button` → `Button(intent: WarpActionIntent(...))`
5. **App Intent Action Dispatch**: Tapping a button fires an iOS `AppIntent` (`WarpActionIntent.swift`). The intent passes the encoded action payload back to KMP's `WarpWidgetBridge`, executing `WarpClickHandler` and updating the state seamlessly.

---

## 4. Cold-Start & Background Execution Handling

Widgets must respond to click actions even when the main app process is closed or killed by the operating system.

- **Android Cold-Start**: `WarpGlanceActionCallback` uses `androidx.startup` and `WarpWidgetAndroidRegistry` to lazily instantiate the required `WarpClickHandler` on background thread workers (`WorkManager`).
- **iOS Cold-Start**: iOS `AppIntent` launches a background extension process. `WarpWidgetBridge` initializes the Kotlin framework, executes `WarpClickHandler.onClick(...)`, writes the new state to `NSUserDefaults`, and triggers a `WidgetCenter` reload.
