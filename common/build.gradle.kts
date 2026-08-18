plugins {
    id("java")
}

group = "net.typho.wathe_port"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.fabricmc.net")
    ivy("https://github.com/TheTypholorian/asm_util/releases/download") {
        patternLayout {
            artifact("[revision]/[artifact]-[revision](-[classifier]).[ext]")
        }

        metadataSources {
            artifact()
        }
    }
}

dependencies {
    implementation("net.fabricmc:tiny-remapper:0.14.0")
    implementation("net.fabricmc:mapping-io:0.8.0")
    implementation("org.ow2.asm:asm:9.10.1")
    implementation("org.ow2.asm:asm-tree:9.10.1")
    implementation("org.ow2.asm:asm-util:9.10.1")
    implementation("org.ow2.asm:asm-commons:9.10.1")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("net.typho:asm_util:1.0.19")
}