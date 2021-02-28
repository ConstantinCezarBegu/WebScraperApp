apply(plugin = "com.github.ben-manes.versions")

buildscript {
    val kotlinVersion by extra("1.4.30")
    repositories {
        google()
        gradlePluginPortal()
    }

    dependencies {
        // Reading gradle versions
        classpath("com.github.ben-manes:gradle-versions-plugin:0.36.0")
        // Kotlin
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        //classpath(kotlin("serialization", version = kotlinVersion))
        // Android tools
        classpath("com.android.tools.build:gradle:7.0.0-alpha08")
        // Navigation safe args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.3")
        // Sqldelight
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.4")
        // Hilt
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.31.2-alpha")

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}