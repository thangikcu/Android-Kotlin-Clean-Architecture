buildscript {
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.com.diffplug.spotless) apply true
    id("com.github.ben-manes.versions") version "0.42.0"
    id("nl.littlerobots.version-catalog-update") version "0.6.1"

}

versionCatalogUpdate {
    // sort the catalog by key (default is true)
    sortByKey.set(true)
    // Referenced that are pinned are not automatically updated.
    // They are also not automatically kept however (use keep for that).
    pin {
        // pins all libraries and plugins using the given versions
/*        versions.add("my-version-name")
        versions.add("other-version")
        // pins specific libraries that are in the version catalog
        libraries.add(libs.my.library.reference)
        libraries.add(libs.my.other.library.reference)
        // pins specific plugins that are in the version catalog
        plugins.add(libs.plugins.my.plugin)
        plugins.add(libs.plugins.my.other.plugin)
        // pins all libraries (not plugins) for the given groups
        groups.add("com.somegroup")
        groups.add("com.someothergroup")*/
    }
    keep {
        // keep has the same options as pin to keep specific entries

        plugins.add(libs.plugins.com.diffplug.spotless)

/*        versions.add("my-version-name")
        versions.add("other-version")
        libraries.add(libs.my.library.reference)
        libraries.add(libs.my.other.library.reference)
        plugins.add(libs.plugins.my.plugin)
        plugins.add(libs.plugins.my.other.plugin)
        groups.add("com.somegroup")
        groups.add("com.someothergroup")*/

        // keep versions without any library or plugin reference
        keepUnusedVersions.set(true)
        // keep all libraries that aren't used in the project
        keepUnusedLibraries.set(true)
        // keep all plugins that aren't used in the project
        keepUnusedPlugins.set(true)
    }
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

spotless {
    // optional: limit format enforcement to just the files changed by this feature branch
//    ratchetFrom "origin/main"

    format("misc") {
        // define the files to apply `misc` to
        target("**/*.gradle", "**/*.md", "**/.gitignore")

        // define the steps to apply to those files
        indentWithSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }

    format("xml") {
        target("**/*.xml")
    }

    java {
        target("**/*.java")
    }

    kotlin {
        target("**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}