plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

group = "me.nahu.portals"
version = "0.1.0"

repositories {
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "spigotmc"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        name = "aikar-repo"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        name = "themoep-repo"
        url = uri("https://repo.minebench.de/")
    }

    maven {
        name = "enginehub-repo"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        name = "chatmenuapi-repo"
        url = uri("https://dl.bintray.com/nahuld/minevictus/")
    }

    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.5")
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT")
    implementation("me.tom.sparse:ChatMenuAPI:1.1.2_jdk8_spgt1.8.8")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

bukkit {
    name = "Portals"
    description = "Very nice portals plugin!"
    main = "me.nahu.portals.PortalsPlugin"
    authors = listOf("NahuLD")
    depend = listOf("WorldEdit")
}
