plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "committee.nova.mkb.forge"
version = project.property("version") as String
val modId: String = project.property("modId") as String

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

// Minecraft configuration:
loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            // If you don't want mixins, remove these lines
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--mixin", "mixins.${modId}.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        // If you don't want mixins, remove this lines
        mixinConfig("mixins.${modId}.json")
    }
    // If you don't want mixins, remove these lines
    mixin {
        defaultRefmapName.set("mixins.${modId}.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

// Dependencies:

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    // If you don't want to log in with your real minecraft account, remove this line
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    // If you don't want mixins, remove these lines
    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    //annotationProcessor("org.spongepowered:mixin:0.8.5")
    //testAnnotationProcessor("org.spongepowered:mixin:0.8.5")
    // If you don't want to log in with your real minecraft account, remove this line
    //runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.0")

}

// Tasks:

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set("ModernKeyBinding-Forge-1.8.9")
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        // If you don't want mixins, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.${modId}.json"
    }
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveBaseName.set("ModernKeyBinding-Forge-1.8.9")
    if (project.hasProperty("nomixin")) {
        archiveClassifier.set("mixinless")
        return@named
    }
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.shadowJar {
    archiveClassifier.set("dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Config: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "com.${modId}.deps.$name")
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)
    filesMatching("mcmod.info") {
        expand(mapOf("version" to project.version))
    }
}

tasks.assemble.get().dependsOn(tasks.remapJar)

publishing {
    publications {
        val mavenPublication by register("maven", MavenPublication::class) {
            from(components["java"])
            artifactId = "mkb-1.8.9"
        }
    }

    repositories {
        maven {
            url = uri("https://maven.nova-committee.cn/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}