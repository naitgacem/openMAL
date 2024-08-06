plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.aitgacem.openmalnet"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    apollo {
        service("service") {
            packageName.set("com.aitgacem.openmalnet.models")
            introspection {
                endpointUrl.set("https://graphql.anilist.co/graphql")
                schemaFile.set(file("src/main/graphql/schema.graphqls"))
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.gson.retrofit)
    implementation(libs.http.logging.interceptor)
    implementation(libs.hilt.android)
    implementation(libs.apollo.runtime)
    implementation(project(":domain"))
    ksp (libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}