enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "train_across_time"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
    create(rootProject) {
        fun match(loader: String, platform: String, vararg versions: String) = versions
            .forEach {
                val versionId = "mc${it.replace('.', '_')}_$loader"
                val propsFile = file("versions/$versionId/gradle.properties")

                if (!propsFile.exists()) {
                    propsFile.parentFile.mkdirs()
                    propsFile.writeText("modstitch.platform=$platform")
                }

                version(versionId, it).buildscript = "build.gradle.kts"
            }

        match("fabric", "fabric-loom-remap", "26.1.2")
        //match("fabric", "fabric-loom", "26.2")

        vcsVersion = "mc26_1_2_fabric"
    }
}

include("agent")
include("common")
includeBuild("plugin")