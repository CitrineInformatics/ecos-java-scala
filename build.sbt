import Dependencies._

ThisBuild / scalaVersion       := "2.13.15"
ThisBuild / version            := "0.0.10"
ThisBuild / versionScheme      := Some("early-semver")
ThisBuild / organization       := "io.citrine"
ThisBuild / organizationName   := "Citrine Informatics"
ThisBuild / artifactClassifier := Some(osNameClassifier + "_" + osArchitecture)

// Publish versions based on OS name/cpu architecture
lazy val osNameClassifier = System.getProperty("os.name").replace(' ', '_').trim
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
