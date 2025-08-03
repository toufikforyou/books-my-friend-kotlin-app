plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sopnolikhi.booksmyfriend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sopnolikhi.booksmyfriend"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2023.03.23.1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            /**
             * Publish app then uncomment the code
             * isMinifyEnabled = true
             * isShrinkResources = true
             * isDebuggable = false
             */
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        viewBinding = true
    }
}

dependencies {
    /** TODO: Google Play Service */
    implementation("com.google.android.gms:play-services-location:21.1.0")

    /** Image load Dependency */
    implementation("com.github.bumptech.glide:glide:4.16.0")

    /** TODO: lottie Animations */
    implementation ("com.airbnb.android:lottie:6.3.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}