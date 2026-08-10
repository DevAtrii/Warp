---
icon: lucide/scan-line
---

# Adaptive Widgets & Multi-Size Support

Widgets come in various shapes and sizes on user home screens and lock screens. WARP provides built-in environment context allowing your Compose UI code to adapt dynamically to small, medium, large, and accessory lock screen widgets.

---

## Supported Widget Families

WARP unifies Android Glance size ranges and iOS WidgetKit families into standard `WarpWidgetFamily` enums:

| `WarpWidgetFamily` | Description | Typical Use Cases |
| :--- | :--- | :--- |
| `SYSTEM_SMALL` | Small square widget (2x2 grid) | Quick metric, single counter, icon badge |
| `SYSTEM_MEDIUM` | Wide rectangular widget (4x2 grid) | List previews, dual metrics, header + action buttons |
| `SYSTEM_LARGE` | Large square widget (4x4 grid) | Full task lists, detailed graphs, multi-column views |
| `SYSTEM_EXTRA_LARGE` | Extra large widget (iPadOS / Tablet) | Full dashboard views |
| `ACCESSORY_CIRCULAR` | Circular lock screen gauge / icon | iOS Lock Screen / Watch circular complication |
| `ACCESSORY_RECTANGULAR` | Rectangular lock screen widget | iOS Lock Screen multi-line status update |
| `ACCESSORY_INLINE` | Single-line lock screen text | iOS Lock Screen top status text |

---

## Accessing Widget Environment Context

Inside any `@Composable` content function, access the current widget size, family, and theme environment using `LocalWidgetEnvironment.current`:

```kotlin
@Composable
override fun Content(state: CounterState) {
    val environment = LocalWidgetEnvironment.current
    val family = environment.family

    when (family) {
        WarpWidgetFamily.SYSTEM_SMALL -> SmallWidgetView(state)
        WarpWidgetFamily.SYSTEM_MEDIUM -> MediumWidgetView(state)
        WarpWidgetFamily.SYSTEM_LARGE -> LargeWidgetView(state)
        WarpWidgetFamily.ACCESSORY_CIRCULAR -> CircularLockScreenView(state)
        else -> SmallWidgetView(state)
    }
}
```

---

## Responsive Layout Examples

### Small Widget View (`SYSTEM_SMALL`)
Focuses on a single primary metric or minimal control:

```kotlin
@Composable
fun SmallWidgetView(state: CounterState) {
    WarpColumn(
        modifier = WarpModifier.fillMaxSize().padding(12),
        horizontalAlignment = WarpHorizontalAlignment.CenterHorizontally,
        verticalAlignment = WarpVerticalAlignment.CenterVertically
    ) {
        WarpText(text = "Count", style = WarpTextStyle(fontSize = 12))
        WarpText(text = "${state.count}", style = WarpTextStyle(fontSize = 32, fontWeight = WarpFontWeight.Bold))
        WarpButton(text = "+1", onClick = CounterAction.Increment)
    }
}
```

### Medium Widget View (`SYSTEM_MEDIUM`)
Presents extended metrics, progress indicators, or multiple action buttons:

```kotlin
@Composable
fun MediumWidgetView(state: CounterState) {
    WarpRow(
        modifier = WarpModifier.fillMaxSize().padding(16),
        verticalAlignment = WarpVerticalAlignment.CenterVertically
    ) {
        WarpColumn(modifier = WarpModifier.weight(1f)) {
            WarpText(text = "Total Count", style = WarpTextStyle(fontSize = 14, fontWeight = WarpFontWeight.Bold))
            WarpText(text = "${state.count}", style = WarpTextStyle(fontSize = 36, fontWeight = WarpFontWeight.Bold))
            WarpProgressIndicator(progress = (state.count % 10) / 10f, modifier = WarpModifier.fillMaxWidth().height(6))
        }
        WarpSpacer(width = 16)
        WarpColumn {
            WarpButton(text = "Increment", onClick = CounterAction.Increment)
            WarpSpacer(height = 8)
            WarpButton(text = "Decrement", onClick = CounterAction.Decrement)
        }
    }
}
```

---

## Lock Screen & Accessory Widgets (iOS & Android)

For lock screen widgets (`ACCESSORY_CIRCULAR` or `ACCESSORY_RECTANGULAR`), render ultra-compact UIs without heavy background colors:

```kotlin
@Composable
fun CircularLockScreenView(state: CounterState) {
    WarpBox(
        modifier = WarpModifier.fillMaxSize(),
        contentAlignment = WarpContentAlignment.Center
    ) {
        WarpProgressIndicator(
            progress = (state.count % 100) / 100f,
            style = WarpProgressIndicatorStyle.Circular
        )
        WarpText(
            text = "${state.count}",
            style = WarpTextStyle(fontSize = 14, fontWeight = WarpFontWeight.Bold)
        )
    }
}
```
