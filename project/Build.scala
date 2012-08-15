import sbt._
import Keys._
import com.jsuereth.pgp.sbtplugin.PgpKeys._

object ScalaJPABuild extends Build {
  val fullSettings: Seq[Project.Setting[_]] = Seq(
    name := "ScalaJPA",

    organization := "org.scala-libs",

    version := "1.4",

    crossScalaVersions := Seq("2.8.0", "2.8.1", "2.9.0", "2.9.0-1", "2.9.1", "2.9.2"),

    resolvers ++= Seq(
      "Jboss Public Repository" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
      "Typesafe Repository"     at "http://repo.typesafe.com/typesafe/releases/",
      "Maven Repo 1"            at "http://repo1.maven.org/maven2/",
      "Guiceyfruit"             at "http://guiceyfruit.googlecode.com/svn/repo/releases/",
      "Sonatype Snapshots"      at "https://oss.sonatype.org/content/repositories/snapshots/"
    ),
    
    libraryDependencies <<= (scalaVersion) { sv => {
      val specsVersion = sv match {
        case "2.8.0"             => "1.6.5"
        case "2.8.1"             => "1.6.7"
        case "2.9.0" | "2.9.0-1" => "1.6.8"
        case _                   => "1.6.9"
      }

      val specsScalaVersion = sv match {
        case "2.9.2" => "specs_2.9.1"
        case other   => "specs_" + other
      }
      
      Seq(
        "javax.persistence" % "persistence-api" % "1.0" % "provided",
        "geronimo-spec" % "geronimo-spec-jta" % "1.0-M1" % "provided",
        "com.h2database" % "h2" % "1.3.152" % "test",
        "org.hibernate" % "hibernate-entitymanager" % "3.4.0.GA" % "test",
        "org.scala-tools.testing" % specsScalaVersion % specsVersion % "test",
        "ch.qos.logback" % "logback-classic" % "0.9.27" % "test"
      )
    }},

    useGpg := true,

    publishMavenStyle := true,

    pomIncludeRepository := { _ => false },

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) 
        Some("snapshots" at nexus + "content/repositories/snapshots") 
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    publishArtifact in Test := false,

    pomExtra :=
      <xml:group>
        <inceptionYear>2008</inceptionYear>
        <name>Scala JPA Utility Framework</name>
      
        <description>
          This module provides some basic classes to simplify using JPA (Java
          Persistence) under Scala.
        </description>
      
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
