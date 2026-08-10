---
icon: lucide/proportions
---

# WARP Composables Reference

WARP provides a declarative, platform-agnostic Compose DSL to build cross-platform home screen widgets for **Android (Glance)** and **iOS (WidgetKit/SwiftUI)**.

---

## 1. Layout Composables

Layout composables structure child elements vertically, horizontally, or stacked in layers.

### `WarpColumn`
Arranges child composables vertically in a top-to-bottom column layout.

```kotlin title="WarpColumn Signature"
@Composable
fun WarpColumn(
    modifier: WarpModifier = WarpModifier(),
    verticalAlignment: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    horizontalAlignment: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpColumn(
    modifier = WarpModifier.fillMaxWidth().padding(16.dp),
    horizontalAlignment = WarpHorizontalAlignment.CenterHorizontally,
    verticalAlignment = WarpVerticalAlignment.Center,
) {
    WarpText(text = "Header Title", style = WarpTextStyle(fontWeight = WarpFontWeight.Bold))
    WarpSpacer(modifier = WarpModifier.height(8.dp))
    WarpText(text = "Subtitle description goes here.")
}
```

---

### `WarpRow`
Arranges child composables horizontally from left to right.

```kotlin title="WarpRow Signature"
@Composable
fun WarpRow(
    modifier: WarpModifier = WarpModifier(),
    horizontalAlignment: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    verticalAlignment: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpRow(
    modifier = WarpModifier.fillMaxWidth().padding(horizontal = 12.dp),
    verticalAlignment = WarpVerticalAlignment.Center,
) {
    WarpText(text = "Left Label")
    WarpSpacer(modifier = WarpModifier.weight(1f))
    WarpText(text = "Right Status")
}
```

---

### `WarpBox`
Stacks child composables on top of each other (similar to Jetpack Compose `Box` or SwiftUI `ZStack`).

```kotlin title="WarpBox Signature"
@Composable
fun WarpBox(
    modifier: WarpModifier = WarpModifier(),
    contentAlignment: WarpContentAlignment = WarpContentAlignment.TopStart,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
WarpBox(
    modifier = WarpModifier
        .fillMaxSize()
        .background(WarpColor.Blue600)
        .cornerRadius(16.dp),
    contentAlignment = WarpContentAlignment.Center,
) {
    WarpText(text = "Centered Badge", style = WarpTextStyle(color = WarpColor.White))
}
```

---

### `WarpSpacer`
Creates empty space within a `WarpColumn`, `WarpRow`, or `WarpBox`. Use size modifiers (`width`, `height`, `size`, `weight`) to specify spacing.

```kotlin title="WarpSpacer Signature"
@Composable
fun WarpSpacer(
    modifier: WarpModifier = WarpModifier(),
)
```

**Example:**
```kotlin
WarpSpacer(modifier = WarpModifier.height(12.dp))
WarpSpacer(modifier = WarpModifier.weight(1f)) // Fills remaining flex space
```

---

### `WarpLazyColumn` & `WarpLazyRow` (Experimental)
Scrollable list containers for displaying dynamic collections of items.

```kotlin title="WarpLazyColumn Signature"
@WarpExperimentalApi
@Composable
fun WarpLazyColumn(
    modifier: WarpModifier = WarpModifier(),
    verticalAlignment: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    horizontalAlignment: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    content: @Composable () -> Unit,
)
```

```kotlin title="WarpLazyRow Signature"
@WarpExperimentalApi
@Composable
fun WarpLazyRow(
    modifier: WarpModifier = WarpModifier(),
    horizontalAlignment: WarpHorizontalAlignment = WarpHorizontalAlignment.Start,
    verticalAlignment: WarpVerticalAlignment = WarpVerticalAlignment.Top,
    content: @Composable () -> Unit,
)
```

