buildscript {
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.androidGradle apply false
    id("com.android.library") version Versions.androidGradle apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
    id("com.google.dagger.hilt.android") version Versions.hilt apply false
    id("androidx.navigation.safeargs") version Versions.navigation apply false
    id("com.diffplug.spotless") version Versions.spotless apply true

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