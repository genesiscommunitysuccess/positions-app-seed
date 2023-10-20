ext.set("localDaogenVersion", "POSITION")

plugins {
  kotlin("jvm") version "1.7.10"
  `maven-publish`
  id("global.genesis.build")
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.gradle.maven-publish")

  dependencies {
    implementation(platform("global.genesis:genesis-bom:${properties["genesisVersion"]}"))
    implementation("org.agrona:agrona:1.10.0!!")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.10")
    constraints {
      // define versions of your dependencies here so that submodules do not have to define explicit versions
      api("global.genesis:fix-xlator:${properties["fixVersion"]}")
      api("global.genesis:FIX44_ref:${properties["fixVersion"]}")
      api("global.genesis:ref_data_app-config:${properties["refDataVersion"]}")
      api("global.genesis:market-data-config:${properties["marketDataVersion"]}")
      api("global.genesis:fix-config:${properties["fixVersion"]}")
      api("global.genesis:auth-config:${properties["authVersion"]}")
      api("global.genesis:reporting-config:${properties["reportingVersion"]}")
      api("global.genesis:genesis-notify-config:${properties["notifyVersion"]}")
      testImplementation("junit:junit:4.13.2")
    }
  }
  tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
        jvmTarget = "11"
      }
    }
    test {
      systemProperty("DbLayer", "SQL")
      systemProperty("DbHost", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
      systemProperty("DbQuotedIdentifiers", "true")
    }
    jar {
      duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
  }
}

tasks {
  assemble {
    for (subproject in subprojects) {
      dependsOn(subproject.tasks.named("assemble"))
    }
  }
  build {
    for (subproject in subprojects) {
      dependsOn(subproject.tasks.named("build"))
    }
  }
  clean {
    for (subproject in subprojects) {
      dependsOn(subproject.tasks.named("clean"))
    }
  }
}

allprojects {

  group = "global.genesis"
  version = "1.0.0-SNAPSHOT"

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
    }
  }

  repositories {
    mavenCentral()
    maven {
      val repoUrl = if(extra.properties["clientSpecific"] == "true") {
          "https://genesisglobal.jfrog.io/genesisglobal/libs-release-client"
      } else {
          "https://genesisglobal.jfrog.io/genesisglobal/dev-repo"
      }
      url = uri(repoUrl)
      credentials {
        username = properties["genesisArtifactoryUser"].toString()
        password = properties["genesisArtifactoryPassword"].toString()
      }
      content {
        fun disableIfTrue(
          property: String,
          moduleRegex: String
        ) {
          if (project.ext[property] == "true") excludeModuleByRegex("global.genesis", moduleRegex)
        }

        disableIfTrue("localGenesis", "genesis-[\\w-]+")
        disableIfTrue("localAuth", "auth-[\\w-]+")
        disableIfTrue("localFix", "fix-[\\w-]+")
        disableIfTrue("localMarketData", "market-data-[\\w-]+")
        disableIfTrue("localElektron", "elektron-[\\w-]+")
        disableIfTrue("localRefData", "ref_data_app-[\\w-]+")
        disableIfTrue("localDeployPlugin", "deploy-gradle-plugin")
      }
    }
    mavenLocal {
      // VERY IMPORTANT!!! EXCLUDE AGRONA AS IT IS A POM DEPENDENCY AND DOES NOT PLAY NICELY WITH MAVEN LOCAL!
      content {
        excludeGroup("org.agrona")
      }
    }
  }

  publishing {
    publications.create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}
