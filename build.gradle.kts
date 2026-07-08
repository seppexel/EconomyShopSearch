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
    // Official repository hosting the EconomyShopGUI API
    maven("https://repo.bg-software.com/repository/api/")
}

dependencies {
    // Paper 1.21.1 Development environment
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")
    
    // Pulls the verified API directly via Maven dependency management
    compileOnly("me.gypopo:EconomyShopGUI-API:1.10.0")
} // <-- This is the bracket that was missing!

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
}
