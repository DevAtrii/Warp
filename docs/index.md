# WARP — Widget Abstraction & Rendering Pipeline

**WARP** is a Kotlin Multiplatform (KMP) framework that enables you to build cross-platform home screen and lock screen widgets for **Android (Glance)** and **iOS (WidgetKit)** using a single, unified declarative Compose DSL codebase in Kotlin.

---

## Key Features

- ⚡ **Write Once in Kotlin**: Build widget UIs using familiar Compose syntax (`WarpColumn`, `WarpRow`, `WarpText`, `WarpButton`).
- 📱 **Pure Native Performance**: No WebViews or bridge overhead. Renders directly as native **Android Glance RemoteViews** and **iOS SwiftUI / WidgetKit** UIs.
- 🔄 **Automatic State Synchronization**: Persists widget states seamlessly to `SharedPreferences` (Android) and `NSUserDefaults` / App Group (iOS) with `@Serializable` data models.
- 🎯 **Type-Safe Actions & Clicks**: Handle button taps and interactive events using sealed class actions and type-safe click handlers.
- 🎨 **Adaptive Themes & Styling**: Built-in dark mode, dynamic color tokens (`WarpColor`), text styles, borders, corner radii, and chainable `WarpModifier` elements.
- 📐 **Multi-Size & Accessory Support**: Responsive widget layouts for `SYSTEM_SMALL`, `SYSTEM_MEDIUM`, `SYSTEM_LARGE`, and iOS Lock Screen accessory widgets (`ACCESSORY_CIRCULAR`, `ACCESSORY_RECTANGULAR`).

---

## Module Architecture

WARP is structured into modular KMP libraries:

| Module | Description | Platform Support |
| :--- | :--- | :--- |
| **`warp-runtime`** | Core AST (`WarpNode`), serializable models, state scope, `WarpModifier`, and action definitions. | Common, Android, iOS, JVM |
| **`warp-ui`** | High-level Compose UI engine and component renderers for platform target rendering. | Common, Android, iOS |
| **`warp-widget`** | High-level KMP widget orchestration, Glance AppWidget integration, and Swift SPM bridge bindings. | Common, Android, iOS |
| **`warpWidgetKit`** | Native Swift package (SPM) providing Swift UIRenderer, App Intents, and WidgetKit entrypoints for iOS host apps. | iOS / Swift |

---

## Quick Example

Here is how simple it is to build a counter widget with WARP:

```kotlin
@Serializable
data class CounterState(val count: Int = 0)

@Serializable
sealed class CounterAction {
    @Serializable data object Increment : CounterAction()
    @Serializable data object Decrement : CounterAction()
}

class CounterClickHandler : WarpClickHandler<CounterState, CounterAction> {
    override suspend fun onClick(
        context: PlatformContext,
        action: CounterAction,
        widgetId: WarpWidgetId,
    ) {
        updateWarpWidgetState<CounterState>(context, widgetId) { current ->
            when (action) {
                CounterAction.Increment -> current.copy(count = current.count + 1)
                CounterAction.Decrement -> current.copy(count = current.count - 1)
            }
        }
    }
}

object CounterWidget : WarpWidget<CounterState> {
    override val id = "counter_widget"
    override val initialState = CounterState()

    @Composable
    override fun Content(state: CounterState) {
        WarpTheme {
            WarpColumn(
                modifier = WarpModifier.fillMaxSize().padding(16),
                horizontalAlignment = WarpHorizontalAlignment.Center,
                verticalAlignment = WarpVerticalAlignment.Center
            ) {
                WarpText(
                    text = "Count: ${state.count}",
                    style = WarpTextStyle(fontSize = 20, fontWeight = WarpFontWeight.Bold)
                )
                WarpSpacer(height = 12)
                WarpRow {
                    WarpButton(
                        text = "-",
                        onClick = CounterAction.Decrement
                    )
                    WarpSpacer(width = 8)
                    WarpButton(
                        text = "+",
                        onClick = CounterAction.Increment
                    )
                }
            }
        }
    }
}
```

---

## Documentation Guide

Explore the detailed guides to get started with WARP:

1. [**Mental Model & Architecture**](1-mental-model.md): Deep dive into WARP's state flow, AST tree, and platform renderers.
2. [**Creating Your First Widget**](2-creating-your-first-widget.md): Step-by-step tutorial building an interactive KMP widget.
3. [**Composables Reference**](3-warp-composables.md): Complete API reference for all WARP UI components and layout nodes.
4. [**Styling & Theme**](4-warp-theme.md): Customizing themes, typography, colors, and modifiers.
5. [**Adaptive Widgets**](5-warp-adaptive.md): Building responsive layouts for small, medium, large, and accessory lock screen widgets.
6. [**How WARP Works**](6-how-warp-works.md): Technical deep dive into AST generation, Swift bridge IPC, Glance execution, and cold-start action handling.
