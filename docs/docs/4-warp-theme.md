---
icon: lucide/paintbrush
---

# Theming Widget

WARP provides a Material 3-inspired theme system built around `WarpTheme` and cross-platform `WarpColors` tokens. It automatically adapts to platform defaults (Material 3 on Android, System Blue on iOS) and handles dynamic Light/Dark mode switching based on the `WidgetEnvironment`.

---

## 1. Overview of `WarpTheme`

Wrap your widget content inside `WarpTheme` to set up color tokens for all nested composables. You can access the active color scheme anywhere in your composition via `WarpTheme.colors`.

```kotlin title="Basic Theme Usage"
@Composable
override fun Content(env: WidgetEnvironment, state: CounterState) {
    WarpTheme(environment = env) {
        WarpBox(
            modifier = WarpModifier
                .fillMaxSize()
                .background(WarpTheme.colors.widgetBackground)
                .padding(16.dp),
        ) {
            WarpText(
                text = "Hello WARP Widget",
                style = WarpTextStyle(
                    color = WarpTheme.colors.onSurface,
                    fontSize = 18.sp,
                    fontWeight = WarpFontWeight.Bold,
                ),
            )
        }
    }
}
```

---

## 2. Automatic Environment & Dark Mode Resolution

The standard `WarpTheme` overload accepts a `WidgetEnvironment` object to dynamically resolve theme palettes:

```kotlin title="Environment Theme Signature"
@Composable
fun WarpTheme(
    environment: WidgetEnvironment,
    lightColors: WarpColors = WarpColors.defaultLight(environment.platform),
    darkColors: WarpColors = WarpColors.defaultDark(environment.platform),
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
)
```

### Key Behaviors:
- **Platform Defaults**:
  - **Android**: Defaults to Material 3 baseline palettes (`Material3Light` / `Material3Dark`).
  - **iOS**: Defaults to iOS System Blue baseline palettes (`IosLight` / `IosDark`).
- **Dark Mode Resolution**: Reads `environment.theme`. When the host OS switches between Light and Dark appearance, `WarpTheme` updates its active `WarpColors` automatically.
- **Explicit Theme Override**: You can force light or dark mode using the `darkTheme` parameter (e.g. `darkTheme = false.takeIf { env.platform.isAndroid }`).

---

## 3. Color Roles (`WarpColors`)

`WarpColors` mirrors Material 3 color roles using serializable `WarpColor` tokens across platforms:

### Primary & Accent Roles
- `primary` / `onPrimary`: Main brand accent color and text/icon color on primary backgrounds.
- `primaryContainer` / `onPrimaryContainer`: Tonal container backgrounds and text.
- `secondary` / `onSecondary` / `secondaryContainer` / `onSecondaryContainer`: Secondary accent elements.
- `tertiary` / `onTertiary` / `tertiaryContainer` / `onTertiaryContainer`: Contrast accent highlights.

### Surface & Background Roles
- **`widgetBackground`**: Tailored background color for the main widget surface card.
- `background` / `onBackground`: General background colors.
- `surface` / `onSurface`: Main surface card and text colors.
- `surfaceVariant` / `onSurfaceVariant`: Elevated or subtle container chips and row backgrounds.
- `outline`: Divider lines and border outlines.

### Feedback Roles
- `error` / `onError` / `errorContainer` / `onErrorContainer`: Validation or error indicator colors.

---

## 4. Custom Color Schemes

You can create custom light and dark color schemes using `WarpColors.light(...)` and `WarpColors.dark(...)` with hex strings:

```kotlin title="Custom Color Palette Example"
val CustomLightColors = WarpColors.light(
    primary = "#1E88E5",
    onPrimary = "#FFFFFF",
    primaryContainer = "#D6EBFF",
    onPrimaryContainer = "#004080",
    secondary = "#625B71",
    onSecondary = "#FFFFFF",
    secondaryContainer = "#E8DEF8",
    onSecondaryContainer = "#1D192B",
    tertiary = "#7D5260",
    onTertiary = "#FFFFFF",
    tertiaryContainer = "#FFD8E4",
    onTertiaryContainer = "#31111D",
    error = "#B3261E",
    onError = "#FFFFFF",
    errorContainer = "#F9DEDC",
    onErrorContainer = "#410E0B",
    background = "#F4F6F8",
    onBackground = "#1C1B1F",
    surface = "#FFFFFF",
    onSurface = "#1C1B1F",
    surfaceVariant = "#E7E0EC",
    onSurfaceVariant = "#49454F",
    outline = "#79747E",
    inverseSurface = "#313033",
    inverseOnSurface = "#F4EFF4",
    inversePrimary = "#D0BCFF",
    widgetBackground = "#FFFFFF",
)

val CustomDarkColors = WarpColors.dark(
    primary = "#90CAF9",
    onPrimary = "#0D47A1",
    primaryContainer = "#1565C0",
    onPrimaryContainer = "#D6EBFF",
    secondary = "#CCC2DC",
    onSecondary = "#332D41",
    secondaryContainer = "#4A4458",
    onSecondaryContainer = "#E8DEF8",
    tertiary = "#EFB8C8",
    onTertiary = "#492532",
    tertiaryContainer = "#633B48",
    onTertiaryContainer = "#FFD8E4",
    error = "#F2B8B5",
    onError = "#601410",
    errorContainer = "#8C1D18",
    onErrorContainer = "#F9DEDC",
    background = "#121212",
    onBackground = "#E6E1E5",
    surface = "#1E1E1E",
    onSurface = "#E6E1E5",
    surfaceVariant = "#2C2C2E",
    onSurfaceVariant = "#CAC4D0",
    outline = "#938F99",
    inverseSurface = "#E6E1E5",
    inverseOnSurface = "#313033",
    inversePrimary = "#1E88E5",
    widgetBackground = "#121212",
)

// Pass custom color schemes into WarpTheme:
WarpTheme(
    environment = env,
    lightColors = CustomLightColors,
    darkColors = CustomDarkColors,
) {
    // Widget UI
}
```

---

## 5. Direct `WarpColor` Usage

Outside of `WarpTheme`, you can declare explicit `WarpColor` instances directly:

- **Hex String**: `WarpColor("#1E88E5")` or `WarpColor("#FF1E88E5")`
- **ARGB Int**: `WarpColor(0xFF1E88E5.toInt())`
- **Predefined System Colors**: `WarpColor.White`, `WarpColor.Black`, `WarpColor.Transparent`, `WarpColor.Red`, `WarpColor.Green`, `WarpColor.Blue`, `WarpColor.Gray`
- **Tailwind Palette Tokens**: `WarpColor.Blue500`, `WarpColor.Red600`, `WarpColor.Green600`, etc.
