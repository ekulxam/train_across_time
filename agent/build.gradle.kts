plugins {
    id("java")
}

group = "net.typho.wathe_port"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.jar {
    archiveVersion.set("")
    destinationDirectory.set(rootProject.file("src/main/resources"))

    manifest {
        attributes(
            "Main-Class" to "net.typho.wathe_port.agent.WathePortAgent",
            "Agent-Class" to "net.typho.wathe_port.agent.WathePortAgent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}