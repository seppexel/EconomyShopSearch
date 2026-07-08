plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1" // Required for Paper 1.21+
}

group = "com.yourname.economyshopsearch"
version = "1.0.0"
description = "Modern search GUI addon for EconomyShopGUI"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io") // For EconomyShopGUI API
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    
    // EconomyShopGUI API 
    // Uses JitPack to pull the mock API provided by the developer
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.8.0")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
}
