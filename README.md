<p align="center">
  <img src="docs/assets/logo.webp" alt="WARP Logo" width="160"/>
</p>

<h1 align="center">WARP</h1>

<p align="center">
  Widget Abstraction & Rendering Pipeline for <b>Kotlin Multiplatform</b>.<br>
  Write home screen widgets once in Kotlin — render natively on Android (Glance) and iOS (WidgetKit + SwiftUI).
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 2.4.0" />
  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/iOS-17+-000000?style=for-the-badge&logo=apple&logoColor=white" alt="iOS 17+" />
  <img src="https://img.shields.io/badge/Android-7+-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 7+" />
  <img src="https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge" alt="MIT License" />
  <img src="https://img.shields.io/maven-central/v/io.github.devatrii/warp-widget?style=for-the-badge&label=Version&color=6C63FF" alt="Maven Central Version" />
  <img src="https://hits.sh/github.com/DevAtrii/Warp.svg?style=for-the-badge&label=Views&logo=github" alt="Repository Views" />
</p>

<p align="center">
  <a href="https://warp.atherio.dev">
    <img src="https://img.shields.io/badge/READ%20DOCUMENTATION-Click%20Here-6C63FF?style=for-the-badge" alt="Read Documentation"/>
  </a>
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Support%20Project-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black" alt="Buy Me A Coffee"/>
  </a>
</p>

---

## Overview

https://github.com/user-attachments/assets/50072112-45a3-4bab-bba5-46d696108ec4

> ⚠️ **Alpha Stage**: WARP is currently in **Alpha**. APIs and implementation details may evolve based on community feedback. If you discover any bugs or have feature requests, please [open an issue on GitHub Issues](https://github.com/DevAtrii/Warp/issues).

**WARP** is a declarative widget engine for Kotlin Multiplatform developers. 

It allows you to describe home screen widget UI using familiar Compose syntax (`WarpColumn`, `WarpRow`, `WarpText`, `WarpButton`), serialize the resulting Abstract Syntax Tree (AST) to JSON, and render it using 100% native platform frameworks on each OS:

- **Android**: Compiles to **Jetpack Glance RemoteViews**.
- **iOS**: Compiles to **SwiftUI WidgetKit** extensions with interactive **AppIntents**.

```
Declarative Compose Kotlin UI  ──>  WarpNode AST  ──>  JSON  ──>  Native Platform Renderers
                                          │
                             Shared State & Action Handlers
```

---

## Key Features

- ⚡ **100% Shared UI Code**: Write layout logic, state models, and click actions once in `commonMain`.
- 🎨 **True Native Views**: No canvas painting or heavy Skia runtime. Renders native platform views on both Android and iOS.
- 🪶 **Zero App Size Inflation**: Only depends on `compose.runtime` — does not pull in `compose.ui` graphics pipelines.
- 🔄 **Automatic State Store**: Persistent widget state saved across system restarts using `SharedPreferences` (Android) and `UserDefaults` App Groups (iOS).
- 📐 **Adaptive Size Bucketing**: Automatically scales UI across `Small`, `Medium`, and `Large` widget sizes.
- 🧙 **Interactive Swift Wizard**: Generate iOS WidgetKit SwiftUI and AppIntent boilerplate instantly via [warp.atherio.dev/wizard.html](https://warp.atherio.dev/wizard.html).

---

## Getting Started

### 1. Version Catalog (`libs.versions.toml`)

Add WARP and serialization dependencies to your catalog:

<p>
  <img src="https://img.shields.io/maven-central/v/io.github.devatrii/warp-widget?style=for-the-badge&label=Maven&color=6C63FF" alt="Maven Central Version" />
</p>

```toml
[versions]
warp = "0.1.4"
kotlinx-serialization-json = "1.11.0"
compose-multiplatform = "1.11.1"

[libraries]
warp-widget = { group = "io.github.devatrii", name = "warp-widget", version.ref = "warp" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization-json" }
compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "compose-multiplatform" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
composeCompiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### 2. Shared Gradle Configuration (`build.gradle.kts`)

In your shared KMP module `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export(libs.warp.widget)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.warp.widget)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.runtime)
        }
    }
}
```

---

## Creating Your First Widget

```kotlin
/** 1. State */
@Serializable
data class CounterState(val count: Int = 0)

