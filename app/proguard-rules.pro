# Kt AI ProGuard Rules

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.monem.ktai.**$$serializer { *; }
-keepclassmembers class com.monem.ktai.** {
    *** Companion;
}
-keepclasseswithmembers class com.monem.ktai.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Room entities
-keep class com.monem.ktai.data.local.entity.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
