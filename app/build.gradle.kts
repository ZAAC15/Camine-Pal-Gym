plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "com.caminepalgym"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.caminepalgym"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.material:material:1.13.0")

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.google.id)
    implementation(libs.credential.manager)
    implementation(libs.credential.manager.play)
    implementation(libs.biometric)
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation(libs.coil)
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // FusedLocationProvider (para obtener ubicación actual)
    implementation("com.google.android.gms:play-services-location:21.3.0")
}