/** 2. Type-Safe Actions */
@Serializable
sealed class CounterActions {
    @Serializable data object Increment : CounterActions()
    @Serializable data object Decrement : CounterActions()
}

/** 3. Widget Definition */
object CounterWarpWidget : WarpWidget<CounterState>(CounterState.serializer()) {
    override val id: String = "CounterWidget"
    override val iosGroupId: String = "group.com.example.app"
    override val stateScope: WarpWidgetStateScope = WarpWidgetStateScope.Shared

    override suspend fun defaultState(): CounterState = CounterState()

    @Composable
    override fun Content(env: WidgetEnvironment, state: CounterState) {
        WarpTheme(environment = env) {
            WarpRow(
                modifier = WarpModifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = WarpVerticalAlignment.Center
            ) {
                WarpButton("−", CounterActions.Decrement.asClickAction())
                WarpText("${state.count}", modifier = WarpModifier.weight())
                WarpButton("+", CounterActions.Increment.asClickAction())
            }
        }
    }

    override fun clickHandlers(session: WarpWidgetSession): List<WarpActionHandler<*>> =
        listOf(CounterActionHandler(session))
}
```

---

## iOS Build Size & Benchmark Report

Below is the real-world iOS build size report for the [Todo Widget Example App](https://github.com/DevAtrii/Warp/tree/main/examples/todo-widget):

| Artifact | Component / Path | Release Size | Note |
| :--- | :--- | :--- | :--- |
| **Xcode App Store Archive (`.ipa`)** | `TodoWidget.ipa` *(App Thinning Export)* | **1.8 MB** | **Official App Store Download Size** |
| **Installed App Size (Thinned)** | On-Device Installed Footprint | **5.8 MB** | **Official App Store Install Size** |
| **Shared KMP Framework** | `SharedLogic.framework` | **18.0 MB** | **~73% Release Binary Stripping** |
| **Widget Extension Bundle** | `TodoWidgetExtension.appex` | **8.50 MB** | Uncompressed bundle on-disk |

---

## Module Overview

| Module | Role |
| :--- | :--- |
| [warp-runtime](./warp-runtime/) | Compose compiler DSL, `WarpNode` AST tree, `WarpModifier` chain, serializable actions |
| [warp-ui](./warp-ui/) | Native platform renderers — Jetpack Glance (Android) & SwiftUI generator (iOS) |
| [warp-widget](./warp-widget/) | Shared `WarpWidget` API, session manager, persistent state store, `WarpTheme`, `WarpAdaptive` |
| [warpWidgetKit](./warpWidgetKit/) | **SPM** SwiftUI / WidgetKit package (`import warpWidgetKit`) for Xcode extensions |
| [shared](./shared/) | Shared KMP module with Counter widget demo |
| [examples/todo-widget](./examples/todo-widget) | Complete full-featured Todo Widget example application |

---

## Documentation & Resources

- 📖 **Official Documentation**: [https://warp.atherio.dev](https://warp.atherio.dev)
- 🧙 **iOS Widget Wizard**: [https://warp.atherio.dev/wizard.html](https://warp.atherio.dev/wizard.html)
- 💡 **How WARP Works**: [How WARP Works Under the Hood](https://warp.atherio.dev/2-how-warp-works)

---

## License & Usage

WARP is open-source software released under the [MIT License](LICENSE). You are free to use, modify, and distribute it in personal or commercial applications.

<p align="center">
  <a href="https://warp.atherio.dev">
    <img src="https://img.shields.io/badge/READ%20DOCUMENTATION-Click%20Here-6C63FF?style=for-the-badge" alt="Read Documentation"/>
  </a>
  <a href="https://buymeacoffee.com/devatrii" target="_blank">
    <img src="https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Support%20Project-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black" alt="Buy Me A Coffee"/>
  </a>
</p>
