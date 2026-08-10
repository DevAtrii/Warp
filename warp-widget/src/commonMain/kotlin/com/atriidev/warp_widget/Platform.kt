package com.atriidev.warp_widget

/** Debug label for the current target (`"android"` / `"ios"`). Prefer [com.atriidev.warp_widget.api.currentWidgetPlatform]. */
expect fun platform(): String

internal expect fun currentTimeMillis(): Long