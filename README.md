# Sherlock  [![Build Status: model_v2](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=model_v2)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=model_v2)

<!----- Branch: --->

<!----- Version: --->

##Documentation
The latest built documentation can be found at: https://dcs-sherlock.github.io/Sherlock/

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

## CSS
This project uses Sass (https://sass-lang.com/) to compile the CSS files for the web interface, the Sass files are stored in `src/main/sass`. Please do not edit the CSS files in `src/main/resources/static/css`, any changes you make to these files will be overwritten.

At the moment Gradle does not automatically compile the CSS files, so you will need an external program (see https://sass-lang.com/install) to compile any changes you make. Whatever program you use, set the source/input directory to `src/main/sass/output` and the output to `src/main/resources/static/css`. 