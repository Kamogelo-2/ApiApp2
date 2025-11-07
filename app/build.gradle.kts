plugins {
    // 1. Android Application plugin
    alias(libs.plugins.android.application)

    // 2. Kotlin Android plugin (you had this twice)
    alias(libs.plugins.kotlin.android)

    // 3. Google Services plugin (you also had this twice)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.betabudget"
    compileSdk {
        version = release(36)

    }

    defaultConfig {
        applicationId = "com.example.betabudget"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            getByName("release") {
                isMinifyEnabled = true // Set this to true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}



dependencies {
    // --- ANDROIDX CORE ---
    // (Using the 'libs' aliases from your version catalog)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3)
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // --- ROOM DATABASE ---
    // (Added this back from our previous steps)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // Make sure you have the 'kapt' or 'ksp' plugin enabled for the compiler

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- FIREBASE (Using Bill of Materials) ---
    // The BoM (Bill of Materials) manages all Firebase library versions.
    // You only need to declare it ONCE.
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))

    // Add the Firebase products you need WITHOUT versions
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // --- NETWORKING (Retrofit & OkHttp) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // --- IMAGE LOADING ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
}