!!! danger "Experimental APIs & Platform Limitations"
    `WarpLazyColumn` and `WarpLazyRow` are marked with `@WarpExperimentalApi`. 
    
    - **iOS (WidgetKit)**: WidgetKit does not support interactive scrolling lists. WARP falls back to rendering items inside standard static `VStack` or `HStack` layouts.
    - **Android (Glance)**: Uses Glance `LazyColumn`, but widget memory budgets can cause list scrolling to feel laggy or truncate large item sets.
    
    *Best Practice*: Limit items to a small fixed count (e.g. 3–5 items) or use standard `WarpColumn` / `WarpRow` loops where possible.

---

## 2. Content Composables

Content composables render visual elements such as text, images, buttons, dividers, and progress indicators.

### `WarpText`
Renders read-only text with customizable font size, weight, color, alignment, and maximum lines.

```kotlin title="WarpText Signature"
@Composable
fun WarpText(
    text: String,
    modifier: WarpModifier = WarpModifier(),
    style: WarpTextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
)
```

**Example:**
```kotlin
WarpText(
    text = "Hello WARP Widget",
    modifier = WarpModifier.fillMaxWidth(),
    style = WarpTextStyle(
        color = WarpColor.Blue700,
        fontSize = 18.sp,
        fontWeight = WarpFontWeight.Bold,
        textAlign = WarpTextAlign.Center,
    ),
    maxLines = 1,
)
```

---

### `WarpButton`
Renders an interactive button component that dispatches a type-safe action upon user taps. Supports simple text labels or trailing composable content blocks.

```kotlin title="WarpButton Signature (Label Overload)"
@Composable
fun WarpButton(
    text: String,
    onClick: WarpAction,
    modifier: WarpModifier = WarpModifier(),
    enabled: Boolean = true,
    style: WarpTextStyle? = null,
    colors: WarpButtonColors? = null,
    maxLines: Int = Int.MAX_VALUE,
)
```

```kotlin title="WarpButton Signature (Container Overload)"
@Composable
fun WarpButton(
    onClick: WarpAction,
    modifier: WarpModifier = WarpModifier(),
    enabled: Boolean = true,
    content: @Composable () -> Unit,
)
```

**Example:**
```kotlin
// Text button with custom colors
WarpButton(
    text = "+ Increment",
    onClick = CounterActions.Increment.asClickAction(),
    modifier = WarpModifier.height(40.dp).cornerRadius(8.dp),
    colors = WarpButtonColors(
        backgroundColor = WarpColor.Green,
        contentColor = WarpColor.White,
    ),
)
```

---

### `WarpImage`
Renders cross-platform image assets, including native iOS **SF Symbols**, app-bundled drawables, and local URIs.

```kotlin title="WarpImage Signature"
@Composable
fun WarpImage(
    asset: WarpAsset,
    contentDescription: String? = null,
    modifier: WarpModifier = WarpModifier(),
    contentScale: WarpContentScale = WarpContentScale.Fit,
    tint: WarpColor? = null,
)
```

**Example:**
```kotlin
WarpImage(
    asset = CounterAssets.Plus.asSystem(),
    contentDescription = "Add Item",
    modifier = WarpModifier.size(24.dp),
    tint = WarpColor.Blue600,
)
```

---

### `WarpProgressIndicator`
Renders determinate (`progress = 0f..1f`) or indeterminate (`progress = null`) linear or circular progress indicators.

```kotlin title="WarpProgressIndicator Signature"
@Composable
fun WarpProgressIndicator(
    modifier: WarpModifier = WarpModifier(),
    style: WarpProgressIndicatorStyle = WarpProgressIndicatorStyle.Circular,
    progress: Float? = null,
    color: WarpColor? = null,
    backgroundColor: WarpColor? = null,
)
```

**Example:**
```kotlin
WarpProgressIndicator(
    progress = 0.75f,
    modifier = WarpModifier.fillMaxWidth().height(6.dp),
    style = WarpProgressIndicatorStyle.Linear,
    color = WarpColor.Blue600,
    backgroundColor = WarpColor.Blue100,
)
```

---

### `WarpDivider`
Renders a thin horizontal separator line.

```kotlin title="WarpDivider Signature"
@Composable
fun WarpDivider(
    modifier: WarpModifier = WarpModifier(),
    thickness: Dp = 1.dp,
    color: WarpColor? = null,
)
```

