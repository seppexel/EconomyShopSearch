plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
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
    maven("https://jitpack.io") // Target JitPack
}

dependencies {
    // Paper 1.21.1 Development environment
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")
    
    // This targets the live branch directly, bypassing the broken version tags!
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:main-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
}
