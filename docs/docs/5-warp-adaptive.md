---
icon: lucide/scan-line
---

# Adaptive Widgets

Home screen widgets come in various shapes and sizes—from compact 2x2 squares to wide rectangular cards and full-grid dashboards. **WARP** provides built-in adaptive utilities allowing your declarative UI to scale seamlessly across **Small**, **Medium**, and **Large** layout sizes on both Android and iOS.

---

## 1. Cross-Platform Size Buckets (`WarpAdaptiveSize`)

WARP unifies widget dimensions into three standard `WarpAdaptiveSize` buckets:

| `WarpAdaptiveSize` | Description | Platform Resolution |
| :--- | :--- | :--- |
| `Small` | Compact square widget (2x2 grid) | **iOS**: `systemSmall`<br>**Android**: `widthDp < 250dp` |
| `Medium` | Wide rectangular widget (4x2 grid) | **iOS**: `systemMedium`<br>**Android**: `250dp ≤ widthDp < 550dp` |
| `Large` | Large multi-row widget (4x4 grid) | **iOS**: `systemLarge` / `systemExtraLarge`<br>**Android**: `widthDp ≥ 550dp` or `heightDp ≥ 170dp` |

---

## 2. Adaptive Layouts (`WarpAdaptiveContent`)

Use `WarpAdaptiveContent` to swap composable layouts dynamically based on the active widget size bucket:

```kotlin title="WarpAdaptiveContent Usage"
@Composable
override fun Content(env: WidgetEnvironment, state: CounterState) {
    WarpTheme(environment = env) {
        WarpAdaptiveContent(
            environment = env,
            small = { CompactCounterWidget(state) },
            medium = { WideCounterWidget(state) },
            large = { FullDashboardWidget(state) },
        )
    }
}
```

*Note: `medium` defaults to `small` if omitted, and `large` defaults to `medium` if omitted.*

---

## 3. Dynamic Values (`env.adaptiveValue`)

When you don't need a full layout swap, use `env.adaptiveValue()` to pick scalable UI properties (such as font sizes, padding, corner radii, or icon sizes) without adding composable wrapper nodes:

```kotlin title="adaptiveValue Usage"
val buttonSize = env.adaptiveValue(small = 36.dp, medium = 40.dp, large = 48.dp)
val countFontSize = env.adaptiveValue(small = 22.sp, medium = 26.sp, large = 32.sp)
val cornerRadius = env.adaptiveValue(small = 12.dp, medium = 16.dp, large = 20.dp)

WarpButton(
    text = "+",
    onClick = CounterActions.Increment.asClickAction(),
    modifier = WarpModifier.size(buttonSize).cornerRadius(cornerRadius),
)
```

---

## 4. Remembering Adaptive Size (`rememberWarpAdaptiveSize`)

To branch logic or remember the current size bucket across recompositions (recalculating automatically when dimensions change during user resize):

```kotlin title="rememberWarpAdaptiveSize Usage"
@Composable
fun MyWidget(env: WidgetEnvironment) {
    val adaptiveSize = rememberWarpAdaptiveSize(env)

    if (adaptiveSize == WarpAdaptiveSize.Large) {
        // Render detailed multi-column view
    } else {
        // Render compact view
    }
}
```

---

## 5. Size Queries & Helpers

Use boolean environment extension properties for quick layout branching:

```kotlin title="Environment Extension Helpers"
if (env.isSmallAdaptive()) {
    // Small widget specific adjustments
}

if (env.isMediumAdaptive()) {
    // Medium widget specific adjustments
}

if (env.isLargeAdaptive()) {
    // Large widget specific adjustments
}
```

---

## 6. Custom Breakpoint Calculation

If your widget design requires custom dp thresholds (e.g. switching to Large at 400dp instead of 550dp), pass a custom `calc` lambda to `WarpAdaptiveContent` or `rememberWarpAdaptiveSize`:

```kotlin title="Custom Breakpoints Example"
WarpAdaptiveContent(
    environment = env,
    calc = { widthDp, heightDp ->
        when {
            widthDp < 200f -> WarpAdaptiveSize.Small
            widthDp >= 400f -> WarpAdaptiveSize.Large
            else -> WarpAdaptiveSize.Medium
        }
    },
    small = { SmallLayout() },
    medium = { MediumLayout() },
    large = { LargeLayout() },
)
```
