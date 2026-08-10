---
icon: lucide/brain
---

# How WARP Works Under the Hood

This document dives deep into the technical architecture, compilation pipeline, abstract syntax tree (AST), serialization protocol, and native platform execution engine behind **WARP** (**W**idget **A**bstraction **R**endering **P**ipeline).

---

## Technical Overview

At its core, WARP separates **UI description** from **UI rendering**. 

Instead of rendering canvas pixels or binding directly to platform views during composition, WARP compiles declarative `@Composable` code into a lightweight, serializable **Abstract Syntax Tree (AST)** called `WarpNode`. This tree is encoded into JSON and handed off to native renderers on Android and iOS.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      1. Kotlin Multiplatform Code                       │
│       @Composable Content()  ──>  Builds In-Memory WarpNode AST        │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                        ┌────────────┴────────────┐
                        │ kotlinx.serialization   │
                        │ JSON Serialization      │
                        └────────────┬────────────┘
                                     │
         ┌───────────────────────────┴───────────────────────────┐
         │                                                       │
┌────────▼───────────────────────────────┐     ┌─────────────────▼─────────────────────┐
│       Android Glance Host              │     │         iOS WidgetKit Host            │
│ 1. WarpGlanceWidgetReceiver            │     │ 1. WarpWidgetBridge (spm4Kmp)         │
│ 2. AST ──> Glance Composables          │     │ 2. App Group UserDefaults             │
│ 3. System RemoteViews compilation      │     │ 3. WarpSwiftUIRenderer.swift (SwiftUI)│
└────────────────────────────────────────┘     └───────────────────────────────────────┘
```

---

## 1. Modular Architecture Breakdown

WARP is structured into decoupled, single-responsibility modules:

```
┌────────────────────────────────────────────────────────────────┐
│                         warp-widget                            │
│  · WarpWidget<S> (State, ID, Default State, Content)           │
│  · WarpWidgetSession (Environment, PlatformContext, ID)        │
│  · WarpWidgetHost (compose / JSON / dispatch / prepare)        │
│  · WarpTheme & WarpAdaptive size bucketing                     │
└───────────────┬────────────────────────────────┬───────────────┘
                ▼                                ▼
┌───────────────────────────────┐  ┌─────────────────────────────┐
│          warp-runtime         │  │           warp-ui           │
│  · Compose Compiler DSL       │  │  · Glance AST Renderer      │
│  · WarpNode AST Data Classes  │  │  · SwiftUI JSON Generator   │
│  · Recomposer Frame Clock     │  │  · WarpClickHandler          │
│  · WarpModifier Chain         │  │  · WarpClicksRegistry       │
└───────────────────────────────┘  └──────────────┬──────────────┘
                                                  ▼
                                       warpWidgetKit (SPM)
                                       SwiftUI & AppIntent Bridge
