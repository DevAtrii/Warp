---
name: warp-widget
description: Create cross-platform home screen widgets for Kotlin Multiplatform (Android Glance & iOS WidgetKit/SwiftUI) using the WARP framework. Use when building or updating WARP widgets, defining widget states, action handlers, layout composables, themes, Android Glance receivers, or iOS WidgetKit bridges.
---

# Creating Home Screen Widgets with WARP in KMP

This skill provides step-by-step instructions, design patterns, composables reference, and platform integrations for creating cross-platform home screen widgets using **WARP** (**W**idget **A**bstraction **R**endering **P**ipeline) in Kotlin Multiplatform (KMP).

> 📚 **Official Documentation**: Complete live documentation and page links are available at [`https://warp.atherio.dev`](https://warp.atherio.dev) (Sitemap index: [`https://warp.atherio.dev/sitemap.xml`](https://warp.atherio.dev/sitemap.xml)).

---

## 1. Core Architecture & Mental Model

WARP compiles a Compose-style `@Composable` DSL in `commonMain` into a serializable AST (`WarpNode`), which is rendered natively on **Android (Jetpack Glance)** and **iOS (WidgetKit & SwiftUI)**.

### Architectural Rules for AI Generators:
1. **Never use standard Compose UI or Foundation composables** (`Column`, `Row`, `Text`, `Button`, `Modifier`, `dp`, `sp` from `androidx.compose.ui`). Always use WARP primitives (`WarpColumn`, `WarpRow`, `WarpText`, `WarpButton`, `WarpModifier`, `dp`, `sp` from `com.atriidev.warp_runtime.*`).
2. **Keep Widgets Lightweight**: Widgets display pre-fetched state. Do not execute network calls or heavy DI inside widget render loops.
3. **Use Serialized Actions**: Taps must map to serializable action classes (`CounterActions.Increment.asClickAction()`) handled by a `WarpActionHandler`.

---

## 2. Shared Widget Template (`commonMain`)

Every WARP widget requires **4 components** defined in `commonMain`:

### 2.1 State & Action Definitions
```kotlin
package com.example.app.widget

import kotlinx.serialization.Serializable

/** 1. Widget State Payload */
@Serializable
data class MyWidgetState(
    val title: String = "Hello WARP",
    val count: Int = 0,
)

/** 2. Type-Safe User Action Hierarchy */
@Serializable
sealed class MyWidgetActions {
    @Serializable data object Increment : MyWidgetActions()
    @Serializable data object Decrement : MyWidgetActions()
    @Serializable data object Reset : MyWidgetActions()
}
```

### 2.2 Action Handler
```kotlin
import com.atriidev.warp_ui.WarpActionHandler
import com.atriidev.warp_widget.WarpWidgetSession
import com.atriidev.warp_widget.updateWarpWidgetState

class MyWidgetActionHandler(
    private val session: WarpWidgetSession,
) : WarpActionHandler<MyWidgetActions>(MyWidgetActions.serializer()) {

    override suspend fun onAction(action: MyWidgetActions) {
        updateWarpWidgetState(session, MyWarpWidget) { state ->
            when (action) {
                MyWidgetActions.Increment -> state.copy(count = state.count + 1)
                MyWidgetActions.Decrement -> state.copy(count = state.count - 1)
                MyWidgetActions.Reset -> state.copy(count = 0)
            }
        }
    }
}
```

### 2.3 Asset Identifiers
```kotlin
import com.atriidev.warp_runtime.nodes.assets.WarpAssetId

object MyWidgetAssets {
    val Plus = WarpAssetId("plus") // Maps to SF Symbol "plus" on iOS & R.drawable on Android
    val Minus = WarpAssetId("minus")
}
```

### 2.4 Complete `WarpWidget` Object
```kotlin
import androidx.compose.runtime.Composable
import com.atriidev.warp_runtime.compose.*
import com.atriidev.warp_runtime.nodes.actions.asClickAction
import com.atriidev.warp_runtime.nodes.modifiers.WarpColor
import com.atriidev.warp_runtime.nodes.modifiers.WarpModifier
import com.atriidev.warp_runtime.nodes.style.*
import com.atriidev.warp_runtime.unit.dp
import com.atriidev.warp_runtime.unit.sp
import com.atriidev.warp_ui.WarpActionHandler
import com.atriidev.warp_widget.WarpWidget
import com.atriidev.warp_widget.WarpWidgetSession
import com.atriidev.warp_widget.WarpWidgetStateScope
import com.atriidev.warp_widget.api.WidgetEnvironment
import com.atriidev.warp_widget.ui.WarpTheme

object MyWarpWidget : WarpWidget<MyWidgetState>(MyWidgetState.serializer()) {
    override val id: String = "MyWidget" // Must match iOS Widget target kind
    override val iosGroupId: String = "group.com.example.app" // Xcode App Group ID
    override val stateScope: WarpWidgetStateScope = WarpWidgetStateScope.Shared // Shared or Instance

    override suspend fun defaultState(): MyWidgetState = MyWidgetState()

    @Composable
    override fun Content(env: WidgetEnvironment, state: MyWidgetState) {
        WarpTheme(environment = env) {
            WarpBox(
                modifier = WarpModifier
                    .fillMaxSize()
                    .background(WarpTheme.colors.widgetBackground)
                    .padding(16.dp),
                contentAlignment = WarpContentAlignment.Center,
            ) {
                WarpColumn(
                    horizontalAlignment = WarpHorizontalAlignment.CenterHorizontally,
                ) {
                    WarpText(
                        text = "${state.title}: ${state.count}",
                        style = WarpTextStyle(
                            fontSize = 20.sp,
                            fontWeight = WarpFontWeight.Bold,
                            color = WarpTheme.colors.onSurface,
                        ),
                    )
                    WarpSpacer(modifier = WarpModifier.height(12.dp))
                    WarpRow {
                        WarpButton(
                            text = "−",
                            onClick = MyWidgetActions.Decrement.asClickAction(),
                            modifier = WarpModifier.size(40.dp),
                        )
                        WarpSpacer(modifier = WarpModifier.width(12.dp))
                        WarpButton(
                            text = "+",
                            onClick = MyWidgetActions.Increment.asClickAction(),
                            modifier = WarpModifier.size(40.dp),
                        )
                    }
                }
            }
        }
    }

    override fun clickHandlers(session: WarpWidgetSession): List<WarpActionHandler<*>> =
        listOf(MyWidgetActionHandler(session))
}
```

---

## 3. WARP Composables & Modifiers Quick Reference

### Layout Composables
- **`WarpColumn`**: Vertical column layout (`verticalAlignment`, `horizontalAlignment`).
- **`WarpRow`**: Horizontal row layout (`horizontalAlignment`, `verticalAlignment`).
- **`WarpBox`**: Stacked layer layout (`contentAlignment`).
- **`WarpSpacer`**: Empty spacing box (`width`, `height`, `weight`).

### Content Composables
- **`WarpText(text, modifier, style, maxLines)`**
- **`WarpButton(text/content, onClick, modifier, style, colors, enabled)`**
- **`WarpImage(asset, contentDescription, modifier, contentScale, tint)`**
- **`WarpLink(deeplink, modifier, androidIntentFlags, content)`**
- **`WarpProgressIndicator(modifier, style, progress, color, backgroundColor)`**
- **`WarpDivider(modifier, thickness, color)`**

### Modifiers (`WarpModifier`)
- **Dimensions**: `.fillMaxSize()`, `.fillMaxWidth()`, `.fillMaxHeight()`, `.size(dp)`, `.width(dp)`, `.height(dp)`, `.weight(flex)`
- **Spacing**: `.padding(all)`, `.padding(horizontal, vertical)`, `.padding(start, top, end, bottom)`
- **Appearance**: `.background(WarpColor)`, `.cornerRadius(dp)`, `.border(width, WarpColor)`, `.alpha(float)`, `.visibility(...)`
- **Interactions**: `.clickable(action.asClickAction())`

---

## 4. Theming & Adaptive Size Scaling

### Light / Dark Theme (`WarpTheme`)
Access dynamic light/dark colors mapped to platform defaults (Material 3 on Android, iOS System Blue on iOS):
```kotlin
val colors = WarpTheme.colors
WarpText("Title", style = WarpTextStyle(color = colors.onSurface))
```

### Adaptive Layout Buckets (`WarpAdaptiveSize`)
Scale layouts dynamically across **Small** (2x2), **Medium** (4x2), and **Large** (4x4) sizes:

```kotlin
// 1. Swap full layouts:
WarpAdaptiveContent(
    environment = env,
    small = { SmallLayout(state) },
    medium = { MediumLayout(state) },
    large = { LargeLayout(state) },
)

// 2. Pick adaptive values (padding, font sizes, icon sizes):
val iconSize = env.adaptiveValue(small = 16.dp, medium = 20.dp, large = 24.dp)
val fontSize = env.adaptiveValue(small = 14.sp, medium = 18.sp, large = 24.sp)
```

---

## 5. Android Host Integration (`androidMain`)

Create `MyWidgetGlance.kt` in `shared/src/androidMain`:

```kotlin
package com.example.app.widget

import com.atriidev.warp_ui.glance.WarpDrawableAsset
import com.atriidev.warp_widget.WarpGlanceWidget
import com.atriidev.warp_widget.WarpGlanceWidgetReceiver
import com.atriidev.warp_widget.WarpWidgetHostApi
import com.example.app.R

class MyWidgetReceiver : WarpGlanceWidgetReceiver() {
    override fun createGlanceWidget() = MyGlanceAppWidget(createWarpWidget())
    override fun createWarpWidget(): WarpWidgetHostApi = MyWarpWidget
}

class MyGlanceAppWidget(
    private val widget: WarpWidgetHostApi,
) : WarpGlanceWidget() {
    override fun createWarpWidget(): WarpWidgetHostApi = widget

    override fun assets(): List<WarpDrawableAsset> = listOf(
        WarpDrawableAsset(MyWidgetAssets.Plus, R.drawable.ic_plus),
        WarpDrawableAsset(MyWidgetAssets.Minus, R.drawable.ic_minus),
    )
}
```

Register in `shared/src/androidMain/AndroidManifest.xml`:
```xml
<receiver android:name="com.example.app.widget.MyWidgetReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        <action android:name="android.intent.action.CONFIGURATION_CHANGED" />
        <action android:name="android.intent.action.UI_MODE_CHANGED" />
    </intent-filter>
    <meta-data android:name="android.appwidget.provider" android:resource="@xml/my_app_widget_info" />
</receiver>
```

---

## 6. iOS Host Integration & `PlatformContext`

### 6.1 iOS Swift WidgetKit Boilerplate Generation
Setting up Swift WidgetKit structures (`WidgetBundle`, `AppIntent`, `TimelineProvider`, `WidgetView`, `AppEntity`) can be completed using one of two options:

- **Option A (Web Generator - Recommended)**: If browser access is available, use the interactive **Warp Widget Wizard**: [`https://warp.atherio.dev/wizard.html`](https://warp.atherio.dev/wizard.html) (or `wizard.html` locally). Enter your **iOS Widget Name** (e.g. `Counter`), **Kotlin WARP Object Name** (e.g. `CounterWarpWidget`), and **Shared Module Name** (e.g. `Shared`) to download the 6 generated Swift boilerplate files.
- **Option B (Repository Reference Files)**: If browser access is unavailable, download or copy the template Swift files directly from [`https://github.com/DevAtrii/Warp/tree/main/iosApp/CounterWidget`](https://github.com/DevAtrii/Warp/tree/main/iosApp/CounterWidget) (or `iosApp/CounterWidget/` in the local repository) and manually update the widget names and `Shared` framework module imports.

### 6.2 Obtaining `PlatformContext` across Platforms
- **Compose UI (`commonMain`)**:
  ```kotlin
  val context = rememberPlatformContext(widget = MyWarpWidget)
  ```
- **Android (`androidMain`)**: Pass Android `Context`.
- **iOS (`iosMain` or Swift)**:
  ```kotlin
  // Kotlin iosMain:
  val context = getPlatformContext(widget = MyWarpWidget)
  ```
  ```swift
  // Swift:
  let context = getPlatformContext(widget: MyWarpWidget.shared)
  ```

---

## 7. Programmatic State Updates from Main App

Trigger state updates or read saved state from ViewModels, background workers, or services:

```kotlin
import com.atriidev.warp_widget.updateWarpWidgetState
import com.atriidev.warp_widget.readWarpWidgetState
import com.atriidev.warp_widget.listWarpWidgetIds

// Update state:
suspend fun updateWidgetCount(context: PlatformContext, newCount: Int) {
    updateWarpWidgetState(context, MyWarpWidget) { state ->
        state.copy(count = newCount)
    }
}
```

---

## 8. Generation Checklist for AI Assistants

When asked to generate a WARP widget:
- [ ] Define `@Serializable` State data class.
- [ ] Define `@Serializable` Action sealed hierarchy.
- [ ] Implement `WarpActionHandler` mutating state via `updateWarpWidgetState`.
- [ ] Define `object WidgetName : WarpWidget<State>`.
- [ ] Specify matching `id` and `iosGroupId`.
- [ ] Implement `@Composable Content` using WARP DSL (`WarpColumn`, `WarpRow`, `WarpTheme`, `WarpAdaptiveContent`).
- [ ] Wire click handlers in `override fun clickHandlers(...)`.
- [ ] Provide Android Glance `Receiver` and `AppWidget` boilerplate.
- [ ] Provide iOS setup guidance via Widget Wizard ([`https://warp.atherio.dev/wizard.html`](https://warp.atherio.dev/wizard.html)) or reference Swift templates ([`https://github.com/DevAtrii/Warp/tree/main/iosApp/CounterWidget`](https://github.com/DevAtrii/Warp/tree/main/iosApp/CounterWidget)).
