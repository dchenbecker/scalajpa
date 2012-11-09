import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._

object ScalaJPABuild extends Build {
  val fullSettings: Seq[Project.Setting[_]] = Seq(
    name := "ScalaJPA",

    organization := "org.scala-libs",

    version := "1.4",

    description := "This module provides some basic classes to simplify using JPA (Java Persistence) under Scala.",

    crossScalaVersions := Seq("2.8.0", "2.8.1", "2.9.0", "2.9.0-1", "2.9.1", "2.9.2", "2.10.0-RC1"),

    scalaBinaryVersion <<= scalaVersion,

    resolvers ++= Seq(
      "Jboss Public Repository" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
      "Typesafe Repository"     at "http://repo.typesafe.com/typesafe/releases/",
      "Maven Repo 1"            at "http://repo1.maven.org/maven2/",
      "Guiceyfruit"             at "http://guiceyfruit.googlecode.com/svn/repo/releases/",
      "Sonatype Snapshots"      at "https://oss.sonatype.org/content/repositories/snapshots/",
      "sbt-plugin-releases"     at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"
    ),
    
    libraryDependencies <++= (scalaVersion) { sv => {
      val specsVersion = sv match {
        case "2.8.0" | "2.8.1" | "2.8.2" | "2.9.0" => "1.5"
        case "2.9.0-1" => "1.8.2"  
        case _         => "1.12.2"
      }

      Seq(
        "javax.persistence" % "persistence-api" % "1.0" % "provided",
        "geronimo-spec" % "geronimo-spec-jta" % "1.0-M1" % "provided",
        "com.h2database" % "h2" % "1.3.152" % "test",
        "org.hibernate" % "hibernate-entitymanager" % "3.4.0.GA" % "test",
        if (sv == "2.10.0-RC1") {
          "org.specs2" % "specs2_2.10.0-RC1" % "1.13-SNAPSHOT" % "test"
        } else {
          "org.specs2" %% "specs2" % specsVersion % "test"
        },
        "ch.qos.logback" % "logback-classic" % "0.9.27" % "test"
      )
    }},

    publishMavenStyle := true,

    pomIncludeRepository := { _ => false },

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) 
        Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    credentials += Credentials(Path.userHome / ".ivy2" / "sonatype.credentials"),

    publishArtifact in Test := false,

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
      
  )

  val scalajpa = Project(id = "ScalaJPA", base = file("."), settings = (Project.defaultSettings ++ fullSettings))
}
