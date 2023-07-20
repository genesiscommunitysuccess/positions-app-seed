dependencies {
    implementation("global.genesis:genesis-messages")
    compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
}

description = "position-messages"
