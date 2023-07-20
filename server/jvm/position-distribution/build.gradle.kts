plugins {
    distribution
}

dependencies {
    implementation(project(":position-config"))
    implementation(project(":position-dictionary-cache"))
    implementation(project(":position-eventhandler"))
    implementation(project(":position-messages"))
    implementation(project(":position-script-config"))
}

description = "position-distribution"

val distribution by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false
}

distributions {
    main {
        contents {
            // Octal conversion for file permissions
            val libPermissions = "600".toInt(8)
            val scriptPermissions = "700".toInt(8)
            into("position/bin") {
                from(configurations.runtimeClasspath)
                exclude("position-config*.jar")
                exclude("position-script-config*.jar")
                exclude("position-distribution*.jar")
                include("position-*.jar")
            }
            into("position/lib") {
                from("${project.rootProject.buildDir}/dependencies")
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                exclude("genesis-*.jar")
                exclude("position-*.jar")
                exclude("*.zip")

                fileMode = libPermissions
            }
            into("position/cfg") {
                from("${project.rootProject.projectDir}/position-config/src/main/resources/cfg")
                from(project.layout.buildDirectory.dir("generated/product-details"))
                filter(
                    org.apache.tools.ant.filters.FixCrLfFilter::class,
                    "eol" to org.apache.tools.ant.filters.FixCrLfFilter.CrLf.newInstance("lf")
                )
            }
            into("position/scripts") {
                from("${project.rootProject.projectDir}/position-config/src/main/resources/scripts")
                from("${project.rootProject.projectDir}/position-script-config/src/main/resources/scripts")
                filter(
                    org.apache.tools.ant.filters.FixCrLfFilter::class,
                    "eol" to org.apache.tools.ant.filters.FixCrLfFilter.CrLf.newInstance("lf")
                )
                fileMode = scriptPermissions
            }
            // Removes intermediate folder called with the same name as the zip archive.
            into("/")
        }
    }
}

// To give custom name to the distribution package
tasks {
    distZip {
        archiveBaseName.set("genesisproduct-position")
        archiveClassifier.set("bin")
        archiveExtension.set("zip")
    }
    copyDependencies {
        enabled = false
    }
}

artifacts {
    val distzip = tasks.distZip.get()
    add("distribution", distzip.archiveFile) {
        builtBy(distzip)
    }
}

publishing {
    publications {
        create<MavenPublication>("positionServerDistribution") {
            artifact(tasks.distZip.get())
        }
    }
}

description = "position-distribution"
