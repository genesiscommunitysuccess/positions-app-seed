plugins {
  id("global.genesis.deploy")
}

description = "position-deploy"

deploy {
  productName = "position"
}

dockerImage {
  debugMode.set(false)
}
//
// dockerContainer {
//   networkName.set("genesis")
//   debugEnvVars.putAll(
//     mapOf(
//       "DB_LAYER" to "AEROSPIKE",
//       "DB_HOST" to "aerospike",
//       "DB_NAMESPACE" to "test",
//
//       "MQ_LAYER" to "ZeroMQ",
//
//       "CONSUL_SERVER_URL" to "consul",
//
//       "MQTT_BROKER_URL" to "tcp://172.17.0.5:1883",
//       "MQTT_QOS" to "2"
//     )
//   )
// }

dependencies {
  genesisServer(
    group = "global.genesis",
    name = "genesis-distribution",
    version = properties["genesisVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "auth-distribution",
    version = properties["authVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "fix-distribution",
    version = properties["fixVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "market-data-distribution",
    version = properties["marketDataVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "ref_data_app-distribution",
    version = properties["refDataVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "elektron-distribution",
    version = properties["elektronVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "reporting-distribution",
    version = properties["reportingVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(
    group = "global.genesis",
    name = "genesis-notify-distribution",
    version = properties["notifyVersion"].toString(),
    classifier = "bin",
    ext = "zip"
  )
  genesisServer(project(":position-distribution", "distribution"))
  genesisServer(project(":position-site-specific", "distribution"))

  if (project.ext["skipWeb"] != "true") {
    genesisWeb("client:web")
  }

  api(project(":position-distribution", "distribution"))
  api(project(":position-eventhandler"))
  api(project(":position-messages"))
  api(project(":position-site-specific", "distribution"))
}
