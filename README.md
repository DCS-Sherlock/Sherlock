# Sherlock  [![Build Status: master](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=master)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=master)

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

The project can also be tested directly within IntelliJ IDEA using either the automatically configured Spring Boot run configuration named `SherlockApplication`, or the gradle task `bootRun`
