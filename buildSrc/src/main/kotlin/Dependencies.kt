@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate")

object Dependencies {

    val blackListVersionKey = listOf("version.org.jacoco")
    val blackListGroup = listOf("app.cash.turbine")

    val splashScreen by lazy { "androidx.core:core-splashscreen:${Versions.splashScreen}" }

    val securityCrypto by lazy { "androidx.security:security-crypto:${Versions.securityCrypto}" }

    val navigationFragment by lazy { "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}" }
    val navigationUi by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigation}" }

    // Image loader
    val glide by lazy { "com.github.bumptech.glide:glide:${Versions.glide}" }

    val prefDataStore by lazy { "androidx.datastore:datastore-preferences:${Versions.dataStore}" }

    val pagingRuntime by lazy { "androidx.paging:paging-runtime-ktx:${Versions.paging}" }

    val workRuntime by lazy { "androidx.work:work-runtime-ktx:${Versions.work}" }
    val hiltWork by lazy { "androidx.hilt:hilt-work:${Versions.hiltWork}" }
    val kaptHiltWork by lazy { "androidx.hilt:hilt-compiler:${Versions.hiltWork}" }

    // Room
    val room by lazy { "androidx.room:room-ktx:${Versions.room}" }
    val roomPaging by lazy { "androidx.room:room-paging:${Versions.room}" }
    val roomRuntime by lazy { "androidx.room:room-runtime:${Versions.room}" }
    val kaptRoomCompiler by lazy { "androidx.room:room-compiler:${Versions.room}" }

    // Networking
    val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}" }
    val moshiConverter by lazy { "com.squareup.retrofit2:converter-moshi:${Versions.moshiConverter}" }
    val loggingInterceptor by lazy { "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}" }

    val moshi by lazy { "com.squareup.moshi:moshi-kotlin:${Versions.moshi}" }
    val kaptMoshi by lazy { "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}" }

    val hilt by lazy { "com.google.dagger:hilt-android:${Versions.hilt}" }
    val kaptHilt by lazy { "com.google.dagger:hilt-compiler:${Versions.hilt}" }

    val startupRuntime by lazy { "androidx.startup:startup-runtime:${Versions.startup}" }

    val legacySupport by lazy { "androidx.legacy:legacy-support-v4:${Versions.legacy}" }

    val lifecycleLiveData by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}" }
    val lifecycleViewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" }

    val coroutineCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}" }
    val coroutineAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutine}" }

    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val appCompatResource by lazy { "androidx.appcompat:appcompat-resources:${Versions.appCompat}" }

    val material by lazy { "com.google.android.material:material:${Versions.material}" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }

    // For debug
    val leakCanary by lazy { "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}" }
    val logger by lazy { "com.orhanobut:logger:${Versions.logger}" }

    // Compose
    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
    val composeMaterial by lazy { "androidx.compose.material:material:${Versions.compose}" }
    val composeAnimation by lazy { "androidx.compose.animation:animation:${Versions.compose}" }
    val composePreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
    val composeActivity by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val composeViewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycleViewModelCompose}" }
    val composeNavigation by lazy { "androidx.navigation:navigation-compose:${Versions.composeNavigation}" }
    val hiltNavigationCompose by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}" }
    val composeMaterialIconsCore by lazy { "androidx.compose.material:material-icons-core:${Versions.compose}" }
    val composeMaterialIconsExtended by lazy { "androidx.compose.material:material-icons-extended:${Versions.compose}" }
    val composeFoundation by lazy { "androidx.compose.foundation:foundation:${Versions.compose}" }
    val composeFoundationLayout by lazy { "androidx.compose.foundation:foundation-layout:${Versions.compose}" }
    val composeConstraintLayout by lazy { "androidx.constraintlayout:constraintlayout-compose:${Versions.composeConstraintLayout}" }
    val composeMaterial3 by lazy { "androidx.compose.material3:material3:${Versions.composeMaterial3}" }
    val composeCoil by lazy { "io.coil-kt:coil-compose:${Versions.coil}" }

    val kotlinJsonSerialization by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}" }
    val ktorCore by lazy { "io.ktor:ktor-client-core:${Versions.ktor}" }
    val ktorAndroidEngine by lazy { "io.ktor:ktor-client-android:${Versions.ktor}" }
    val ktorOkHttpEngine by lazy { "io.ktor:ktor-client-okhttp:${Versions.ktor}" }
    val ktorSerialization by lazy { "io.ktor:ktor-client-serialization:${Versions.ktor}" }
    val ktorLogging by lazy { "io.ktor:ktor-client-logging:${Versions.ktor}" }

    // Testing
    val junit by lazy { "junit:junit:${Versions.junit}" }
    val junitTest by lazy { "androidx.test.ext:junit:${Versions.junitTest}" }
    val espressoCore by lazy { "androidx.test.espresso:espresso-core:${Versions.espressoTest}" }
    val testCoreKtx by lazy { "androidx.test:core-ktx:${Versions.testCore}" }
    val testArchCore by lazy { "androidx.arch.core:core-testing:${Versions.testArchCore}" }
    val testExtJUnitKtx by lazy { "androidx.test.ext:junit-ktx:${Versions.testExtJUnit}" }
    val mockitoInline by lazy { "org.mockito:mockito-inline:${Versions.mockito}" }
    val mockitoAndroid by lazy { "org.mockito:mockito-android:${Versions.mockito}" }
    val mockitoKotlin by lazy { "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}" }
    val robolectric by lazy { "org.robolectric:robolectric:${Versions.robolectric}" }
    val turbine by lazy { "app.cash.turbine:turbine:${Versions.turbine}" }
    val coroutineTest by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutineTest}" }
    val hiltTest by lazy { "com.google.dagger:hilt-android-testing:${Versions.hilt}" }

    // Compose Testing
    val junitCompose by lazy { "androidx.compose.ui:ui-test-junit4:${Versions.compose}" }
    val composeTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.compose}" }
}