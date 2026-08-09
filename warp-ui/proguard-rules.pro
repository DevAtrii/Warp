# Proguard / R8 rules for warp-ui

# Keep all warp-ui classes, components, and renderers
-keep class com.atriidev.warp_ui.** { *; }
-keepclassmembers class com.atriidev.warp_ui.** { *; }

# Glance AppWidget Rendering & Composables
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }
-keep class * implements androidx.glance.appwidget.action.ActionCallback { *; }
