import Dependencies._

ThisBuild / scalaVersion       := "2.13.15"
ThisBuild / version            := "0.0.16"
ThisBuild / versionScheme      := Some("early-semver")
ThisBuild / organization       := "io.citrine"
ThisBuild / organizationName   := "Citrine Informatics"

ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  sys.env.getOrElse("GITHUB_ACTOR", ""),
  sys.env.getOrElse("GITHUB_TOKEN", "")
)

lazy val githubRepository = "GitHub Package Registry" at s"https://maven.pkg.github.com/CitrineInformatics/ecos-java-scala"

lazy val commonSettings = Seq(
  javah / target := sourceDirectory.value / "native" / "include",
  // CI drops libraries from other runners into lib_native/<platform>/ before packaging.
  // sbt-jni would also map lib_native/ under the host platform. Turn that second mapping off.
  Compile / unmanagedPlatformDependentNativeDirectories := Seq.empty,
  Test / unmanagedPlatformDependentNativeDirectories := Seq.empty,
  crossPaths := true,
  packageDoc / publishArtifact := false,
  publishTo := Some(githubRepository)
)

lazy val root = (project in file("."))
  .settings(commonSettings:_*)
  .settings(
    name := "ecos",
    libraryDependencies ++= Seq(breeze, jblas, scalatest),
    // The test loads the native library. A forked JVM keeps that out of sbt's class loaders.
    Test / fork := true
  )
  .enablePlugins(JniNative)
