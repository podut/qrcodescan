pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ProScan"

include(":app")
include(":core")
include(":core-ui")

// Scanner feature
include(":feature:scanner:scanner_domain")
include(":feature:scanner:scanner_data")
include(":feature:scanner:scanner_presentation")

// Generator feature
include(":feature:generator:generator_domain")
include(":feature:generator:generator_presentation")

// History feature
include(":feature:history:history_domain")
include(":feature:history:history_data")
include(":feature:history:history_presentation")

// Settings feature
include(":feature:settings:settings_domain")
include(":feature:settings:settings_data")
include(":feature:settings:settings_presentation")

// Result feature
include(":feature:result:result_domain")
include(":feature:result:result_presentation")
