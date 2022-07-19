pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    plugins {
        // See https://jmfayard.github.io/refreshVersions
        id("de.fayard.refreshVersions") version "0.40.2"
    }
}
plugins {
    id("de.fayard.refreshVersions")
}
refreshVersions {
    enableBuildSrcLibs()
    rejectVersionIf {
        (candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
            // || candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
/*            || versionKey in Dependencies.blackListVersionKey
            || moduleId.group in Dependencies.blackListGroup*/
            )
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Android Kotlin Clean Architecture"
include(":app")
