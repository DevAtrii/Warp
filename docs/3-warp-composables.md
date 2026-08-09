# WARP Composables Reference

WARP provides a set of declarative, platform-agnostic Compose primitives that render natively on both Android (Glance) and iOS (WidgetKit/SwiftUI).

---

## Layout Composables

### `WarpColumn`
Arranges items vertically from top to bottom.

```kotlin
@Composable
fun WarpColumn(
    modifier: WarpModifier = WarpModifier,
    verticalArrangement: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    horizontalAlignment: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpColumn(
    modifier = WarpModifier.fillMaxSize().padding(16),
    horizontalAlignment = WarpHorizontalAlignment.CenterHorizontally,
    verticalArrangement = WarpVerticalAlignment.CenterVertically
) {
    WarpText(text = "Header")
    WarpSpacer(height = 8)
    WarpText(text = "Subheader")
}
```

---

### `WarpRow`
Arranges items horizontally from left to right.

```kotlin
@Composable
fun WarpRow(
    modifier: WarpModifier = WarpModifier,
    horizontalArrangement: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    verticalAlignment: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpRow(
    modifier = WarpModifier.fillMaxWidth().padding(horizontal = 12),
    verticalAlignment = WarpVerticalAlignment.CenterVertically
) {
    WarpText(text = "Left Item")
    WarpSpacer(modifier = WarpModifier.weight(1f))
    WarpText(text = "Right Item")
}
```

---

### `WarpBox`
Stacks items on top of each other (like a `ZStack` in SwiftUI or `Box` in Jetpack Compose).

```kotlin
@Composable
fun WarpBox(
    modifier: WarpModifier = WarpModifier,
    contentAlignment: WarpContentAlignment = WarpContentAlignment.Center,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpBox(
    modifier = WarpModifier.size(100).background(WarpColor.Hex(0xFF1E88E5)).corner(12),
    contentAlignment = WarpContentAlignment.Center
) {
    WarpText(text = "Badge", style = WarpTextStyle(color = WarpColor.White))
}
```

---

### `WarpLazyColumn` & `WarpLazyRow`
Scrollable / list layouts for displaying dynamic collections of items.

```kotlin
WarpLazyColumn(modifier = WarpModifier.fillMaxSize()) {
    items(state.todos) { todo ->
        WarpRow(modifier = WarpModifier.fillMaxWidth().clickable(CounterAction.Toggle(todo.id))) {
            WarpText(text = if (todo.done) "✓ " + todo.title else "○ " + todo.title)
        }
    }
}
```

---

## Content Composables

### `WarpText`
Renders formatted text content with styling options.

```kotlin
@Composable
fun WarpText(
    text: String,
    modifier: WarpModifier = WarpModifier,
    style: WarpTextStyle = WarpTextStyle(),
    maxLines: Int? = null,
)
```

**Example:**
```kotlin
WarpText(
    text = "Hello WARP",
    style = WarpTextStyle(
        fontSize = 22,
        fontWeight = WarpFontWeight.Bold,
        color = WarpColor.Hex(0xFF6200EE),
        textAlign = WarpTextAlign.Center
    )
)
```

---

### `WarpButton`
Renders an interactive button component that dispatches a type-safe `WarpAction` when clicked.

```kotlin
@Composable
fun WarpButton(
    text: String,
    onClick: WarpAction,
    modifier: WarpModifier = WarpModifier,
    enabled: Boolean = true,
    colors: WarpButtonColors = WarpButtonColors.default(),
)
```

**Example:**
```kotlin
WarpButton(
    text = "Increment Count",
    onClick = CounterAction.Increment,
    modifier = WarpModifier.fillMaxWidth().height(44),
    colors = WarpButtonColors(
        backgroundColor = WarpColor.Hex(0xFF00C853),
        contentColor = WarpColor.White
    )
)
```

---

### `WarpImage`
Displays local resource assets or remote image URLs.

```kotlin
@Composable
fun WarpImage(
    asset: WarpAsset,
    modifier: WarpModifier = WarpModifier,
    contentDescription: String? = null,
    contentScale: WarpContentScale = WarpContentScale.Fit,
)
```

**Example:**
```kotlin
// Resource asset
WarpImage(
    asset = WarpAsset.Resource("ic_widget_logo"),
    modifier = WarpModifier.size(40)
)

// Remote image URL
WarpImage(
    asset = WarpAsset.Url("https://example.com/avatar.png"),
    modifier = WarpModifier.size(48).corner(24)
)
```

---

### `WarpProgressIndicator`
Renders linear or circular progress indicators for task status.

```kotlin
@Composable
fun WarpProgressIndicator(
    progress: Float,
    modifier: WarpModifier = WarpModifier,
    style: WarpProgressIndicatorStyle = WarpProgressIndicatorStyle.Linear,
)
```

**Example:**
```kotlin
WarpProgressIndicator(
    progress = 0.75f,
    modifier = WarpModifier.fillMaxWidth().height(8),
    style = WarpProgressIndicatorStyle.Linear
)
```

---

### `WarpSpacer` & `WarpDivider`
Helper composables for controlling spacing and visual dividers.

```kotlin
// Spacer
WarpSpacer(width = 16)
WarpSpacer(height = 12)

// Divider
WarpDivider(
    modifier = WarpModifier.fillMaxWidth(),
    color = WarpColor.Hex(0xFFE0E0E0),
    thickness = 1
)
```
