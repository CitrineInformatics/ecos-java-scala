# ecos-java-scala

Scala drivers to Second Order Cone Programming (ECOS) Solver.

## LICENSE

This library is licensed under Apache.
See  LICENSE for more details.
ecos-java-scala project uses ecos as a submodule which is licensed under GNU GPL V3.
See https://github.com/embotech/ecos/blob/master/COPYING for more details on ECOS licensing.
amd and ldl JNI libraries are licensed under LGPL honoring original Tim Davis's license.

## What's available in the project

 * io.citrine.ecos.NativeECOS - interface to native ecos library with `solveSocp()`
 * io.citrine.ecos.QpSolver - solves quadratic programming problem
 * io.citrine.ecos.RunECOS - example usage of NativeECOS

## Build instructions

### Prerequisites

 * sbt
 * cmake
 * [optional] Java SDK 11 (for javah)

### Clone submodules

After cloning this repository, remember to clone submodules:
```
git submodule update --init --recursive
```

### Generate header file

To generate `src/native/include/io_citrine_ecos_NativeECOS.h` use `javah` task from `sbt-jni`:
```
sbt javah
```

The file is committed into the repository, so you need to invoke above only after some changes in the code.

### Publish artefacts to local repository

To publish Scala 2.12 and Scala 2.13 versions to local repository invoke:
```
sbt +publishLocal
```

The above will compile native ecos library and put that into jar. Platform is added as classifier to the published jar.
For Mac OS below files will be generated:
```
${HOME}/.ivy2/local/io.citrine/ecos_2.12/X.X.X/jars/ecos_2.12-Mac_OS_X.jar
${HOME}/.ivy2/local/io.citrine/ecos_2.13/X.X.X/jars/ecos_2.13-Mac_OS_X.jar
```

### Troubleshooting

For certain combinations of Java, CMake, and XCode the `FindJNI` module does not work properly, leading to the following error during compilation:

```
Could NOT find JNI (missing: JAVA_INCLUDE_PATH JAVA_INCLUDE_PATH2 JAVA_AWT_INCLUDE_PATH)
```

The solution is to add the following lines to `src/native/CMakeLists.txt`, setting some paths explicitly.
[Source](https://gitlab.kitware.com/cmake/cmake/-/issues/23364).

```
set(JAVA_HOME "$ENV{JAVA_HOME}")
set(JAVA_INCLUDE_PATH "${JAVA_HOME}/include")
set(JAVA_INCLUDE_PATH2 "${JAVA_HOME}/include/darwin")
set(JAVA_AWT_INCLUDE_PATH "${JAVA_HOME}/include")
```

## Supported features

+ Unconstrained quadratic minimization
+ Quadratic program with bound constraints
+ Quadratic program with L1 constraints
+ Quadratic program with equality constraints

## Usage in Scala project

Add library dependency including classifier, e.g.:
```
private val osNameClassifier = System.getProperty("os.name").replace(' ', '_').trim
private val osArchitecture = System.getProperty("os.arch").replace(' ', '_').trim
private val artifactClassifier = osNameClassifier + "_" + osArchitecture
[...]
libraryDependencies += "io.citrine" %% "ecos" % "X.X.X" classifier artifactClassifier
```

Native library will be loaded when instance of `NativeECOS` is created.

`io.citrine.ecos.RunECOS` contains runnable example.

## Credits

The following people have been, and are, involved in the development of ecos-java-scala:

+ Debasish Das
+ Stephen Boyd
+ Alexander Domahidi

The main technical idea behind ECOS is described in a short [paper](http://www.stanford.edu/~boyd/papers/ecos.html). 
More details are given in Alexander Domahidi's [PhD Thesis](http://e-collection.library.ethz.ch/view/eth:7611?q=domahidi)
in Chapter 9.
