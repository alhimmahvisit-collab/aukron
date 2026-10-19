import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val ksFile = rootProject.file("keystore.properties")
val ksProps = Properties().apply { if (ksFile.exists()) ksFile.inputStream().use { load(it) } }

android {
    namespace = "com.pkj.kimpul"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.pkj.kimpul"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("String", "WEB_URL", "\"https://eduspacedashboardsekolah.vercel.app/\"")
    }
    buildFeatures { buildConfig = true }
    signingConfigs {
        if (ksFile.exists()) create("release") {
            storeFile = rootProject.file(ksProps["storeFile"] as String)
            storePassword = ksProps["storePassword"] as String
            keyAlias = ksProps["keyAlias"] as String
            keyPassword = ksProps["keyPassword"] as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (ksFile.exists()) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
        }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
