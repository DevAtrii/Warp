# Proguard / R8 rules for warp-runtime

# Keep all warp-runtime classes, interfaces, enums, serializable models, and companion objects
-keep class com.atriidev.warp_runtime.** { *; }
-keepclassmembers class com.atriidev.warp_runtime.** { *; }

# Keep kotlinx.serialization classes and companion serializers
-if @kotlinx.serialization.Serializable class **
-keep class <1> {
    *;
}

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
