plugins {
    id("net.fabricmc.fabric-loom")
}

val minecraft = "26.3"
val fabricLoader = "0.19.5"
val neoForge = "26.3.0.42-beta"
val forge = "26.3-66.0.9"
val forgeLoader = forge.substringAfterLast("-")
val java = "25"
val modId = "borderlessmc"
val modVersion = "26.10.07+$minecraft"
val modName = "BorderlessMC"
val modDescription = "Allows you to play Minecraft in a borderless fullscreen window."
val modAuthor = "ValiantGiant985"
val modGroup = "dev.valiantgiant985.borderlessmc"
val sourcesUrl = "https://github.com/ValiantGiant985/BorderlessMC"

base.archivesName = modId
version = modVersion
group = modGroup

sourceSets {
    main {
        java.srcDir(rootProject.file("src/main/java"))
        resources.srcDir(rootProject.file("src/main/resources"))
    }
}

loom.accessWidenerPath = rootProject.file("src/main/resources/$modId.accesswidener")

repositories {
    maven("https://api.modrinth.com/maven/") { content { includeGroup("maven.modrinth") } }
    maven("https://maven.neoforged.net/releases/") { content { includeGroupByRegex("net.neoforged.*") } }
    maven("https://maven.minecraftforge.net/") { content { includeGroup("net.minecraftforge") } }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    implementation("net.fabricmc:fabric-loader:$fabricLoader")
    compileOnly("net.neoforged:neoforge:$neoForge")
    compileOnly("net.neoforged.fancymodloader:loader:11.0.0")
    compileOnly("net.minecraftforge:forge:$forge:universal")
    compileOnly("net.minecraftforge:javafmllanguage:$forge")
    compileOnly("net.minecraftforge:fmlloader:$forge")
    compileOnly("net.minecraftforge:fmlcore:$forge")
    compileOnly("maven.modrinth:sodium:mc26.3-0.9.2-fabric")
    compileOnly("maven.modrinth:modmenu:21.0.0-beta.1")
    compileOnly("maven.modrinth:yacl:3.9.7+26.3-fabric")
    compileOnly("maven.modrinth:cloth-config:26.3.158+fabric")
    compileOnly("ca.weblite:java-objc-bridge:1.1")
}

tasks.processResources {
    filesMatching(listOf("*.mod.json", "META-INF/*.toml", "*.mcmeta")) {
        expand(
            mapOf(
                "id" to modId,
                "version" to modVersion,
                "name" to modName,
                "description" to modDescription,
                "environment" to "client",
                "author" to modAuthor,
                "group" to modGroup,
                "license" to "PolyForm Shield 1.0.0",
                "modrinth" to "",
                "curseforge" to "",
                "homepage" to sourcesUrl,
                "sources" to sourcesUrl,
                "issues" to "$sourcesUrl/issues",
                "fabric_loader" to fabricLoader,
                "neoforge" to neoForge,
                "forge" to forgeLoader,
                "minecraft" to minecraft,
                "java" to java
            )
        )
    }
}

tasks.jar {
    from(rootProject.file(".")) {
        include("LICENSE*")
    }
    manifest.attributes("MixinConfigs" to "$modId.mixins.json")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = java.toInt()
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.toVersion(java)
    targetCompatibility = sourceCompatibility
}
