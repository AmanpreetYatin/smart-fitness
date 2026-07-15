import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Required for kapt configuration to work
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.service)
}

// Single source of truth — read local.properties once at top level
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

android {
    namespace = "com.smartfitness.app"
    compileSdk {
        version = release(36)
    }


    defaultConfig {
        applicationId = "com.smartfitness.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read GEMINI_API_KEY from local.properties → exposes as BuildConfig.GEMINI_API_KEY
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )

        // Google Maps API Key
        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"${localProperties.getProperty("MAPS_API_KEY", "")}\""
        )
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY", "")
        manifestPlaceholders["FACEBOOK_APP_ID"] = localProperties.getProperty("FACEBOOK_APP_ID", "")
        manifestPlaceholders["FACEBOOK_CLIENT_TOKEN"] = localProperties.getProperty("FACEBOOK_CLIENT_TOKEN", "")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx.v2321) // Use -ktx for Kotlin
    implementation(libs.androidx.compose.material.icons.extended)
    implementation("com.google.firebase:firebase-database")
    implementation(libs.google.firebase.firestore.ktx)
    implementation("com.google.android.gms:play-services-location:21.2.0")

    implementation(libs.facebook.login) // Facebook Login SDK
    //lottie for animation
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    //markdown project
    implementation("com.github.jeziellago:compose-markdown:0.7.2")

    // For Google Sign-In
    implementation(libs.play.services.auth)
    // For api calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    //For AI model Gemini
    implementation(libs.generativeai)
    //for image loading
    implementation(libs.coil.compose)

    //Google Fit or health Connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha06")


    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

     implementation(libs.play.services.auth)
     
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation.core.lint)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}