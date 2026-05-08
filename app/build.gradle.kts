plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")

    // Compose compiler plugin (version must match your Kotlin plugin)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"

    // Hilt
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.mygame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mygame"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // If you plan to run Hilt instrumented tests, you can switch to HiltTestRunner:
        // testInstrumentationRunner = "com.google.dagger.hilt.android.testing.HiltTestRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }

    // Compose
    buildFeatures { compose = true }

    // ✅ Kotlin DSL path to enable Robolectric resources
    testOptions {
        unitTests.isIncludeAndroidResources = true // Robolectric
    }
}

dependencies {
    // --- AndroidX / material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- Navigation / JUnit ktx / monitor (from your version catalog)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.monitor)

    // --- Room (don’t mix annotationProcessor in Kotlin projects)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")

    // --- Hilt
//    implementation("com.google.dagger:hilt-android:2.52")
//    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")
    // --- Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // --- Compose (use BOM to align)
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")

    // Other
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // --- Unit tests (JVM / Robolectric)
    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.robolectric:robolectric:4.12.2")
    testImplementation("androidx.test:core:1.6.1")

    // Hilt for unit tests (match versions with main)
//    testImplementation("com.google.dagger:hilt-android-testing:2.52")
//    kaptTest("com.google.dagger:hilt-compiler:2.52")
    testImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kaptTest("com.google.dagger:hilt-compiler:2.57.2")

    // --- Instrumented tests (device/emulator)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("com.google.truth:truth:1.4.4")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // Hilt for instrumented tests
//    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
//    kaptAndroidTest("com.google.dagger:hilt-compiler:2.52")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.57.2")
}
