# Introduction

Build instructions for the application.

# Requirements

Install the following:

* [JDK](https://jdk.java.net/16) 16 or greater
* [Gradle](https://gradle.org) 6.7 or greater
* [Git](https://git-scm.com) 2.31.1 or greater

Ensure both Gradle and Git may be run from the command-line. This may
entail setting the `PATH` environment variable.

# Build

This section describes how to build the application to produce a runnable
Java archive (`.jar`) file.

1. Open a terminal.
1. Run the following commands:
``` bash
git clone https://github.com/DaveJarvis/kmcaster
cd kmcaster
gradle clean build
```

The application is built as `build/libs/kmcaster.jar`.

