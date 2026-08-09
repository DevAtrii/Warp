-keepattributes *Annotation*

-keep class com.atriidev.warp_runtime.**$$serializer { * }
-keep class com.atriidev.warp_runtime.** {
    @kotlinx.serialization.Serializable <fields>;
}