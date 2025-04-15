plugins {
    application
    id("org.beryx.runtime") version "1.13.1" // latest as of now
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://litarvan.github.io/maven")
    }
}

dependencies {
    implementation("fr.litarvan:openauth:1.1.6")
    implementation("fr.flowarg:openlauncherlib:3.2.11")

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

val currentOs = System.getProperty("os.name").lowercase()

runtime {
    options.set(listOf("--strip-debug", "--no-header-files", "--no-man-pages"))
    modules.set(listOf(
        "java.base",    // inclus automatiquement mais indiqué pour clarté
        "java.desktop", // nécessaire pour AWT et Swing
        "jdk.crypto.ec" // si tu en as besoin pour SSL ou autres
    ))

    jpackage {
        imageName = "NewLauncher"
        installerName = "NewLauncherInstaller"
        appVersion = "1.0"

        jvmArgs = listOf("-Xmx512m")

        when {
            currentOs.contains("win") -> {
                installerType = "msi"
                installerOptions.addAll(listOf("--win-dir-chooser", "--win-menu", "--win-shortcut"))
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.ico"))
            }
            currentOs.contains("mac") -> {
                installerType = "dmg"
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.icns"))
            }
            currentOs.contains("nux") || currentOs.contains("nix") -> {
                installerType = "deb"
                imageOptions.addAll(listOf("--icon", "src/main/resources/icon.png"))
            }
        }
    }
}