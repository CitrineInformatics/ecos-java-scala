# AGENTS.md for ecos-java-scala

This repo holds Scala and JNI bindings to the ECOS conic solver. CI publishes the `io.citrine:ecos_2.13` jar to GitHub Packages, and mithril uses it.

See [README.md](README.md) for prerequisites, local publishing, and troubleshooting.

## Layout

| Path | Contents |
|------|----------|
| `src/main/scala/io/citrine/ecos/` | `NativeECOS`, `QpSolver`, and the `RunECOS` example |
| `src/native/` | JNI C source and `CMakeLists.txt` |
| `src/native/include/io_citrine_ecos_NativeECOS.h` | JNI header that `sbt javah` writes |
| `src/test/scala/io/citrine/ecos/QpSolverSpec.scala` | ScalaTest suite that loads the native library |
| `ecos/` | Git submodule of embotech/ecos, the C solver |
| `external/` | LDL and SuiteSparse sources for the native build |
| `scripts/` | CI checks and the log patch for ECOS |
| `local_dev/` | Script that publishes a jar to `~/.ivy2/local` |

## Commands

Install these tools first:

- sbt
- CMake 3.10 or later
- JDK 17

| Task | Command |
|------|---------|
| Fetch the submodule | `git submodule update --init --recursive` |
| Build the native library and test | `sbt nativeCompile test` |
| Regenerate the JNI header | `sbt javah` |
| Publish to `~/.ivy2/local` | `./local_dev/publish_local_with_lowered_log_levels.sh` |
| Check the version bump | `./scripts/check-version-bump` |

CI runs `./scripts/silence-ecos-logs` before it builds. The script turns off solver prints and profiling.

## Gotchas

| Topic | Rule |
|-------|------|
| Version bump | Bump the `ThisBuild / version` value of `build.sbt` in every PR. CI fails when it matches `main`. GitHub Packages rejects a second publish of the same version. |
| Submodule edits | `scripts/silence-ecos-logs` edits `ecos/include/glblopts.h` in the submodule. Leave that edit out of commits. |
| JNI header | Commit the header after you change a native method in `NativeECOS.scala`. `sbt javah` needs `javah` from JDK 11 or older. |
| Platforms | CI builds the library on four runners and packs all four into one jar. The jar holds `native/x86_64-linux/`, `native/aarch64-linux/`, `native/arm64-darwin/`, and `native/x86_64-darwin/`. |
| Local jar | A local publish holds the library for your machine alone. |
| Native loading | sbt-jni picks the library folder from `uname -sm` at runtime. Tests fork a JVM to load it. |
| Versions | sbt 1.10.4 and Scala 2.13.15. |
| Licenses | The wrapper is Apache 2.0. ECOS is GPL v3. The LDL and AMD sources are LGPL. |
