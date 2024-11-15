plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.androidlead.loginappui"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidlead.loginappui"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX dependencies
    implementation(libs.bundles.androidX)

    // Compose dependencies
    implementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.tooling)
    implementation(libs.bundles.ui)

    // Material Icons Extended for Compose
    implementation("androidx.compose.material:material-icons-extended:1.4.0") // Add this line

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.2.2") // For Jetpack Compose

    // If you need Coil for traditional views, you can add:
    // implementation("io.coil-kt:coil:2.2.2")
}
