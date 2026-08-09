# Styling & Theme System

WARP provides a comprehensive styling system built around `WarpTheme`, dynamic `WarpColor` tokens, text typography models (`WarpTextStyle`), and chainable `WarpModifier` elements.

---

## `WarpTheme`

Wrap your widget content inside `WarpTheme` to set global defaults for text colors, typography, background colors, and light/dark mode adaptations.

```kotlin
@Composable
fun WarpTheme(
    colors: WarpThemeColors = WarpThemeColors.default(),
    typography: WarpTypography = WarpTypography.default(),
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
val CustomPalette = WarpThemeColors(
    primary = WarpColor.Hex(0xFF1E88E5),
    background = WarpColor.Hex(0xFF121212),
    surface = WarpColor.Hex(0xFF1E1E1E),
    onPrimary = WarpColor.White,
    onBackground = WarpColor.White,
)

WarpTheme(colors = CustomPalette) {
    // Widget Composables
}
```

---

## `WarpColor` Representation

Colors in WARP are platform-agnostic tokens serialized cleanly across Kotlin, Glance, and SwiftUI.

### Color Declarations

```kotlin
// Hex Color (ARGB / RGB)
val primaryColor = WarpColor.Hex(0xFF6200EE)

// RGB Color
val accentColor = WarpColor.Rgb(red = 255, green = 111, blue = 0)

// Predefined System Colors
val white = WarpColor.White
val black = WarpColor.Black
val transparent = WarpColor.Transparent
```

---

## Typography & `WarpTextStyle`

`WarpTextStyle` defines text styling for `WarpText`.

```kotlin
data class WarpTextStyle(
    val fontSize: Int = 14,
    val fontWeight: WarpFontWeight = WarpFontWeight.Normal,
    val color: WarpColor = WarpColor.Unspecified,
    val textAlign: WarpTextAlign = WarpTextAlign.Start,
)
```

### Font Weight Options
- `WarpFontWeight.Thin`
- `WarpFontWeight.Light`
- `WarpFontWeight.Normal`
- `WarpFontWeight.Medium`
- `WarpFontWeight.SemiBold`
- `WarpFontWeight.Bold`
- `WarpFontWeight.ExtraBold`

**Example Usage:**
```kotlin
WarpText(
    text = "Dashboard Title",
    style = WarpTextStyle(
        fontSize = 20,
        fontWeight = WarpFontWeight.Bold,
        color = WarpColor.Hex(0xFF00E676),
        textAlign = WarpTextAlign.Center
    )
)
```

---

## The `WarpModifier` System

`WarpModifier` elements let you chain layout constraints, spacing, backgrounds, borders, and touch gestures onto any WARP composable node.

### Padding
```kotlin
// Uniform padding
WarpModifier.padding(16)

// Axis padding
WarpModifier.padding(horizontal = 16, vertical = 8)

// Directional padding
WarpModifier.padding(top = 12, bottom = 12, start = 8, end = 8)
```

### Layout Constraints
```kotlin
WarpModifier.fillMaxSize()       // Fills available container space
WarpModifier.fillMaxWidth()      // Fills container width
WarpModifier.fillMaxHeight()     // Fills container height
WarpModifier.size(width = 100, height = 40) // Fixed size
WarpModifier.height(48)          // Fixed height
WarpModifier.width(120)          // Fixed width
WarpModifier.weight(1f)          // Flex weight inside Column/Row
```

### Appearance & Decoration
```kotlin
WarpModifier.background(WarpColor.Hex(0xFF212121))
WarpModifier.corner(16)          // Rounded corners (dp/pt)
WarpModifier.border(color = WarpColor.Hex(0xFF00E676), width = 2)
WarpModifier.alpha(0.8f)         // Transparency
```

### Interactions & Visibility
```kotlin
WarpModifier.clickable(CounterAction.Increment) // Click gesture binding
WarpModifier.visibility(visible = state.showBadge) // Conditional rendering
```

### Chaining Example
```kotlin
WarpBox(
    modifier = WarpModifier
        .fillMaxWidth()
        .height(60)
        .background(WarpColor.Hex(0xFF1F1B24))
        .corner(16)
        .border(WarpColor.Hex(0xFFBB86FC), width = 1)
        .padding(horizontal = 16)
        .clickable(CounterAction.Reset)
) {
    WarpText(text = "Reset Counter", style = WarpTextStyle(color = WarpColor.White))
}
```
