import Dependencies._

ThisBuild / scalaVersion       := "2.13.10"
ThisBuild / version            := "0.0.10"
ThisBuild / organization       := "io.citrine"
ThisBuild / organizationName   := "Citrine Informatics"
ThisBuild / artifactClassifier := Some(osArchitecture)

// Publish artifacts based on CPU architecture
lazy val osArchitecture = System.getProperty("os.arch").replace(' ', '_').trim

lazy val commonSettings = Seq(
  javah / target := sourceDirectory.value / "native" / "include",
  crossPaths := true,
  packageDoc / publishArtifact := false,
  publishTo := {
    if (isSnapshot.value) {
      None
    } else {
      Some("Citrine Nexus" at "https://nexus.corp.citrine.io/repository/citrine/")
    }
  },
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)

lazy val root = (project in file("."))
  .settings(commonSettings:_*)
  .settings(
    name := "ecos",
    libraryDependencies ++= Seq(breeze, jblas)
  )
  .enablePlugins(JniNative)
