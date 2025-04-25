plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        mlModelBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Ensure Google services is added
    }
}

dependencies {
    implementation("com.google.android.material:material:1.12.0") // Use the latest available version

    // Firebase BoM to handle consistent versions for Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.5.1")) // Update to latest Firebase BOM version
    implementation("com.firebaseui:firebase-ui-auth:8.0.0") // or latest version
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // Firebase dependencies (BoM will handle versioning)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0" )

    // Google Play Services dependencies
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-places:17.1.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")

    // TensorFlow dependencies
    implementation("org.tensorflow:tensorflow-lite-support:0.4.1")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.3.0")
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.12.0")
    implementation(libs.tensorflow.lite.gpu)

    // AndroidX dependencies
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.multidex:multidex:2.0.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.7.1")
    implementation("androidx.compose.material:material:1.7.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.1")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Charting library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.com.google.firebase.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation ("com.airbnb.android:lottie:6.1.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.7.5")
}
