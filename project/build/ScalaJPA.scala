import sbt._

class ScalaJPAProject(info : ProjectInfo) extends DefaultProject(info) with IdeaProject {
  // Repositories
  val jbossRepo = "Jboss Public Repository" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
  val scalaToolsSnapshots = "Scala Tools Nexus Snapshot" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  val scalaToolsReleases = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"

  // From Lift (thanks, Indrajit!)
  // Add all the Scala version specific variations here
  lazy val (scalazVersion, specsVersion, scalacheckVersion) = buildScalaVersion match {
    case "2.8.0"     => ("5.0", "1.6.5",   "1.7")
    case "2.8.1"     => ("5.0", "1.6.7.2", "1.8")
    case _           => ("5.0", "1.6.8",   "1.8")
  }

  // Dependencies
  val jpa = "javax.persistence" % "persistence-api" % "1.0" % "provided"
  val jta = "geronimo-spec" % "geronimo-spec-jta" % "1.0-M1" % "provided"
  val h2 = "com.h2database" % "h2" % "1.3.152" % "test"
  val hibernate = "org.hibernate" % "hibernate-entitymanager" % "3.4.0.GA" % "test"
  val specs = "org.scala-tools.testing" %% "specs" % specsVersion % "test"
  val logback = "ch.qos.logback" % "logback-classic" % "0.9.27" % "test"

  override def managedStyle = ManagedStyle.Maven
  val publishTo = scalaToolsSnapshots
  //val publishTo = scalaToolsReleases

  Credentials(Path.userHome / ".ivy2" / ".credentials", log)

  // The following extra settings were copied from the ScalaCheck project definition at
  // http://code.google.com/p/scalacheck/source/browse/tags/1.7/project/build/ScalaCheckProject.scala?r=495

  override def packageDocsJar = defaultJarPath("-javadoc.jar")
  override def packageSrcJar= defaultJarPath("-sources.jar")

  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc)

  override def deliverScalaDependencies = Nil

  override def documentOptions = Nil

  val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  val docsArtifact = Artifact(artifactID, "docs", "jar", Some("javadoc"), Nil, None)


  // Insert extra info into the generated POM
  override def pomExtra =
  <xml:group>
    <inceptionYear>2008</inceptionYear>
    <name>Scala JPA Utility Framework</name>

    <description>
      This module provides some basic classes to simplify using JPA (Java
      Persistence) under Scala.
    </description>

    <url>http://scala-tools.org/mvnsites/scalajpa</url>

    <organization>
      <name>scala-tools.org</name>
      <url>http://scala-tools.org/mvnsites/scalajpa/</url>
    </organization>

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

    <ciManagement>
      <system>hudson</system>
      <url>http://hudson.scala-tools.org/job/scalajpa/</url>
    </ciManagement>

    <issueManagement>
      <system>GitHub</system>
      <url>http://github.com/dchenbecker/scalajpa/issues</url>
    </issueManagement>

    <scm>
      <connection>scm:git://github.com/dchenbecker/scalajpa.git</connection>
      <url>http://github.com/dchenbecker/scalajpa/tree/master</url>
    </scm>

  </xml:group>
}
