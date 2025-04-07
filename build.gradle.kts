import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.clem76"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303") // version récente
}

// Définir le chemin de l'icône en fonction du système d'exploitation
val os = OperatingSystem.current()
val iconPath = when {
    os.isWindows -> "src/main/resources/icons/icon.ico"
    os.isMacOsX -> "src/main/resources/icons/icon.icns"
    else -> "src/main/resources/icons/icon.png"
}

val mainJarName = "NewLauncher.jar"
val mainClassName = "fr.clem76.Main"
val appVersion = "1.0"

// Définir l'icône dans le manifeste
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}

// Créer le JAR avec le plugin Shadow (Fat JAR)
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("NewLauncher")
    archiveClassifier.set("") // pas de "-all"
    archiveVersion.set("1.0")
}

// Définir la tâche JPackage pour chaque plateforme

// JPackage pour Windows
tasks.register<Exec>("jpackageWindows") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .plus(listOf(
                "--input", "build/libs",
                "--main-jar", mainJarName,
                "--main-class", mainClassName,
                "--app-version", appVersion,
                "--java-options", "-Xmx512m",
                "--icon", iconPath,
                "--dest", "build/jpackage"
            ))
            .plus(listOf(
                "--type", "exe",
                "--win-menu",
                "--win-shortcut"
            ))
    }
}

// JPackage pour macOS
tasks.register<Exec>("jpackageMac") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .plus(listOf(
                "--input", "build/libs",
                "--main-jar", mainJarName,
                "--main-class", mainClassName,
                "--app-version", appVersion,
                "--java-options", "-Xmx512m",
                "--icon", iconPath,
                "--dest", "build/jpackage"
            ))
            .plus(listOf(
                "--type", "dmg"
            ))
    }
}

// JPackage pour Linux
tasks.register<Exec>("jpackageLinux") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .plus(listOf(
                "--input", "build/libs",
                "--main-jar", mainJarName,
                "--main-class", mainClassName,
                "--app-version", appVersion,
                "--java-options", "-Xmx512m",
                "--icon", iconPath,
                "--dest", "build/jpackage"
            ))
            .plus(listOf(
                "--type", "deb",
                "--linux-shortcut"
            ))
    }
}

