plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication2"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.myapplication2"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7" // Ensure this matches your Compose version
    }
}

dependencies {
    implementation("com.google.firebase:firebase-analytics-ktx:20.1.0") // Example version
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1") // Example version
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.21")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity:1.8.2") // For Material Design 3
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
}

// Apply the Google Services plugin
apply(plugin = "com.google.gms.google-services")

