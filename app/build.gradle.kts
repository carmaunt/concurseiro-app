import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun releaseSetting(name: String): String? {
    return localProperties.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: System.getenv(name)?.takeIf { it.isNotBlank() }
}

val googleSampleAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
val googleSampleBannerAdUnitId = "ca-app-pub-3940256099942544/9214589741"
val googleSampleInterstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712"

val releaseAdMobAppId = releaseSetting("ADMOB_APP_ID")
val releaseBannerAdUnitId = releaseSetting("ADMOB_BANNER_AD_UNIT_ID")
val releaseInterstitialAdUnitId = releaseSetting("ADMOB_INTERSTITIAL_AD_UNIT_ID")

val adMobAppIdPattern = Regex("""ca-app-pub-\d+~\d+""")
val adMobUnitIdPattern = Regex("""ca-app-pub-\d+/\d+""")

releaseAdMobAppId?.let {
    require(adMobAppIdPattern.matches(it)) {
        "ADMOB_APP_ID inválido. Use o formato ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY."
    }
}
listOf(
    "ADMOB_BANNER_AD_UNIT_ID" to releaseBannerAdUnitId,
    "ADMOB_INTERSTITIAL_AD_UNIT_ID" to releaseInterstitialAdUnitId
).forEach { (name, value) ->
    value?.let {
        require(adMobUnitIdPattern.matches(it)) {
            "$name inválido. Use o formato ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ."
        }
    }
}
require(
    releaseAdMobAppId != null ||
        (releaseBannerAdUnitId == null && releaseInterstitialAdUnitId == null)
) {
    "Configure ADMOB_APP_ID antes de informar unidades de anúncio de produção."
}

android {
    namespace = "br.com.mauricio.oconcurseiro"
    compileSdk = 37

    defaultConfig {
        applicationId = "br.com.mauricio.oconcurseiro"
        minSdk = 24
        targetSdk = 36
        // Incrementar sempre que enviar uma nova versão para a Play Store.
        versionCode = 20

        // Versão visível para o usuário na loja e nas configurações do app.
        versionName = "2.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val releaseStoreFile = releaseSetting("RELEASE_STORE_FILE")
            val releaseStorePassword = releaseSetting("RELEASE_STORE_PASSWORD")
            val releaseKeyAlias = releaseSetting("RELEASE_KEY_ALIAS")
            val releaseKeyPassword = releaseSetting("RELEASE_KEY_PASSWORD")

            if (
                !releaseStoreFile.isNullOrBlank() &&
                !releaseStorePassword.isNullOrBlank() &&
                !releaseKeyAlias.isNullOrBlank() &&
                !releaseKeyPassword.isNullOrBlank()
            ) {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://concurseiro-api-lnae.onrender.com/\"")
            buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"$googleSampleBannerAdUnitId\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID", "\"$googleSampleInterstitialAdUnitId\"")
            manifestPlaceholders["admobAppId"] = googleSampleAdMobAppId
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://concurseiro-api-lnae.onrender.com/\"")
            buildConfigField(
                "String",
                "ADMOB_BANNER_AD_UNIT_ID",
                "\"${releaseBannerAdUnitId.orEmpty()}\""
            )
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"${releaseInterstitialAdUnitId.orEmpty()}\""
            )
            // O ID de amostra evita crash de inicialização. Sem unidades reais,
            // o AdsManager mantém anúncios desativados no release.
            manifestPlaceholders["admobAppId"] = releaseAdMobAppId ?: googleSampleAdMobAppId
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkReleaseBuilds = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.install.referrer)
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":feature-auth"))
    implementation(project(":feature-questoes"))
    implementation(project(":feature-comentarios"))
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.google.mobile.ads)
    implementation(libs.google.user.messaging.platform)
}
