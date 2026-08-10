---
icon: lucide/settings
---

# Setup

Setting up **WARP** in your Kotlin Multiplatform project is straightforward. You only need to add the dependencies to your Version Catalog (`libs.versions.toml`) and configure your shared `build.gradle.kts`.

---

## 1. Version Catalog

Add the WARP dependency, KotlinX Serialization, and Compose Runtime to your `gradle/libs.versions.toml`:

```toml title="gradle/libs.versions.toml"
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

---

## 2. Shared Module Gradle Configuration

In your shared module's `build.gradle.kts` (e.g., `shared/build.gradle.kts`), apply the serialization plugin, export `warp-widget` in iOS framework binaries, and declare dependencies in `commonMain`:

```kotlin title="shared/build.gradle.kts" hl_lines="3 13 25-27"
plugins {
    // ...
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            // ...
            export(libs.warp.widget)
        }
    }

    androidTarget { 
        // ...
    }

    sourceSets {
        // ...
        commonMain.dependencies {
            // Multiplatform dependencies
            api(libs.warp.widget)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.runtime)
        }
    }
}
```

---

!!! note "App Size & KMP Business-Logic Compatibility"
    Notice that we are **only including `compose.runtime`** here. It does **not** pull in Compose Multiplatform UI (`compose.ui`, graphics, or rendering pipelines), meaning it **will not increase your app size**. 
    
    This allows WARP to seamlessly work even in pure Kotlin Multiplatform (KMP) projects that only share business logic without full Compose Multiplatform UI.

    📊 For an app size benchmark and detailed impact breakdown, check the [Todo Widget Benchmark Example](https://github.com/DevAtrii/Warp/tree/main/examples/todo-widget#ios-build-size-report).