import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
  id("java")
  id("com.gradleup.shadow") version "9.2.2"
  id("xyz.jpenilla.run-paper") version "2.3.1"
  id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

group = "org.amethystdev"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {

  // Paper
  compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
  paperLibrary("com.google.code.gson", "gson", "2.13.1")

}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

// paper-plugin.yml
paper {
  name = "MyPlugin"
  version = "1.0.0"
  description = "My first plugin"

  main = "org.amethystdev.Main"
  prefix = "MyPlugin"
  apiVersion = "1.21"
  authors = listOf("Phrut", "greenlantern456")

  // Plugin bootstrapper/loader
  bootstrapper = "org.amethystdev.Bootstrapper"
  loader = "org.amethystdev.PluginLibrariesLoader"
  hasOpenClassloader = false
  generateLibrariesJson = true

  // TODO: Add Folia support
  //foliaSupported = true

  serverDependencies {
    register("LuckPerms") {
      load = PaperPluginDescription.RelativeLoadOrder.BEFORE
      required = false
    }

    register("WorldEdit") {
      load = PaperPluginDescription.RelativeLoadOrder.BEFORE
      required = false
    }

    register("Essentials") {
      required = false
      joinClasspath = false
    }

    register("PlaceHolderAPI") {
      load = PaperPluginDescription.RelativeLoadOrder.BEFORE
      required = false
    }
  }
}

tasks {
  generatePaperPluginDescription {
    addMavenCentralProxy("google", "https://maven-central.storage-download.googleapis.com/maven2")
  }

  runServer {
    minecraftVersion("1.21.8")
    downloadPlugins {
      // FastAsyncWorldEdit
      url("https://ci.athion.net/job/FastAsyncWorldEdit/1135/artifact/artifacts/FastAsyncWorldEdit-Paper-2.13.1-SNAPSHOT-1135.jar")

      // WorldGuard
      modrinth("worldguard", "7.0.14")

      // Vault replacement
      hangar("ServiceIO","2.2.0")

      // Hangar
      hangar("PlaceholderAPI", "2.11.6")

      // luckperms
      url("https://download.luckperms.net/1595/bukkit/loader/LuckPerms-Bukkit-5.5.10.jar")

    }
  }
}