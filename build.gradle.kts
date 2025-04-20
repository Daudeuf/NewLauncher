plugins {
    application
    id("org.beryx.jlink") version "2.26.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
    maven { url = uri("https://litarvan.github.io/maven") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("fr.litarvan:openauth:1.1.6")
    implementation("fr.flowarg:openlauncherlib:3.2.11")
    implementation("fr.flowarg:flowupdater:1.9.2")
    implementation("com.github.Querz:NBT:6.1")
    implementation("org.json:json:20240303")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(23))
}

application {
    mainClass.set("fr.clem76.Main")
}

javafx {
    version = "21"
    modules = listOf("javafx.swing", "javafx.web")
}

val currentOs = org.gradle.internal.os.OperatingSystem.current()

jlink {
    // JavaFX + Java modules à inclure dans le runtime
    mergedModule {
        requires("java.base")
        requires("java.desktop")
        requires("jdk.crypto.ec")
        requires("javafx.swing")
        requires("javafx.web")
    }

    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        name = "NewLauncher"
    }

    jpackage {
        imageName = "NewLauncher"
        installerName = "NewLauncherInstaller"
        appVersion = "1.0.0"
        jvmArgs = listOf("-Xmx512m")

        when {
            currentOs.isWindows -> {
                installerType = "msi"
                installerOptions.addAll(listOf("--win-dir-chooser", "--win-menu", "--win-shortcut"))
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.ico"))
            }
            currentOs.isMacOsX -> {
                installerType = "dmg"
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.icns"))
            }
            currentOs.isLinux -> {
                installerType = "deb"
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.png"))
            }
        }
    }
}
