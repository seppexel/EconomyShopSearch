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
    maven("https://repo.bg-software.com/repository/api/")
}

dependencies {
    // Paper 1.21.1 Development environment
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")
    
    // The "+" sign tells Gradle to find the latest version automatically
    compileOnly("me.gypopo:EconomyShopGUI-API:+")
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
