# Introduction

Java-based on-screen display (OSD) for keyboard and mouse events.

This program displays keyboard and mouse events for the purpose of screencasting. While such software already exists, none meet all the following criteria:

* custom display size;
* easily positioned;
* show single events;
* show all mouse clicks;
* show scrolling;
* accurate modifier key states; and
* works with emulation software (e.g., [Sikuli](http://sikulix.com/)).

## Alternatives

* [QKeysOnScreen](https://github.com/ctrlcctrlv/QKeysOnScreen)

# Comparison

The following video compares KmCaster to [key-mon](https://github.com/critiqjo/key-mon):

![KmCaster Demo](images/kmcaster-01.gif "Comparison Video")

# Requirements

[OpenJDK](https://bell-sw.com/pages/downloads/#/java-14-current) version 14.0.1 or newer.

## Java Version

Depending on the distribution, Java 14 can be installed using:

```
sudo apt install openjdk-14-jdk
```

Switching from earlier versions of Java to Java 14 can be accomplished using one of the following:

```
sudo update-alternatives --config java
sudo archlinux-java set java-14-openjdk
```

# Download

Download the latest Java Archive file:

[Download](https://gitreleases.dev/gh/DaveJarvis/kmcaster/latest/kmcaster.jar)

# Running

After installing Java, run the program as follows:

``` bash
java -jar kmcaster.jar
```

