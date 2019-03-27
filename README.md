# Sherlock  [![Build Status: model_v2](https://travis-ci.org/DCS-Sherlock/Sherlock.svg?branch=model_v2)](https://travis-ci.org/DCS-Sherlock/Sherlock?branch=model_v2)

<!----- Branch: --->

<!----- Version: --->

## Documentation
The latest built documentation can be found at: https://dcs-sherlock.github.io/Sherlock/

## Requirements
  - JDK 1.8 or above
  - Gradle (4.10.2 included in repo)


## IDEA support
The gradle project comes with IntelliJ IDEA support. To use: 

```New project from existing sources -> Import project -> Import project from external model -> gradle -> uncheck "Create seperate module per source set"```


## Building
To build Sherlock use `gradlew(.bat) build`.

The standard and development jars, along with the war file for running Sherlock on a server, will be built into the `./build/out/` directory.

## Running/Testing
The compiled jar is executable and can be run either by double clicking in most operating systems, or using the `java -jar Sherlock-x.x.x.jar` command.

Alternatively, Sherlock can be run directly within the gradle environment using the command `gradlew(.bat) bootRun`, this has no prior requirements, does not require or produce jar/war files and can be run directly on a fresh clone of the repo. This command enables more detailed logging to console, which can also be achieved using the VM option: `-Dspring.output.ansi.enabled=ALWAYS -Dspring.profiles.active=dev`.

`SherlockClient` should be used as the starting class in a development environment.

## CSS
This project uses Sass (https://sass-lang.com/) to compile the CSS files for the web interface, the Sass files are stored in `src/main/sass`. Please do not edit the CSS files in `src/main/resources/static/css`, any changes you make to these files will be overwritten.

At the moment Gradle does not automatically compile the CSS files, so you will need an external program (see https://sass-lang.com/install) to compile any changes you make. Whatever program you use, set the source/input directory to `src/main/sass/output` and the output to `src/main/resources/static/css`. 