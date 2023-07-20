
// Add your genesis config dependencies here
dependencies {
  implementation(project(":position-dictionary-cache:position-generated-dao"))
  implementation(project(":position-dictionary-cache:position-generated-fields"))
  implementation(project(":position-dictionary-cache:position-generated-hft"))
  implementation(project(":position-dictionary-cache:position-generated-sysdef"))
  implementation(project(":position-dictionary-cache:position-generated-view"))
  api("global.genesis:auth-config")
  api("global.genesis:ref_data_app-config")
  api("global.genesis:market-data-config")
  api("global.genesis:fix-config")
  api("global.genesis:reporting-config")
  api("global.genesis:genesis-notify-config")
}

description = "position-dictionary-cache"
