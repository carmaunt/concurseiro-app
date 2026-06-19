plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val debugApiBaseUrl = providers.gradleProperty("CONCURSEIRO_BASE_URL")
    .orElse("https://concurseiro-api-lnae.onrender.com/")

android {
    namespace = "br.com.mauricio.oconcurseiro.data"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"${debugApiBaseUrl.get()}\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://concurseiro-api-lnae.onrender.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    api(project(":domain"))
    implementation(project(":core"))

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.security.crypto)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
