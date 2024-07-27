pluginManagement {
    repositories {
        google() {
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

// TODO: 移除
plugins {
    id("com.highcapable.sweetproperty") version "1.0.5"
}

sweetProperty {
    isEnable = true
    global {
        all {
            isEnableTypeAutoConversion = true
            propertiesFileNames(
                "keystore.properties",
                "application.properties",
                isAddDefault = true
            )
            permanentKeyValues(
                "keystore.file" to "",
                "keystore.password" to "",
                "keystore.key.alias" to "",
                "keystore.key.password" to "",
            )
            generateFrom(CURRENT_PROJECT, ROOT_PROJECT)
        }
        buildScript {
            extensionName = "property"
        }
    }
}

rootProject.name = "TiebaLite"
include(":app")
