import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.tree.MemoryMappingTree
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.FileNotFoundException

plugins {
    java

    id("me.modmuss50.mod-publish-plugin") version "2.0.0-beta.1"
    id("io.github.klahap.dotenv") version "1.1.3"

    id("dev.isxander.modstitch.base") version "0.8.5"
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
                vmArg("-Dtrain_across_time:mappings_output_file=${rootProject.file("src/main/resources/mappings.bin").absolutePath}")
                ideConfigGenerated(true)
            }
            enableTransitiveAccessWideners = false
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
}

// What do they say, "Fake it 'til you make it"? Something like that
val genTemplateIntermediaryClasses by tasks.registering {
    val outputDir = layout.projectDirectory.dir("run/.template_intermediary")
    outputs.dir(outputDir)

    doLast {
        val path = "run/mappings.tiny"
        val mappingsTiny = file(path)
        if (!mappingsTiny.exists()) {
            println("(Template Intermediary) Unable to find mappings.tiny file at " + "path" + ", skipping...")
            return@doLast
        }

        val tree = MemoryMappingTree()
        MappingReader.read(mappingsTiny.toPath(), tree)

        val targetDir = outputDir.asFile

        tree.classes.forEach { classMapping ->
            val intermediary = classMapping.getName("intermediary")
            @Suppress("FoldInitializerAndIfToElvis", "RedundantSuppression")
            if (intermediary == null) {
                return@forEach
            }

            val classFile = File(targetDir, "$intermediary.class")
            val folder = classFile.parentFile

            if (!folder.mkdirs() && !folder.exists()) {
                throw FileNotFoundException("File with path " + classFile.path + " could not be written to!")
            }

            val writer = ClassWriter(0) // the flags passed in here are also the number of clues I have of what the flags should be
            writer.visit(
                Opcodes.V25,
                Opcodes.ACC_PUBLIC
                        or Opcodes.ACC_INTERFACE
                        or Opcodes.ACC_ABSTRACT,
                intermediary,
                null,
                "java/lang/Object", // no L apparently
                null
            )
            classFile.writeBytes(writer.toByteArray())
        }
    }
}

val clearTemplateIntermediaryClasses by tasks.registering {
    val outputDir = layout.projectDirectory.dir("run/.template_intermediary")

    doLast {
        if (!outputDir.asFile.deleteRecursively()) {
            println("(Template Intermediary) Failed to delete all files in $outputDir")
        }
    }
}

dependencies {
    modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.155.2+26.1.2")

    // TODO: versioned deps

    modstitchModImplementation("dev.doctor4t:wathe:1.3.2-1.21.1") {
        isTransitive = false
    }
    modstitchModImplementation("dev.doctor4t:ratatouille:1.4.3-1.21.1") {
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

    include(implementation("com.github.cputnam-a11y:MassAsmer:c1a863f7e6")!!)

    implementation("net.fabricmc:tiny-remapper:0.14.0")

    compileOnly(files(genTemplateIntermediaryClasses))

    // if needed
    //include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.3.7-beta.3")))
}

// Hook it into the compile task
tasks.named<JavaCompile>("compileJava") {
    dependsOn(genTemplateIntermediaryClasses)
}

tasks.processResources {
    dependsOn(project(":agent").tasks.build)
    dependsOn(clearTemplateIntermediaryClasses)
}