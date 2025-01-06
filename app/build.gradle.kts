plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.ubierzmain_kotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ubierzmain_kotlin"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.biometric)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.koin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coil)

    // додати залежність для osmdroid
    implementation("org.osmdroid:osmdroid-android:6.1.8")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.10")
    implementation("org.osmdroid:osmdroid-wms:6.1.10")
}