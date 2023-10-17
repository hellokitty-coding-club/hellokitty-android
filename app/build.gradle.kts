import org.jetbrains.kotlin.konan.properties.Properties

val lgtmPropertiesFile = rootProject.file("lgtm.properties")
val lgtmProperties = Properties()
lgtmProperties.load(lgtmPropertiesFile.inputStream())

plugins {
    kotlin("android")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
}

android {
    namespace = "com.lgtm.android"

    defaultConfig {
        applicationId = "com.lgtm.android"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.appVersion.get()
        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "String",
            "LGTM_BASE_URL_DEBUG",
            lgtmProperties.getProperty("LGTM_BASE_URL_DEBUG")
        )
        buildConfigField(
            "String",
            "LGTM_BASE_URL_RELEASE",
            lgtmProperties.getProperty("LGTM_BASE_URL_RELEASE")
        )
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        disable += "Instantiatable"
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:main"))
    implementation(project(":feature:create_mission"))
    implementation(project(":feature:mission_detail"))
    implementation(project(":feature:manage_mission"))
    implementation(project(":feature:profile"))
    implementation(project(":common-ui"))

    implementation(libs.hilt)
    implementation(platform(libs.firebase))
    implementation(libs.bundles.firebase)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.security.cripto)

}
