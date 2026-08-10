package com.atriidev.warp_widget

actual fun platform() = "Android"

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()