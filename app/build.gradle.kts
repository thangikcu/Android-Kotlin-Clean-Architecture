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

    implementation(Dependencies.coreKtx)
    implementation(Dependencies.appCompat)
    implementation(Dependencies.appCompatResource)
    implementation(Dependencies.startupRuntime)
    implementation(Dependencies.legacySupport)
    implementation(Dependencies.lifecycleLiveData)
    implementation(Dependencies.lifecycleViewModel)
    implementation(Dependencies.coroutineCore)
    implementation(Dependencies.coroutineAndroid)

    implementation(Dependencies.splashScreen)

    implementation(Dependencies.securityCrypto)

    implementation(Dependencies.navigationFragment)
    implementation(Dependencies.navigationUi)

    implementation(Dependencies.glide)

    implementation(Dependencies.pagingRuntime)

    implementation(Dependencies.workRuntime)
    implementation(Dependencies.hiltWork)
    kapt(Dependencies.kaptHiltWork)

    implementation(Dependencies.room)
    implementation(Dependencies.roomPaging)
    implementation(Dependencies.roomRuntime)
    kapt(Dependencies.kaptRoomCompiler)

    implementation(Dependencies.retrofit)
    implementation(Dependencies.moshiConverter)
    implementation(Dependencies.loggingInterceptor)

    implementation(Dependencies.moshi)
    kapt(Dependencies.kaptMoshi)

    implementation(Dependencies.hilt)
    kapt(Dependencies.kaptHilt)


    implementation(Dependencies.material)
    implementation(Dependencies.constraintLayout)

    // For debug
    debugImplementation(Dependencies.leakCanary)
    implementation(Dependencies.logger)

    // For test
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.junitTest)
    androidTestImplementation(Dependencies.espressoCore)

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