import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

val secrets = loadProperties("secret.properties")

android {
    signingConfigs {
        create("common") {
            storeFile = file(secrets.getProperty("STORE_FILE"))
            storePassword = secrets.getProperty("STORE_PASSWORD")
            keyAlias = secrets.getProperty("STORE_KEY_ALIAS")
            keyPassword = secrets.getProperty("STORE_KEY_PASSWORD")
        }
    }
    namespace = "com.development.clean"
    compileSdk = AppConfig.compileSdkVersion

    defaultConfig {
        applicationId = "com.development.clean"
        minSdk = AppConfig.minSdkVersion
        targetSdk = AppConfig.targetSdkVersion
        versionCode = AppConfig.versionCode
        resourceConfigurations.addAll(listOf("en", "vi"))
        buildConfigField("String", "API_KEY", secrets.getProperty("API_KEY"))
        manifestPlaceholders["apiKey"] = secrets.getProperty("API_KEY")
        signingConfig = signingConfigs.getByName("common")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { dataBinding = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    hilt { enableAggregatingTask = true }
    kapt { correctErrorTypes = true }
    base {
        @Suppress("DEPRECATION")
        archivesBaseName = AppConfig.archivesBaseName
    }
    flavorDimensions += "default"
    productFlavors {
        create("dev") {
            versionName = "${AppConfig.versionName}.(${AppConfig.versionTemp})"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "DEV")
            buildConfigField("boolean", "SHAKE_LOG", "true")
            loadEnv(this, "env/dev.properties")
        }

        create("uat") {
            versionName = "${AppConfig.versionName}.(${AppConfig.versionTemp})"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat"
            resValue("string", "app_name", "UAT")
            buildConfigField("boolean", "SHAKE_LOG", "true")
            loadEnv(this, "env/uat.properties")
        }

        create("staging") {
            versionName = "${AppConfig.versionName}.(${AppConfig.versionTemp})"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "Staging")
            buildConfigField("boolean", "SHAKE_LOG", "true")
            loadEnv(this, "env/staging.properties")
        }

        create("product") {
            versionName = AppConfig.versionName
            resValue("string", "app_name", "APP")
            buildConfigField("boolean", "SHAKE_LOG", "false")
            loadEnv(this, "env/product.properties")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            ext.set("enableCrashlytics", false)
            ext.set("alwaysUpdateBuildId", false)
/*            FirebasePerformance {
                instrumentationEnabled= false
            }*/
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.appcompat.appcompat.resources)
    implementation(libs.androidx.startup.startup.runtime)
    implementation(libs.androidx.legacy.legacy.support.v4)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)

    implementation(libs.androidx.core.core.splashscreen)

    implementation(libs.androidx.security.security.crypto)

    implementation(libs.androidx.navigation.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.navigation.ui.ktx)

    implementation(libs.com.github.bumptech.glide)

    implementation(libs.androidx.paging.paging.runtime.ktx)

    implementation(libs.androidx.work.work.runtime.ktx)
    implementation(libs.androidx.hilt.hilt.work)
    kapt(libs.androidx.hilt.hilt.compiler)

    implementation(libs.androidx.room.room.ktx)
    implementation(libs.androidx.room.room.paging)
    implementation(libs.androidx.room.room.runtime)
    kapt(libs.androidx.room.room.compiler)

    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.moshi)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    implementation(libs.com.squareup.moshi.moshi.kotlin)
    kapt(libs.com.squareup.moshi.moshi.kotlin.codegen)

    implementation(libs.com.google.dagger.hilt.android)
    kapt(libs.com.google.dagger.hilt.compiler)


    implementation(libs.com.google.android.material)
    implementation(libs.androidx.constraintlayout)

    // For debug
    debugImplementation(libs.com.squareup.leakcanary.leakcanary.android)
    implementation(libs.com.orhanobut.logger)

    // For test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)

}

fun loadProperties(filename: String): Properties {
    val properties = Properties()

    project.rootProject.file(filename).inputStream().use {
        properties.load(it)
    }

    return properties
}

fun findPropertyByKey(key: String): String {
    return project.findProperty(key) as String
}

fun loadEnv(target: com.android.build.api.dsl.ApplicationProductFlavor, envPath: String) {
    val envProperties = loadProperties(envPath)

    target.buildConfigField(
        "String", "APP_BASE_URL",
        envProperties.getProperty("APP_BASE_URL")
    )

    target.buildConfigField(
        "String", "APP_CLIENT_ID",
        envProperties.getProperty("APP_CLIENT_ID")
    )
}