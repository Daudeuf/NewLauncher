import org.gradle.internal.os.OperatingSystem

plugins {
    application
    id("java")
    id("org.beryx.jlink") version "3.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.clem76"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303") // JSON lib
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(23))
}

application {
    mainClass.set("fr.clem76.Main")
    mainModule.set("fr.clem76")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.clem76.Main"
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("NewLauncher")
    archiveClassifier.set("")
    archiveVersion.set("1.0")
}

// Détection OS pour choisir l'icône
val os = OperatingSystem.current()
val iconExtension = when {
    os.isWindows -> "ico"
    os.isMacOsX -> "icns"
    else -> "png"
}
val iconPath = "src/main/resources/icons/icon.$iconExtension"

// jlink + jpackage config
jlink {
    imageName.set("NewLauncher")

    launcher {
        name = "NewLauncher"
    }

    jpackage {
        imageOutputDir = layout.buildDirectory.dir("jpackage").get().asFile
        installerOutputDir = layout.buildDirectory.dir("installers").get().asFile
        installerName = "NewLauncher"

        /*installerType = when {
            os.isWindows -> "msi"      // ou "exe" si supporté
            os.isMacOsX -> "dmg"
            else -> "deb"
        }*/

        icon = file(iconPath).absolutePath
        skipInstaller = false
        appVersion = "1.0"
        vendor = "Clem76 Software"
        description = "NewLauncher - A minimal Swing App"
    }
}