# Sherlock  [![Build Status: devel](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=devel)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=devel)

<!----- Branch: --->

<!----- Version: --->


## Requirements
  - JDK 1.8
  - Gradle (4.10.2 included in repo)


## IDEA support
The gradle project comes with IntelliJ IDEA support. To use: 

```New project from existing sources -> Import project -> Import project from external model -> gradle -> uncheck "Create seperate module per source set"```


## Building
To build WITH tests use `gradlew(.bat) build`

To build WITHOUT tests use `gradlew(.bat) bootJar`

The jar will be built into the `/build/out/` directory, contains all the required dependencies and can be run using `java -jar Sherlock-x.x.x.jar`

## Running/Testing
Sherlock can be run within the gradle environment using the command `gradlew(.bat) bootRun`, this has no prior requirements and can be run directly on a clone of the repo

When developing use `SherlockClient` as the main class and use following VM options to enable more console output `-Dspring.output.ansi.enabled=ALWAYS -Dspring.profiles.active=dev`
