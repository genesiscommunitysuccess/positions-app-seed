import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node")
}

tasks {
    val npmBuild = register("npmBuild", NpmTask::class) {
        args.set(listOf("run", "build"))
    }

    val packageDist = register<Zip>("packageDist") {
        archiveFileName.set("position-web-distribution-${version}.zip")
        from("dist")
    }

    assemble {
        dependsOn(npmBuild)
    }

    build {
      dependsOn(packageDist)
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
        enabled = true
        // Disable clean task.
        enabled = false
    }
}
