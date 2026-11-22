plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "dev.namdev.hmi.car"
    compileSdk = 35

    // Compile against the Car API from the SDK; on the device it comes from the system image.
    useLibrary("android.car")

    defaultConfig {
        minSdk = 29 // Android 10, the first AAOS release with a public Car API
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(project(":hmi-components"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.namdevlondhe.aaos-hmi-components"
                artifactId = "hmi-car"
                version = System.getenv("VERSION") ?: "0.1.0"
            }
        }
    }
}
