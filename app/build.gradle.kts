plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")  // Плагин Google Services
}

android {
    namespace = "com.example.aptofam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aptofam"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.tbuonomo:dotsindicator:5.0")

    implementation("com.google.firebase:firebase-storage:20.1.0")

    implementation("com.google.firebase:firebase-database:20.0.5")

    implementation("com.google.firebase:firebase-auth:21.0.1")

    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    implementation ("com.airbnb.android:lottie:6.3.0")

    implementation ("com.google.android.gms:play-services-maps:19.0.0")

    implementation ("androidx.browser:browser:1.8.0")

    implementation ("com.google.android.gms:play-services-safetynet:18.0.1")

    implementation ("com.google.firebase:firebase-messaging:24.1.0")

    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.work:work-runtime:2.8.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
