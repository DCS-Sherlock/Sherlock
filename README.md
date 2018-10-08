# Sherlock  [![Build Status: master](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=master)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=master)

<!----- Branch: --->

<!----- Version: --->


## Requirements
  - JDK 1.8
  - Gradle (4.10.2 included in repo)


## IDEA support
The gradle project comes with IntelliJ IDEA support. To use: 

```New project from existing sources -> Import project -> Import project from external model -> gradle -> uncheck "Create seperate module per source set"```


## Dependencies
To download the project dependencies use: `gradlew(.bat) deps`. They can be found in `build/lib/`


## Building
To build WITH tests use `gradlew(.bat) build`

To build WITHOUT tests use `gradlew(.bat) jar`

To create a distribution zip including jar and dependencies use `gradlew(.bat) distribute`

