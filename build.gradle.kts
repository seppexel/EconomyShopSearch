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
}

dependencies {
    // Paper 1.21.1 Development environment
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")
    
    // Instead of JitPack, we'll use a local folder that our GitHub action will fill with the code
    implementation(fileTree("libs") { include("*.jar") })
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
