buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2") // Ensure you have the latest compatible version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21") // Use the correct version of Kotlin
        classpath("com.google.gms:google-services:4.3.14")

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

}

