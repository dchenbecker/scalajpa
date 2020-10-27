import sbt._
import Keys._
// import com.typesafe.sbt.pgp.PgpKeys._

name := "ScalaJPA"

organization := "org.scala-libs"

version := "1.5"

description := "This module provides some basic classes to simplify using JPA (Java Persistence) under Scala."
crossScalaVersions := Seq("2.12.0", "2.13.1")

// Only for RC releases
//scalaBinaryVersion <<= scalaVersion,

// resolvers ++= Seq(
//   "Sonatype Release"        at "https://oss.sonatype.org/content/repositories/releases/",
//   "Sonatype Snapshots"      at "https://oss.sonatype.org/content/repositories/snapshots/",
//   "Jboss Public Repository" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
//   "Typesafe Repository"     at "http://repo.typesafe.com/typesafe/releases/",
//   "Maven Repo 1"            at "http://repo1.maven.org/maven2/",
//   "Guiceyfruit"             at "http://guiceyfruit.googlecode.com/svn/repo/releases/",
//   "sbt-plugin-releases"     at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"
// )

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
  "javax.persistence" % "javax.persistence-api" % "2.2" % "provided",
  "geronimo-spec" % "geronimo-spec-jta" % "1.0-M1" % "provided",
  "com.h2database" % "h2" % "1.4.200" % "test",
  "org.hibernate" % "hibernate-entitymanager" % "5.2.18.Final" % "test",
  "org.specs2" %% "specs2-core" % "4.9.4" % "test",
  "ch.qos.logback" % "logback-classic" % "0.9.27" % "test"
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

// publishTo <<= version { (v: String) =>
//   val nexus = "https://oss.sonatype.org/"
//   if (v.trim.endsWith("SNAPSHOT"))
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else
//     Some("releases"  at nexus + "service/local/staging/deploy/maven2")
// }

// credentials += Credentials(Path.userHome / ".ivy2" / "sonatype.credentials")

publishArtifact in Test := false

pomExtra :=
  <xml:group>
    <inceptionYear>2008</inceptionYear>

    <url>https://github.com/dchenbecker/scalajpa</url>

    <licenses>
      <license>
        <name>Apache License, ASL Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>

    <developers>
      <developer>
        <id>dchenbecker</id>
        <name>Derek Chen-Becker</name>
        <timezone>-7</timezone>
        <email>java [at] chen-becker.org</email>
        <roles>
          <role>BDFL</role>
        </roles>
      </developer>
    </developers>

    <issueManagement>
      <system>GitHub</system>
      <url>http://github.com/dchenbecker/scalajpa/issues</url>
    </issueManagement>

    <scm>
      <connection>scm:git:git@github.com:dchenbecker/scalajpa.git</connection>
      <url>http://github.com/dchenbecker/scalajpa/tree/master</url>
    </scm>

  </xml:group>

val scalajpa = Project(id = "ScalaJPA", base = file("."))

// ScalaJPA / Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.ScalaLibrary
