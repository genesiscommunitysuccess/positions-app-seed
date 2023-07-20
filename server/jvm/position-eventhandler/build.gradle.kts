dependencies {
    implementation("global.genesis:genesis-pal-execution")
    implementation("global.genesis:genesis-eventhandler")
    implementation(project(":position-messages"))
    compileOnly("global.genesis:genesis-db")
    compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testImplementation(project(":position-config"))
}

description = "position-eventhandler"