**Example:**
```kotlin
WarpDivider(
    modifier = WarpModifier.fillMaxWidth(),
    thickness = 0.5.dp,
    color = WarpColor.Gray,
)
```

---

## 3. Modifiers (`WarpModifier`)

`WarpModifier` is an immutable, chainable modifier sequence used to apply dimensions, padding, background colors, borders, corner radii, visibility, and click listeners.

### 3.1 Dimensions & Layout Constraints

| Modifier | Description | Example |
| :--- | :--- | :--- |
| `fillMaxWidth()` | Expands the component's width to fill parent constraints. | `WarpModifier.fillMaxWidth()` |
| `fillMaxHeight()` | Expands the component's height to fill parent constraints. | `WarpModifier.fillMaxHeight()` |
| `fillMaxSize()` | Expands both width and height to fill parent constraints. | `WarpModifier.fillMaxSize()` |
| `width(width)` | Sets a fixed width (`Dp` or `Number`). | `WarpModifier.width(100.dp)` |
| `height(height)` | Sets a fixed height (`Dp` or `Number`). | `WarpModifier.height(44.dp)` |
| `size(size)` | Sets equal fixed width and height. | `WarpModifier.size(36.dp)` |
| `size(width, height)` | Sets explicit width and height. | `WarpModifier.size(120.dp, 40.dp)` |
| `weight(weight)` | Assigns flex weight inside `WarpRow` or `WarpColumn`. | `WarpModifier.weight(1f)` |
| `wrapContentWidth()` | Wraps content width based on children. | `WarpModifier.wrapContentWidth()` |
| `wrapContentHeight()` | Wraps content height based on children. | `WarpModifier.wrapContentHeight()` |
| `wrapContentSize()` | Wraps both width and height. | `WarpModifier.wrapContentSize()` |

---

### 3.2 Spacing & Padding

| Modifier | Description | Example |
| :--- | :--- | :--- |
| `padding(all)` | Applies uniform padding to all 4 edges. | `WarpModifier.padding(16.dp)` |
| `padding(horizontal, vertical)` | Applies symmetric horizontal and vertical padding. | `WarpModifier.padding(horizontal = 12.dp, vertical = 8.dp)` |
| `padding(start, end, top, bottom)` | Applies explicit padding to individual edges. | `WarpModifier.padding(start = 8.dp, top = 4.dp)` |
| `padding(WarpPadding)` | Accepts a pre-constructed `WarpPadding` object. | `WarpModifier.padding(WarpPadding(8.dp))` |

---

### 3.3 Appearance & Styling

| Modifier | Description | Example |
| :--- | :--- | :--- |
| `background(color)` | Sets background color using `WarpColor`. | `WarpModifier.background(WarpColor.Blue600)` |
| `cornerRadius(radius)` | Applies rounded corners (`Dp` or `Number`). | `WarpModifier.cornerRadius(12.dp)` |
| `border(width, color)` | Applies an outline border with color or hex string. | `WarpModifier.border(1.dp, WarpColor.Gray)` |
| `alpha(alpha)` | Adjusts opacity (`0f` transparent to `1f` opaque). | `WarpModifier.alpha(0.8f)` |
| `visibility(visibility)` | Controls component visibility (`Visible`, `Invisible`, `Gone`). | `WarpModifier.visibility(WarpVisibility.Visible)` |

---

### 3.4 Interactive Click Listeners

| Modifier | Description | Example |
| :--- | :--- | :--- |
| `clickable(action)` | Attaches a type-safe `WarpAction` tap listener to any node. | `WarpModifier.clickable(CounterActions.Reset.asClickAction())` |

---

### Complete Modifier Chaining Example

```kotlin
WarpColumn(
    modifier = WarpModifier
        .fillMaxWidth()
        .background(WarpColor.White)
        .cornerRadius(16.dp)
        .border(1.dp, WarpColor.Blue200)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .clickable(CounterActions.Reset.asClickAction()),
) {
    WarpText(text = "Tap to Reset")
}
```