```

- **`warp-runtime`**: Provides the `@Composable` DSL (`WarpColumn`, `WarpText`, `WarpButton`), the `WarpNode` AST tree definition, the `WarpModifier` chain, and the in-memory `Recomposer` engine.
- **`warp-ui`**: Houses platform renderers that map `WarpNode` trees to **Jetpack Glance** composables on Android, and generate JSON payloads for **SwiftUI** on iOS.
- **`warp-widget`**: Defines `WarpWidget<S>`, `WarpWidgetSession`, persistent state stores (`WarpWidgetStateStore`), theme resolution (`WarpTheme`), and adaptive size bucketing (`WarpAdaptive`).
- **`warpWidgetKit`**: A standalone Swift Package Manager (SPM) library providing SwiftUI renderers (`WarpSwiftUIRenderer.swift`) and iOS 17+ interactive `AppIntent` handlers (`WarpClickAppIntent`).

---

## 2. Phase 1: Composition to AST (`warp-runtime`)

When you write a WARP UI using `@Composable` primitives, WARP uses **Compose Runtime** (without Compose UI, Foundation, or Material dependencies):

```kotlin
val json = composeWarpToJson(CounterState(count = 42)) { state ->
    WarpColumn(modifier = WarpModifier.padding(16.dp)) {
        WarpText("Counter")
        WarpRow {
            WarpButton(text = "−", onClick = CounterActions.Decrement.asClickAction())
            WarpText("${state.count}")
            WarpButton(text = "+", onClick = CounterActions.Increment.asClickAction())
        }
    }
}
```

### How `composeWarp` Executes:

1. **Root Creation**: Initializes a temporary `RootHolder` bucket.
2. **Recomposer Frame Clock**: Uses a headless `Recomposer` and `BroadcastFrameClock` to execute composable functions. If local `mutableStateOf` changes occur during execution, WARP drives recomposition passes until the tree settles.
3. **CompositionLocal Parent Tracking**: An internal `CompositionLocal` tracks current parent containers (`WarpColumnHolder`, `WarpRowHolder`, `WarpBoxHolder`). As composables run, they register child holders into their parent's children list.
4. **AST Conversion**: Once composition completes, `root.toWarpNode()` transforms temporary internal holders into an immutable, serializable `WarpNode` tree.

---

## 3. Phase 2: Serialization (`WarpNode` JSON)

The resulting `WarpNode` tree is a hierarchy of `@Serializable` Kotlin data classes. `kotlinx.serialization` serializes the tree using a polymorphic class discriminator (`"type"`):

```json
{
  "type": "column",
  "modifier": {
    "elements": [
      { "type": "padding", "start": 16, "end": 16, "top": 16, "bottom": 16 }
    ]
  },
  "children": [
    { "type": "text", "text": "Counter" },
    {
      "type": "row",
      "children": [
        {
          "type": "button",
          "text": "−",
          "onClick": { "type": "click", "actionId": "decrement", "parameters": {} }
        },
        { "type": "text", "text": "42" },
        {
          "type": "button",
          "text": "+",
          "onClick": { "type": "click", "actionId": "increment", "parameters": {} }
        }
      ]
    }
  ]
}
```

---

## 4. Phase 3: Native Platform Rendering (`warp-ui`)

Because Android and iOS enforce strict system boundaries for home screen widgets, WARP delegates visual layout to true native framework elements on each OS.

### Android Rendering (Jetpack Glance)
On Android, home screen widgets are hosted inside system `RemoteViews`:

1. **Receiver Broadcast**: `WarpGlanceWidgetReceiver` receives `ACTION_APPWIDGET_UPDATE` or `UI_MODE_CHANGED` broadcasts.
2. **State & Environment**: Reads `CounterState` from `SharedPreferences` via `WarpWidgetStateStore` and resolves `WidgetEnvironment`.
3. **Glance Mapping**: `WarpRender` recursively converts `WarpNode` objects into Jetpack Glance composables:
   - `WarpNode.Column` $\rightarrow$ `androidx.glance.layout.Column`
   - `WarpNode.Row` $\rightarrow$ `androidx.glance.layout.Row`
   - `WarpNode.Box` $\rightarrow$ `androidx.glance.layout.Box`
   - `WarpNode.Text` $\rightarrow$ `androidx.glance.text.Text`
   - `WarpNode.Button` $\rightarrow$ `androidx.glance.Button`
4. **RemoteViews Compilation**: Jetpack Glance compiles the layout into Android system `RemoteViews`.

### iOS Rendering (WidgetKit & SwiftUI)
iOS extensions run in isolated background processes and expect native SwiftUI views:

1. **State Store Sync**: Calling `updateWarpWidgetState` in KMP updates App Group `NSUserDefaults` (`"group.com.company.app"`).
2. **Timeline Trigger**: KMP calls `WarpWidgetBridge.shared.reloadTimelinesOfKind("CounterWidget")` to notify iOS `WidgetCenter`.
3. **JSON Evaluation**: WidgetKit invokes `CounterWidgetEntryView`. SwiftUI queries Kotlin via `composeWidgetJson(...)` to generate the latest `WarpNode` AST JSON string.
4. **SwiftUI Parsing**: `WarpSwiftUIRenderer.swift` (inside `warpWidgetKit`) parses the AST JSON at render time and builds a native SwiftUI view tree:
   - `column` $\rightarrow$ `VStack`
   - `row` $\rightarrow$ `HStack`
   - `box` $\rightarrow$ `ZStack`
   - `text` $\rightarrow$ `Text`
   - `button` $\rightarrow$ `Button(intent: CounterWidgetClickIntent(...))`

---

## 5. Phase 4: Action Dispatch & Cold Starts

Widgets cannot execute live Kotlin lambdas on button clicks because taps must survive process restarts and OS sleep cycles.

```
Common UI                                Host (Android / iOS)
─────────────────────────────────────────────────────────────
WarpButton(                              Native renderer reads WarpButton.onClick
  onClick = CounterActions               ──> Wires native button tap with actionId
    .Increment.asClickAction()           User Taps Button
)                                                  │
     │                                             ▼
     ▼                                    Glance ActionCallback / AppIntent
JSON: { onClick: {                                 │
  type: "click",                                   ▼
  actionId: "increment"                   WarpClicksRegistry.dispatch(
}}                                          actionId, parameters
                                          )
                                                   │
                                                   ▼
                                          WarpClickHandler.onClick(action)
                                                   │
                                                   ▼
                                          updateWarpWidgetState { ... }
                                                   │
                                                   ▼
                                          Reload Widget Timelines
```

### Cold-Start Handling:
- **Android**: Tapping a button fires a Glance `ActionCallback` (`WarpRegistryActionCallback`). If the main app process is dead, Android initializes background workers, registers widget receivers, dispatches `actionId` + `parameters` through `WarpClicksRegistry`, updates state in `SharedPreferences`, and calls `GlanceAppWidget.update()`.
- **iOS 17+**: Tapping a button fires a Swift `AppIntent` (`WarpClickAppIntent`). WidgetKit launches the extension target, initializes KMP via `WarpWidgetHost.shared.prepare(...)`, dispatches the click to `WarpClickHandler`, mutates `NSUserDefaults`, and calls `WidgetCenter.shared.reloadTimelines(...)`.

---

## 6. Beyond Widgets: The "Compose-Native" Vision 💡

WARP proves an exciting architectural concept: **Writing declarative UI with Compose syntax in Kotlin, compiling it to a clean serializable AST, and rendering true 100% native platform views on both platforms.**

### A Food-For-Thought Question for Curious Minds:

> *If we can compile Compose functions into a serializable AST that renders native Glance RemoteViews on Android and native SwiftUI views on iOS for home screen widgets...*
> 
> **Could the exact same architecture be expanded to build full mobile apps via `Compose-Native`?**

Imagine a future cross-platform framework where:
- You write 100% shared Compose code in Kotlin Multiplatform.
- Instead of drawing pixels on a Skia/OpenGL canvas (like Compose Multiplatform), the engine translates AST nodes into **native UIKit / SwiftUI views on iOS** and **native Jetpack Compose / Android Views on Android**.
- App buttons, navigation stacks, text fields, and scroll views use **native OS animation engines, accessibility APIs, and platform themes** automatically!

Could this AST pipeline be the key to achieving true native platform look-and-feel from a single Compose codebase? 🚀
