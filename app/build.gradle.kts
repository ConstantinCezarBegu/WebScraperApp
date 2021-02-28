plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.squareup.sqldelight")
    id( "dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.constantin.webscraperapp"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    lint {
        isCheckReleaseBuilds = false
        textReport = true
        htmlReport = false
        xmlReport = false
        isExplainIssues = false
        isAbortOnError = false
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs += listOf(
            "-progressive",
            "-Xinline-classes",
            "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
        )
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin std lib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")

    // Material
    implementation("com.google.android.material:material:1.4.0-alpha01")
    // Appcompat
    implementation("androidx.appcompat:appcompat:1.3.0-beta01")
    // Activity
    implementation("androidx.activity:activity-ktx:1.3.0-alpha03")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.3.0")
    // Core
    implementation("androidx.core:core-ktx:1.5.0-beta02")
    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    // CoordinatorLayout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    // Viewpager2
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.0-beta02")
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Browser
    implementation("androidx.browser:browser:1.3.0")
    // Process
    implementation("androidx.lifecycle:lifecycle-process:2.3.0")
    // Work
    implementation("androidx.work:work-runtime-ktx:2.5.0")
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.3")
    // paging
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")

    // Coil
    implementation("io.coil-kt:coil:0.11.0")

    // Dagger
    implementation("com.google.dagger:dagger:2.31.2")
    kapt( "com.google.dagger:dagger-compiler:2.31.2")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.31.2-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.31.2-alpha")
    implementation("androidx.hilt:hilt-work:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha03")

    // Sqldelight
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.4")
    implementation("com.squareup.sqldelight:android-driver:1.4.4")
    implementation("com.squareup.sqldelight:android-paging-extensions:1.4.4")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.4.4")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("org.jsoup:jsoup:1.13.1")

    // Test implementation
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Allows to detect memory leacks.
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.4")
}