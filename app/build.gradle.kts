plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sopnolikhi.booksmyfriend"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.sopnolikhi.booksmyfriend"
        minSdk = 24
        targetSdk = 36
        // Releases are numbered YEAR.MONTH.<appVersionBase>.<build> (e.g. 2026.10.1.0.3).
        // The release workflow passes the real values; local builds use the dev defaults below.
        versionCode = (findProperty("bmf.versionCode") as String?)?.toInt() ?: 1
        versionName = (findProperty("bmf.versionName") as String?)
            ?: "${findProperty("bmf.versionBase")}-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    /**
     * Release signing comes from environment variables (set by the release workflow from
     * repository secrets), so no keystore or password is ever committed.
     */
    val releaseKeystore = System.getenv("BMF_KEYSTORE_FILE")?.let { file(it) }?.takeIf { it.exists() }
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("BMF_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("BMF_KEY_ALIAS")
                keyPassword = System.getenv("BMF_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (releaseKeystore != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    /** TODO: lottie Animations */
    implementation(libs.lottie)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
