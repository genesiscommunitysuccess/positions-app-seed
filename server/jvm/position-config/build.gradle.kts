dependencies {
    compileOnly("global.genesis:genesis-dictionary")
    compileOnly("global.genesis:genesis-process")
    compileOnly("global.genesis:genesis-pal-execution")
    testImplementation("global.genesis:genesis-consolidator2")
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation("global.genesis:ref_data_app-config")
    testImplementation("global.genesis:market-data-config")
    testImplementation("global.genesis:genesis-notify-config")
    testImplementation("global.genesis:reporting-config")
    compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testImplementation(project(path = ":position-dictionary-cache", configuration = "codeGen"))
}

description = "position-config"
