pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    }
}

rootProject.name = "BorderlessMC"

include(":versions:mc26_1")
include(":versions:mc26_1_1")
include(":versions:mc26_1_2")
include(":versions:mc26_2")
include(":versions:mc26_3")

project(":versions:mc26_1").projectDir = file("versions/mc26.1")
project(":versions:mc26_1_1").projectDir = file("versions/mc26.1.1")
project(":versions:mc26_1_2").projectDir = file("versions/mc26.1.2")
project(":versions:mc26_2").projectDir = file("versions/mc26.2")
project(":versions:mc26_3").projectDir = file("versions/mc26.3")
