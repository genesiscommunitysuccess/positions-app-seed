dependencies {
    implementation("global.genesis:genesis-pal-execution")
    implementation("global.genesis:genesis-eventhandler")
    implementation(project(":position-messages"))
    compileOnly("global.genesis:genesis-db")
    compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testCompileOnly(project(":position-config"))
    testImplementation("global.genesis:ref_data_app-config")
    testImplementation("global.genesis:market-data-config")
}

description = "position-samples"
