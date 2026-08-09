# Proguard / R8 consumer rules for warp-widget

# Keep all warp-widget classes, receivers, actions, and models
-keep class com.atriidev.warp_widget.** { *; }
-keepclassmembers class com.atriidev.warp_widget.** { *; }

# Glance AppWidget & Action Callbacks
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }
-keep class * implements androidx.glance.appwidget.action.ActionCallback { *; }

# AndroidX Room Database (Required for Glance & WorkManager reflective instantiation of WorkDatabase_Impl)
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class androidx.work.impl.WorkDatabase_Impl {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase$Callback {
    <init>(...);
}
-dontwarn androidx.room.paging.**

# AndroidX WorkManager (Required for Glance Widget background updates)
-keep class * extends androidx.work.ListenableWorker {
    <init>(...);
}
-keep class * extends androidx.work.Worker {
    <init>(...);
}
-keep class androidx.work.impl.WorkDatabase { *; }
-keep class androidx.work.impl.WorkManagerImpl { *; }
-keep class androidx.work.impl.background.systemalarm.RescheduleReceiver { *; }
-keep class androidx.work.impl.background.systemjob.SystemJobService { *; }
-keep class androidx.work.impl.diagnostics.DiagnosticsReceiver { *; }

# AndroidX WorkManager InputMerger (Fix for: java.lang.InstantiationException: Class<androidx.work.OverwritingInputMerger> has no zero argument constructor)
-keep class * extends androidx.work.InputMerger {
    <init>();
}
-keep class androidx.work.OverwritingInputMerger {
    <init>();
}
-keep class androidx.work.ArrayCreatingInputMerger {
    <init>();
}

# AndroidX App Startup (Required for WorkManagerInitializer & InitializationProvider)
-keep class * implements androidx.startup.Initializer {
    <init>();
}
-keep class androidx.work.WorkManagerInitializer { *; }
-keep class androidx.startup.InitializationProvider { *; }
