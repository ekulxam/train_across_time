plugins {
    id("java")
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.typho.wathe_port"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation("org.ow2.asm:asm:9.10.1")
    implementation(project(":common")) // TODO JiJ
}

gradlePlugin {
    plugins {
        create("train_across_time_plugin") {
            id = "survivalblock.train_across_time.plugin"
            implementationClass = "survivalblock.train_across_time.plugin.TATPlugin"
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    from("../src/main/resources/mappings.bin")
}