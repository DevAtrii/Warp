https://github.com/user-attachments/assets/6bcdd802-41fc-4ac7-8629-e9711f0fb1f8


## WARP

> **Alpha Stage** — APIs and architecture are in alpha. Follow [@dev_atrii on X](https://x.com/dev_atrii) for updates.

**WARP** (**W**idget **A**bstraction, **R**endering **P**ipeline) is a unified API for creating home-screen widgets in **Kotlin Multiplatform**.

Write widget UI once in Kotlin → render on **Android** (Jetpack Glance) and **iOS** (WidgetKit + SwiftUI).

```
Compose-like Kotlin UI  →  WarpNode tree  →  JSON  →  platform renderer
                              ↑
                         shared state & click handlers
```

### Installation & Dependencies

You can configure dependencies via `gradle/libs.versions.toml`:

```toml
[versions]
warp = "0.1.4"
kotlinx-serialization-json = "1.6.3"

[libraries]
warp-runtime = { module = "dev.atherio.warp:warp-runtime", version.ref = "warp" }
warp-ui = { module = "dev.atherio.warp:warp-ui", version.ref = "warp" }
warp-widget = { module = "dev.atherio.warp:warp-widget", version.ref = "warp" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization-json" }
```

In your shared module's `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.warp.runtime)
            implementation(libs.warp.widget)
            implementation(libs.warp.ui)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
```

> 🪄 **Interactive Setup Wizard**: Easily customize dependency snippets and project setups for Android & iOS at [warp.atherio.dev](https://warp.atherio.dev/).

---

### iOS Setup with Wizard

To quickly configure iOS WidgetKit extensions, Swift AppIntents, and Swift Package Manager (`warpWidgetKit`) integrations, generate ready-to-use Xcode & Swift snippets using the **WARP Setup Wizard**:

🔗 **[https://warp.atherio.dev/](https://warp.atherio.dev/)**

The wizard guides you through:
1. Configuring your **App Group ID** (e.g. `group.com.yourcompany.app`).
2. Setting up `warpWidgetKit` SPM dependency in Xcode.
3. Generating Swift AppIntent (`WarpClickAppIntent`) and `WidgetBundle` entry points.

---

## Implementing `TodoWarpWidget` — Overview & Guide

The [`TodoWarpWidget`](./examples/todo-widget/sharedLogic/src/commonMain/kotlin/com/atriidev/todowidget/widgets/TodoWarpWidget.kt) in `examples/todo-widget` demonstrates how to build interactive, stateful, and responsive widgets with WARP across Android and iOS.

### 1. State Definition
Define serializable data structures representing the state of your widget:

```kotlin
@Serializable
@Stable
data class TodoWidgetState(
    val todos: List<TodoItem> = emptyList()
)

@Serializable
data class TodoItem(val id: Int, val title: String, val done: Boolean)
```

### 2. Sealed Actions & Click Handlers
Define type-safe actions using `@Serializable` sealed interfaces and handle state updates asynchronously in a `WarpClickHandler`:

```kotlin
@Serializable
sealed interface TodoActions {
    @Serializable data class Toggle(val todoId: Int) : TodoActions
    @Serializable data object Clear : TodoActions
    @Serializable data object AddSample : TodoActions
}

private class TodoClickHandler(
    private val session: WarpWidgetSession
) : WarpClickHandler<TodoActions>(serializer = TodoActions.serializer()) {
    override suspend fun onAction(action: TodoActions) {
        when (action) {
            is TodoActions.Toggle -> updateWarpWidgetState(session, TodoWarpWidget) { state ->
                state.copy(todos = state.todos.map { 
                    if (it.id == action.todoId) it.copy(done = !it.done) else it 
                })
            }
            is TodoActions.Clear -> updateWarpWidgetState(session, TodoWarpWidget) { state.copy(todos = emptyList()) }
            is TodoActions.AddSample -> updateWarpWidgetState(session, TodoWarpWidget) { sampleTodoWidgetState }
        }
    }
}
```

### 3. Type-Safe Assets
Declare asset identifiers shared between Kotlin UI and platform asset catalogs:

```kotlin
object TodoAssets {
    val Circle = WarpAssetId("circle")
    val CheckCircle = WarpAssetId("checkmark.circle.fill")
    val Plus = WarpAssetId("plus")
    val Trash = WarpAssetId("trash")
}
```

### 4. Widget Definition & Adaptive UI
Inherit from `WarpWidget<State>`, define state scopes, and specify UI layouts for different widget family sizes using `WarpAdaptiveContent`:

```kotlin
object TodoWarpWidget : WarpWidget<TodoWidgetState>(stateSerializer = TodoWidgetState.serializer()) {
    override val id: String = "TodoWidget"
    override val iosGroupId: String = "group.warpexample.todowidget"
    override val defaultState: TodoWidgetState = TodoWidgetState()
    override val stateScope: WarpWidgetStateScope = WarpWidgetStateScope.Shared

    @Composable
    override fun Content(env: WidgetEnvironment, state: TodoWidgetState) {
        WarpTheme(environment = env) {
            WarpAdaptiveContent(
                environment = env,
                small = { TodoWidgetContent(state, env, compact = true) },
                medium = { TodoWidgetContent(state, env) },
                large = { TodoWidgetContent(state, env, spacious = true) }
            )
        }
    }

    override fun clickHandlers(session: WarpWidgetSession): List<WarpClickHandler<*>> = listOf(
        TodoClickHandler(session)
    )
}
```

---

### Modules

| Module | Role |
|--------|------|
| [warp-runtime](./warp-runtime/) | Author widget UI (`WarpColumn`, `WarpText`, `WarpButton`), compose to tree/JSON, click wire format |
| [warp-ui](./warp-ui/) | Platform renderers — Glance (Android), iOS via [spm4Kmp](https://github.com/frankois944/spm4Kmp) |
| [warp-widget](./warp-widget/) | Shared `WarpWidget` definition, session/env, prefs store, host API (`WarpWidgetHost`) |
| [warpWidgetKit](./warpWidgetKit/) | **SPM** SwiftUI / WidgetKit package (`import warpWidgetKit`) — local now, remote later |
| [shared](./shared/) | App + demo widgets (counter), shared click handlers, DataStore |
| [examples/todo-widget](./examples/todo-widget) | Complete full-featured Todo Widget example application |
| [androidApp](./androidApp/) | Android host app + Glance widget |
| [iosApp](./iosApp/) | iOS host app + Counter Widget extension (`.systemSmall`) |

### Docs

- [warp-runtime README](./warp-runtime/README.md) — composing widgets, JSON, click actions
- [warp-runtime click guide](./warp-runtime/README_CLICK.md) — handler registry & dispatch
- [warp-ui README](./warp-ui/README.md) — `WarpRender`, `warpRender`, iOS WidgetKit setup
- [warp-widget README](./warp-widget/README.md) — `WarpWidget`, session, state, Glance / WidgetKit hosts

### Status

| Platform | Renderer | Demo |
|----------|----------|------|
| Android | Jetpack Glance ✓ | Counter widget & Todo widget ✓ |
| iOS | WidgetKit + SwiftUI ✓ | Counter widget & Todo widget (`.systemSmall`, `.systemMedium`, `.systemLarge`) ✓ |
| API stability | Early / experimental | — |

---

## Running the apps

- **Android:** `./gradlew :androidApp:assembleDebug` — install app, add Counter or Todo widget from launcher
- **iOS:** open [iosApp](./iosApp) in Xcode, run **iosApp**, add **Counter** / **Todo** widget (requires App Group + iOS 17+)

### Verify builds

```bash
./gradlew :warp-runtime:jvmTest
./gradlew :warp-ui:compileKotlinIosSimulatorArm64
./gradlew :androidApp:assembleDebug
```

---

## Project layout

* [/iosApp](./iosApp) — iOS application and Widget Extension entry points
* [/shared](./shared/src) — shared Kotlin (commonMain, androidMain, iosMain)
* [/examples/todo-widget](./examples/todo-widget) — full sample Todo Widget implementation
* [/androidApp](./androidApp) — Android application

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).

