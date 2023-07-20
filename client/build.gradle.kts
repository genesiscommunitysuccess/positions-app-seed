import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "3.1.1"
}

node {
    // Version of node to use.
    version.set("18.15.0")

    // Version of npm to use.
    npmVersion.set("9.5.0")

    // If true, it will download node using above parameters.
    // If false, it will try to use globally installed node.
    download.set(false)
}

allprojects {
    group = "global.genesis"
    version = "1.0.0-SNAPSHOT"
}

tasks {

    val npmBootstrap = register("npmBootstrap", NpmTask::class) {
        val workingDir = layout.projectDirectory.asFile
        args.set(listOf("run", "bootstrap"))
        outputs.upToDateWhen { File(workingDir, "bootstrapDone").exists() }
        doLast { File(workingDir, "bootstrapDone").createNewFile() }
    }

    assemble {
        dependsOn(npmBootstrap)
        for (project in subprojects) {
            dependsOn(project.tasks.named("assemble"))
        }
    }

    build {
        for(project in subprojects){
            dependsOn(project.tasks.named("build"))
        }
    }

    val testsExecutedMarkerName: String = "${projectDir}/.tests.executed"

    val test = register("test", NpmTask::class) {
        // Disable tests for now, if there are not tests (like in this example) the build returns 1 and fails.
        enabled = false
        args.set(listOf("run", "test"))
        inputs.files(fileTree("packages"))
        inputs.files(fileTree("www"))
        inputs.file("package.json")

        // Below some potentially useful config snippets if we want to be efficient with test executions.
        //
        // force Jest test runner to execute tests once and finish the process instead of starting watch mode
        // setEnvironment(mapOf("CI" to "true"))

        // allows easy triggering re-tests
        // doLast {
        //     File(testsExecutedMarkerName).appendText("delete this file to force re-execution JavaScript tests")
        // }
        // outputs.file(testsExecutedMarkerName)
    }

    check {
        dependsOn(test)
        for(project in subprojects){
            dependsOn(project.tasks.named("check"))
        }
    }

    // Setup custom clean task to be run when "clean" task runs.
    val npmClean = register("npmClean", NpmTask::class) {
        args.set(listOf("run", "clean"))
        delete("bootstrapDone")
    }

    // Setup custom clean task to be run when "clean" task runs.
    val npmCleanDist = register("npmCleanDist", NpmTask::class) {
        args.set(listOf("run", "clean:dist"))
    }

    clean {
        // Depend on the custom npmClean task, the default gradle one deletes the "build" folder by default...
        // and the project build won't work without it.
        dependsOn(npmCleanDist)
        dependsOn(npmClean)
        // Disable clean task.
        enabled = false
        for(project in subprojects){
            dependsOn(project.tasks.named("clean"))
        }
    }
}
