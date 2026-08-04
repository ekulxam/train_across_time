plugins {
    id("java")
}

group = "net.typho.wathe_port"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation("net.fabricmc:tiny-remapper:0.14.0")
    implementation("net.fabricmc:mapping-io:0.8.0")
    implementation("org.ow2.asm:asm:9.10.1")
    implementation("org.ow2.asm:asm-tree:9.10.1")
    implementation("org.ow2.asm:asm-util:9.10.1")
    implementation("org.jetbrains:annotations:26.0.2")
}