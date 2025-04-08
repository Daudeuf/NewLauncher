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

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("jdk.crypto.ec"))

    jpackage {
        imageName = "NewLauncher"
        installerName = "NewLauncherInstaller"
        appVersion = "1.0"

        jvmArgs = listOf("-Xmx512m")

        // licenseFile.set(project.file("LICENSE.txt"))

        // Utilisation du hook doLast pour configurer dynamiquement les options spécifiques à l'OS
        installerOptions.addAll(
            provider {
                val os = System.getProperty("os.name").lowercase()
                when {
                    os.contains("win") -> listOf(
                        "--win-dir-chooser",
                        "--win-menu",
                        "--win-shortcut",
                        "--icon", "src/main/resources/icon.ico"
                    )
                    os.contains("mac") -> listOf(
                        "--icon", "src/main/resources/icon.icns"
                    )
                    os.contains("nux") || os.contains("nix") -> listOf(
                        "--icon", "src/main/resources/icon.png"
                    )
                    else -> emptyList()
                }
            }.get() // <- la correction est ici
        )

    }
}
