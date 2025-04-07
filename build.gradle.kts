import org.gradle.internal.os.OperatingSystem

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.clem76"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
}

val os = OperatingSystem.current()
val iconPath = when {
    os.isWindows -> "src/main/resources/icons/icon.ico"
    os.isMacOsX -> "src/main/resources/icons/icon.icns"
    else -> "src/main/resources/icons/icon.png"
}

val mainClassName = "fr.clem76.Main"
val appVersion = version.toString()
val appName = "NewLauncher"
val mainJarName = "$appName-$appVersion.jar"
val imageDir = "$buildDir/image"
val runtimeDir = "$buildDir/runtime"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set(appName)
    archiveClassifier.set("")
    archiveVersion.set(appVersion)
}

tasks.register<Exec>("jlinkImage") {
    dependsOn("shadowJar")
    doFirst {
        delete(runtimeDir)
    }
    commandLine = listOf(
        "jlink",
        "--output", runtimeDir,
        "--add-modules", "jdk.crypto.ec,jdk.unsupported,jdk.zipfs,jdk.charsets", // minimal + crypto
        "--strip-debug",
        "--compress", "2",
        "--no-header-files",
        "--no-man-pages"
    )
}

fun registerJPackage(name: String, type: String, extraOptions: List<String> = emptyList()) {
    tasks.register<Exec>(name) {
        dependsOn("jlinkImage")

        onlyIf {
            val currentOS = OperatingSystem.current()
            when (name) {
                "jpackageWindows" -> currentOS.isWindows
                "jpackageMac" -> currentOS.isMacOsX
                "jpackageLinux" -> currentOS.isLinux
                else -> false
            }
        }

        doFirst {
            delete("$buildDir/jpackage")
        }

        commandLine = listOf("jpackage") + listOf(
            "--runtime-image", runtimeDir,
            "--input", "build/libs",
            "--main-jar", mainJarName,
            "--main-class", mainClassName,
            "--app-version", appVersion,
            "--icon", iconPath,
            "--dest", "$buildDir/jpackage",
            "--name", appName,
            "--type", type
        ) + extraOptions
    }
}

registerJPackage("jpackageWindows", "exe", listOf("--win-shortcut"))
registerJPackage("jpackageMac", "dmg")
registerJPackage("jpackageLinux", "deb", listOf("--linux-shortcut"))
