# ============================================================
# Preservar informações de linha para stack traces de produção
# ============================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================================
# Kotlin
# ============================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlin.Metadata { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================================
# Retrofit + OkHttp
# ============================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================================
# Gson — manter todos os DTOs e modelos usados na serialização
# ============================================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# DTOs de rede (Retrofit + Gson usa reflexão nessas classes)
-keep class br.com.mauricio.oconcurseiro.data.remote.** { *; }

# Modelos de domínio
-keep class br.com.mauricio.oconcurseiro.data.model.** { *; }

# ============================================================
# Room — entidades e DAOs
# ============================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers @androidx.room.Entity class * { *; }
-dontwarn androidx.room.**

# Entidade local
-keep class br.com.mauricio.oconcurseiro.data.local.RespostaEntity { *; }
-keep class br.com.mauricio.oconcurseiro.data.local.RespostaDao { *; }

# ============================================================
# Hilt — injeção de dependência (AAR já inclui regras, mas reforçamos)
# ============================================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}
-dontwarn dagger.**

# ============================================================
# Firebase — Auth e Analytics (AARs já têm regras, reforço de segurança)
# ============================================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ============================================================
# AndroidX Credentials (Google Sign-In)
# ============================================================
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**

# ============================================================
# AndroidX Security — EncryptedSharedPreferences
# ============================================================
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# ============================================================
# Jetpack Compose — geralmente seguro, mas preservar lambdas
# ============================================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
