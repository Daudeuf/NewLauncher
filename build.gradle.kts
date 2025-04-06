import java.util.*

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


// --------------------------------------------------------------------------------------------------------------



// --------------------------------------------------------------------------------------------------------------


tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.clem76.Main" // adapte avec ton package
    }
}

// Fat JAR
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("NewLauncher")
    archiveClassifier.set("") // pas de "-all"
    archiveVersion.set("1.0")
}

val os = org.gradle.internal.os.OperatingSystem.current()
val iconPath = when {
    os.isWindows -> "src/main/resources/icons/icon.ico"
    os.isMacOsX -> "src/main/resources/icons/icon.icns"
    else -> "src/main/resources/icons/icon.png"
}

val mainJarName = "NewLauncher.jar"
val mainClassName = "fr.clem76.Main"
val appVersion = "1.0"

fun List<String>.jpackageCommonArgs(): List<String> {
    return this + listOf(
        "--name", "NewLauncher",
        "--input", "build/libs",
        "--main-jar", mainJarName,
        "--main-class", mainClassName,
        "--app-version", appVersion,
        "--icon", iconPath,
        "--dest", "build/jpackage"
    )
}

// JPackage pour Windows
tasks.register<Exec>("jpackageWindows") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .jpackageCommonArgs() +
            listOf(
                "--type", "msi",
                "--win-menu",
                "--win-shortcut",
                "--win-dir-chooser",  // Option supplémentaire pour personnaliser l'installation
            )
    }
}

// JPackage pour macOS
tasks.register<Exec>("jpackageMac") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .jpackageCommonArgs() +
            listOf(
                "--type", "dmg",
                "--mac-package-identifier", "com.clem76.newlauncher", // Option pour identifier le package
                "--mac-package-name", "NewLauncher",
                "--mac-sign", "NONE" // Option pour la signature sur macOS, à adapter si besoin
            )
    }
}

// JPackage pour Linux
tasks.register<Exec>("jpackageLinux") {
    dependsOn("shadowJar")
    doFirst {
        commandLine = listOf("jpackage")
            .jpackageCommonArgs() +
            listOf(
                "--type", "deb",
                "--linux-shortcut",
                "--linux-package-name", "newlauncher"
            )
    }
}
