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
    maven {
        url = uri("https://jitpack.io/")
    }
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

val currentOs = System.getProperty("os.name").lowercase()

runtime {
    options.set(listOf("--strip-debug", "--no-header-files", "--no-man-pages"))
    modules.set(listOf(
        "java.base",
        "java.compiler",
        "java.datatransfer",
        "java.desktop",
        "java.instrument",
        "java.logging",
        "java.management",
        "java.management.rmi",
        "java.naming",
        "java.net.http",
        "java.prefs",
        "java.rmi",
        "java.scripting",
        "java.se",
        "java.security.jgss",
        "java.security.sasl",
        "java.smartcardio",
        "java.sql",
        "java.sql.rowset",
        "java.transaction.xa",
        "java.xml",
        "java.xml.crypto",
        "jdk.accessibility",
        "jdk.attach",
        "jdk.charsets",
        "jdk.compiler",
        "jdk.crypto.cryptoki",
        "jdk.crypto.ec",
        "jdk.dynalink",
        "jdk.editpad",
        "jdk.graal.compiler",
        "jdk.graal.compiler.management",
        "jdk.hotspot.agent",
        "jdk.httpserver",
        "jdk.incubator.vector",
        "jdk.internal.ed",
        "jdk.internal.jvmstat",
        "jdk.internal.le",
        "jdk.internal.md",
        "jdk.internal.opt",
        "jdk.internal.vm.ci",
        "jdk.jartool",
        "jdk.javadoc",
        "jdk.jcmd",
        "jdk.jconsole",
        "jdk.jdeps",
        "jdk.jdi",
        "jdk.jdwp.agent",
        "jdk.jfr",
        "jdk.jlink",
        "jdk.jpackage",
        "jdk.jshell",
        "jdk.jsobject",
        "jdk.jstatd",
        "jdk.localedata",
        "jdk.management",
        "jdk.management.agent",
        "jdk.management.jfr",
        "jdk.naming.dns",
        "jdk.naming.rmi",
        "jdk.net",
        "jdk.nio.mapmode",
        "jdk.sctp",
        "jdk.security.auth",
        "jdk.security.jgss",
        "jdk.unsupported",
        "jdk.unsupported.desktop",
        "jdk.xml.dom",
        "jdk.zipfs"
    ))

    jpackage {
        imageName = "Diamoria"
        installerName = "DiamoriaInstaller"
        appVersion = "1.0"
        installerOptions.addAll(listOf("--vendor", "Lost Studio"))

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