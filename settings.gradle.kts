rootProject.name = "position"

// servers
includeBuild("server/jvm") {
  name = "genesisproduct-position"
}

// clients
includeBuild("client")
