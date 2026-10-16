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
 * cmake 3.10 or newer
 * JDK 17
 * [optional] `javah` from a JDK 11 or older, to regenerate the JNI header

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

To publish the Scala 2.13 version to local repository invoke:
```
./local_dev/publish_local_with_lowered_log_levels.sh
```

The script lowers the ECOS log levels and builds the native library for this machine.
Then sbt packs that library into the jar and writes the jar to:
```
${HOME}/.ivy2/local/io.citrine/ecos_2.13/X.X.X/jars/ecos_2.13.jar
```

A local jar holds one native library, the one for this machine, under `native/<arch>-<kernel>/`.
On an Apple Silicon Mac that directory is `native/arm64-darwin/`.
The local ivy repository is first in sbt's resolver order, so mithril builds against the local jar at once.

### Continuous integration and publishing

The CI workflow builds the native library on four runners, one per platform:

| Runner | Platform directory |
|--------|--------------------|
| `ubuntu-latest` | `native/x86_64-linux/` |
| `ubuntu-24.04-arm` | `native/aarch64-linux/` |
| `macos-latest` | `native/arm64-darwin/` |
| `macos-15-intel` | `native/x86_64-darwin/` |

On each runner `sbt test` loads the fresh library and solves two small problems with known answers.
A final job collects the four libraries into one jar and checks that each directory is present.
On a push to `main` that job publishes the jar to GitHub Packages.
That jar has no classifier.
At runtime sbt-jni reads `uname -sm` and loads the library from the directory with that name.

GitHub Packages rejects a second publish of the same version, so bump `ThisBuild / version` in `build.sbt` in every PR.
A CI job on each PR runs `scripts/check-version-bump` and fails when the version matches main.

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

Add the library dependency:
```
libraryDependencies += "io.citrine" %% "ecos" % "X.X.X"
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
