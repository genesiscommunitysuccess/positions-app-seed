dependencies {
    implementation("global.genesis:genesis-pal-execution")
    api("global.genesis:fix-xlator")
    api("global.genesis:FIX44_ref")
    api(project(":position-eventhandler"))
    // "api" necessary so we can use the dependency when executing tests.
    api("global.genesis:genesis-pal-eventhandler")
    api("global.genesis:genesis-pal-requestserver")
    api("global.genesis:genesis-consolidator2")
    api("global.genesis:genesis-pal-consolidator")
    api("global.genesis:genesis-pal-datapipeline")
    api("global.genesis:genesis-pal-camel")
    api(project(":position-messages"))
    compileOnly("global.genesis:genesis-dictionary")
    compileOnly("global.genesis:genesis-pal-dataserver")
    compileOnly("global.genesis:genesis-pal-streamer")
    compileOnly("global.genesis:genesis-pal-streamerclient")
    compileOnly(project(path = ":position-dictionary-cache", configuration = "codeGen"))
    testCompileOnly(project(":position-config"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    // Config dependencies to add the dictionary schemas to our test execution
    testImplementation(project(":position-config"))
    testImplementation("global.genesis:ref_data_app-config")
    testImplementation("global.genesis:market-data-config")
    testImplementation(project(path = ":position-dictionary-cache", configuration = "codeGen"))
}

description = "position-script-config"
