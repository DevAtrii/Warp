# Writing Documentation Guide

This guide explains how to write clean, professional, and visually engaging documentation for this site using **Zensical** (built on Material for MkDocs syntax).

---

## 1. Page Frontmatter

Every `.md` file should start with YAML frontmatter to set metadata and navigation icons.

```yaml
---
icon: lucide/book-open
---
```

Common icons from Lucide:
- `lucide/settings` - Setup & Config
- `lucide/sparkles` - Creating Widgets / Getting Started
- `lucide/proportions` - UI Components & Layouts
- `lucide/paintbrush` - Styling & Themes
- `lucide/scan-line` - Adaptive & Multi-size
- `lucide/brain` - Mental Models & Architecture
- `lucide/code` - Code examples & Guides

---

## 2. Callout Blocks (Admonitions)

Use callout blocks to highlight key takeaways, warnings, notes, or tips. Indent all content inside the callout block by **4 spaces**.

### Types of Callouts

#### Note Callout
```markdown
!!! note "Optional Custom Title"
    This is a standard note callout. Use it for context or secondary information.
```

#### Info Callout
```markdown
!!! info "Android Studio Compatibility"
    Supports Gradle 9.0 and latest KMP plugins.
```

#### Warning Callout
```markdown
!!! warning "Important"
    Don't forget to register your modules! Adding a Gradle dependency alone is not enough.
```

#### Tip Callout
```markdown
!!! tip "Best Practice"
    Structure your screens using separate Screen and Content composables for easier previewing.
```

---

## 3. Code Blocks & Formatting

Enhance code snippets with titles, line numbers, and highlighted lines.

### Code Block Title

Add `title="..."` right after the language specifier:

````markdown
```toml title="gradle/libs.versions.toml"
[versions]
warp = "0.1.4"
```
````

### Line Numbers & Highlighting

Add `linenums="1"` to enable line numbers, and `hl_lines="..."` to highlight specific lines:

````markdown
```kotlin title="shared/build.gradle.kts" linenums="1" hl_lines="3 8-10"
plugins {
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.warp.widget)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.runtime)
        }
    }
}
```
````

---

## 4. Code Tabs

Organize multi-language or multi-option code snippets into interactive tabs using `=== "Tab Title"`:

````markdown
=== "Android (Glance)"

    ```kotlin
    GlanceAppWidgetManager(context)
    ```

=== "iOS (WidgetKit)"

    ```swift
    WidgetCenter.shared.reloadAllTimelines()
    ```
````

---

## 5. Keyboard Shortcuts & UI Badges

- **Keyboard shortcuts**: Use double plus signs (`++cmd+shift+r++` or `++ctrl+shift+r++`).
- **Badges**: Use Shields.io images or standard Markdown badge buttons.

```markdown
<p>
  <img src="https://img.shields.io/maven-central/v/io.github.devatrii/warp-widget?style=for-the-badge&label=Maven&color=6C63FF" alt="Maven Central"/>
</p>
```

---

## 6. Registering Pages in Navigation

Whenever you create a new documentation page, add it to `zensical.toml` under the `nav` section:

```toml title="zensical.toml"
nav = [
  {"Home" = "index.md"},
  {"Mental Model" = "1-mental-model.md"},
  {"Documentation" = [
    "docs/1-setup.md",
    "docs/2-creating-your-first-widget.md",
    "docs/3-warp-composables.md",
    "docs/4-warp-theme.md",
    "docs/5-warp-adaptive.md",
  ]},
  {"How WARP Works" = "docs/2-how-warp-works.md"},
]
```
