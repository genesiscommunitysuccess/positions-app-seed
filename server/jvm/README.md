# {{kebabCase pkgName}} App Server

###### Kotlin based variant

# Project structure

1. position-config -> holds the configuration, including the dictionary
2. position-script-config -> holds the scripts configuration
3. position-messages -> holds the message classes used in other modules e.g. eventhandler
4. position-eventhandler -> holds eventhandler classes
5. position-dictionary-cache -> generates dao classes
6. position-distribution -> builds the distribution zip

# Adding modules 

Please make sure to add new modules as a dependency to position-distribution

# Build generated code 

To build the generated code, please run `gradle build` on position-dictionary-cache, this will generated 
the following jars: 

* position-generated-sysdef
* position-generated-fields
* position-generated-dao
* position-generated-hft
* position-generated-view

# Tests 

To run tests specify gradle property "integrationTests" e.g. `./gradlew build -PintegrationTests`
