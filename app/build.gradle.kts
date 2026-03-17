plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.proscan.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.proscan.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "2.2.0"

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
        compose = true
    }
}

dependencies {
    // Core modules
    implementation(project(":core"))
    implementation(project(":core-ui"))

    // Feature modules
    implementation(project(":feature:scanner:scanner_presentation"))
    implementation(project(":feature:scanner:scanner_domain"))
    implementation(project(":feature:scanner:scanner_data"))
    implementation(project(":feature:generator:generator_presentation"))
    implementation(project(":feature:generator:generator_domain"))
    implementation(project(":feature:history:history_presentation"))
    implementation(project(":feature:history:history_domain"))
    implementation(project(":feature:history:history_data"))
    implementation(project(":feature:settings:settings_presentation"))
    implementation(project(":feature:settings:settings_domain"))
    implementation(project(":feature:settings:settings_data"))
    implementation(project(":feature:result:result_presentation"))
    implementation(project(":feature:result:result_domain"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
