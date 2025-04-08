plugins {
    application
    id("org.beryx.runtime") version "1.13.1" // latest as of now
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(23))
}

application {
    mainClass.set("fr.clem76.Main") // TODO: change to your main class
}

val currentOs = System.getProperty("os.name").lowercase()

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("jdk.crypto.ec"))

    jpackage {
        imageName = "NewLauncher"
        installerName = "NewLauncherInstaller"
        appVersion = "1.0"

        jvmArgs = listOf("-Xmx512m")
        mainJar = "${project.name}-all.jar"

        when {
            currentOs.contains("win") -> {
                installerType = "msi"
                installerOptions.addAll(
                    listOf(
                        "--win-dir-chooser",
                        "--win-menu",
                        "--win-shortcut",
                        "--icon", "src/main/resources/icon.ico"
                    )
                )
            }

            currentOs.contains("mac") -> {
                installerType = "dmg" // ou "pkg"
                installerOptions.addAll(
                    listOf("--icon", "src/main/resources/icon.icns")
                )
            }

            currentOs.contains("nux") || currentOs.contains("nix") -> {
                installerType = "deb" // ou "rpm" si tu préfères
                installerOptions.addAll(
                    listOf("--icon", "src/main/resources/icon.png")
                )
            }

            else -> {
                // OS non supporté
                println("⚠️ OS non reconnu : installateur non configuré.")
            }
        }
    }
}