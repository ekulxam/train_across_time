plugins {
    id("java")

    id("me.modmuss50.mod-publish-plugin") version "2.0.0-beta.1"
    id("io.github.klahap.dotenv") version "1.1.3"

    id("dev.isxander.modstitch.base") version "0.8.5"

    id("survivalblock.train_across_time.plugin") version "1.0.0"
}

modstitch {
    modLoaderVersion = "0.19.3"
    minecraftVersion = "26.1.2" // TODO

    javaVersion.set(25)

    metadata {
        modId = project.property("id") as String
        modName = project.property("displayName") as String
        modVersion = project.property("version") as String
        modGroup = project.property("group") as String

        findProperty("authors")?.let { modAuthor = it as String }
        findProperty("description")?.let { modDescription = it as String }
        findProperty("license")?.let { modLicense = it as String }
        findProperty("credits")?.let { modCredits = it as String }

        replacementProperties.put("id", project.property("id") as String)
        replacementProperties.put("version", project.property("version") as String)
        replacementProperties.put("displayName", project.property("displayName") as String)
        replacementProperties.put("description", project.property("description") as String)
        replacementProperties.put("authors", project.property("authors") as String)
        replacementProperties.put("contributors", project.property("contributors") as String)
        replacementProperties.put("license", project.property("license") as String)
        replacementProperties.put("group", project.group as String)
        replacementProperties.put("loader", "fabric")
        replacementProperties.put("java_version", javaVersion.toString())
    }

    loom {
        configureLoom {
            runs.configureEach {
                vmArg("-Dfabric.debug.disableClassPathIsolation=true")
                vmArg("-Dtrain_across_time.mappings_output_file=${rootProject.file("src/main/resources/mappings.bin").absolutePath}")
                vmArg("-Dtrain_across_time.debug_output_path=${project.file(runDir).resolve(".train_debug").absolutePath}")
                vmArg("-javaagent:${finalJarTask.get().archiveFile.get().asFile.absolutePath}")
                ideConfigGenerated(true)
            }
            enableTransitiveAccessWideners = false
        }
    }

    finalJarTask {
        manifest {
            attributes(
                "Premain-Class" to "survivalblock.train_across_time.agent.TATAgent",
                "Can-Redefine-Classes" to "true",
                "Can-Retransform-Classes" to "true"
            )
        }
    }

    mixin {
        val modId = metadata.modId.get()
        //configs.register(modId)
        addMixinsToModManifest = true
    }

    classTweaker.set(sc.process(
        rootProject.file("src/main/resources/train_across_time.classtweaker"),
        "build/classTweaker.ct"
    ))
}

version = property("version") as String
base.archivesName = property("id") as String

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io") // mass asmer
    maven("https://maven.fabricmc.net")
    maven("https://api.modrinth.com/maven") {
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/releases") // modmenu
    maven("https://maven.parchmentmc.org")
    maven("https://maven.maxhenkel.de/repository/public") // simple voice chat
    maven("https://maven.ladysnake.org/releases") // cca, ratatouille
    maven("https://maven.uuid.gg/releases") // datasync
    maven("https://maven.midnightdust.eu/releases") // midnightlib
    maven("https://maven.bawnorton.com/releases") // mixinsquared
    ivy("https://github.com/TheTypholorian/asm_util/releases/download") { // asm util
        patternLayout {
            artifact("[revision]/[artifact]-[revision](-[classifier]).[ext]")
        }

        metadataSources {
            artifact()
        }
    }
}

dependencies {
    modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.155.2+26.1.2")

    // TODO: versioned deps

    modstitchModRuntimeOnly("dev.doctor4t:wathe:1.3.2-1.21.1") {
        isTransitive = false
    }
    modstitchModRuntimeOnly("dev.doctor4t:ratatouille:1.4.3-1.21.1") {
        isTransitive = false
    }

    modstitchModRuntimeOnly("dev.upcraft.datasync:datasync-minecraft-26.1-fabric:0.11.0")

    modstitchModRuntimeOnly("com.terraformersmc:modmenu:18.0.0")

    modstitchModImplementation("de.maxhenkel.voicechat:voicechat-api:2.6.20")
    modstitchModRuntimeOnly("maven.modrinth:simple-voice-chat:fabric-2.6.21+26.1.2")

    modstitchModImplementation("eu.midnightdust:midnightlib:1.9.3+26.1-fabric")

    modstitchModImplementation("org.ladysnake.cardinal-components-api:cardinal-components-base:8.0.1")
    modstitchModImplementation("org.ladysnake.cardinal-components-api:cardinal-components-level:8.0.1")
    modstitchModImplementation("org.ladysnake.cardinal-components-api:cardinal-components-entity:8.0.1")
    modstitchModImplementation("org.ladysnake.cardinal-components-api:cardinal-components-scoreboard:8.0.1")

    modstitchModCompileOnly("maven.modrinth:sodium:mc26.1.2-0.9.2-alpha.3-fabric")
    modstitchModCompileOnly("maven.modrinth:iris:1.11.2+26.1-fabric")

    implementation(modstitchJiJ("net.fabricmc:tiny-remapper:0.14.0")!!)
    implementation(modstitchJiJ("net.typho:asm_util:1.0.19")!!)
    implementation(modstitchJiJ("org.jetbrains.kotlin:kotlin-stdlib:2.3.21")!!)
    implementation(modstitchJiJ(project(":common"))!!)
}

tasks.named("runClient") {
    dependsOn(tasks.build)
}

tasks.named("runServer") {
    dependsOn(tasks.build)
}