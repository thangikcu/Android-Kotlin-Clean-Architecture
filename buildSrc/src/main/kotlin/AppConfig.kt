@file:Suppress("MemberVisibilityCanBePrivate")

object AppConfig {
    const val versionMajor = 0
    const val versionMinor = 0
    const val versionPatch = 0
    const val versionTemp = 1

    const val compileSdkVersion = 32
    const val minSdkVersion = 23
    const val targetSdkVersion = 32

    const val versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
    const val versionCode =
        versionMajor * 1000 + versionMinor * 100 + versionPatch * 10 + versionTemp

    const val archivesBaseName = "${versionMajor}.${versionMinor}.${versionPatch}.(${versionTemp})"
}