# Sherlock  [![Build Status: CodeReduction](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=CodeReduction)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=CodeReduction)

<!----- Branch: --->

<!----- Version: --->


## Requirements
  - JDK 1.8
  - Gradle (4.10.2 included in repo)


## IDEA support
The gradle project comes with IntelliJ IDEA support. To use: 

```New project from existing sources -> Import project -> Import project from external model -> gradle -> uncheck "Create seperate module per source set"```


## Dependencies
To download the project dependencies use: `gradlew(.bat) deps`. They can be found in `/build/out/lib/`


## Building
To build WITH tests use `gradlew(.bat) build`

To build WITHOUT tests use `gradlew(.bat) jar`

To create a distribution zip including the jar and all dependencies use `gradlew(.bat) distribute`

## Testing
First ensure the dependencies have been downloaded using the `gradlew(.bat) deps` command

Then build the jar file using `gradlew(.bat) build` or `gradlew(.bat) jar`

The jar will be built into the `/build/out/` directory, the dependencies are in the correct location to run the jar file in place using `java -jar Sherlock-x.x.x.jar`
