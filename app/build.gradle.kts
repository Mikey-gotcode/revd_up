plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.revd_up"
    compileSdk = 36 // If this causes issues, change to 34, but 36 should be fine.

    defaultConfig {
        applicationId = "com.example.revd_up"
        minSdk = 26
        targetSdk = 36 // Or 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // This version MUST align with your Compose BOM. 2024.09.00 uses 1.5.14.
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        // Fix for multiple files with the same path, common with Ktor
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- BOMs (Bill of Materials) ---
    // BOMs ensure all the libraries below use compatible versions.
    // Using the version from your project's context.
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("com.google.firebase:firebase-analytics")
    // --- Core & UI ---
    implementation("androidx.core:core-ktx:1.13.1") // Update to a recent version
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3") // Update to a recent version
    implementation("androidx.activity:activity-compose:1.9.1") // Update to a recent version
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // --- Compose ---
    // NO versions needed here; they are managed by the compose-bom
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui-text")

    // --- Navigation ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- Google & Firebase ---
    // NO version needed here; it's managed by the firebase-bom
    implementation("com.google.android.gms:play-services-auth")

    // --- Ktor for Networking ---
    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // --- Image Loading ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Other ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
