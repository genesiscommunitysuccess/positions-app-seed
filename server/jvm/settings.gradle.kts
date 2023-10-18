rootProject.name = "genesisproduct-position"

pluginManagement {

  pluginManagement {
    val genesisVersion: String by settings
    val deployPluginVersion: String by settings
    plugins {
      id("global.genesis.build") version genesisVersion
      id("global.genesis.deploy") version deployPluginVersion
    }
  }

  repositories {
    mavenLocal {
      // VERY IMPORTANT!!! EXCLUDE AGRONA AS IT IS A POM DEPENDENCY AND DOES NOT PLAY NICELY WITH MAVEN LOCAL!
      content {
        excludeGroup("org.agrona")
      }
    }
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://genesisglobal.jfrog.io/genesisglobal/dev-repo")
      credentials {
        username = extra.properties["genesisArtifactoryUser"].toString()
        password = extra.properties["genesisArtifactoryPassword"].toString()
      }
    }
  }
}

include("position-config")
include("position-deploy")
include("position-messages")
include("position-eventhandler")
include("position-script-config")
include("position-distribution")
include("position-dictionary-cache")
include("position-dictionary-cache:position-generated-sysdef")
include("position-dictionary-cache:position-generated-fields")
include("position-dictionary-cache:position-generated-dao")
include("position-dictionary-cache:position-generated-hft")
include("position-dictionary-cache:position-generated-view")
include("position-site-specific")
include("position-samples")
includeBuild("../../client")
