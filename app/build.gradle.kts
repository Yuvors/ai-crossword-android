plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.yuval.tashchetz"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yuval.tashchetz"
        minSdk = 28          // Android 9
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    // MVVM: ViewModel and LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Unit tests that run on the computer, without an emulator
    testImplementation(libs.junit)
}
