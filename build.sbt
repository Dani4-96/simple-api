organization := "ASTU"
name := "simple-api"
version := "0.1"
scalaVersion := "2.12.8"

val CatsVersion            = "1.5.0"
val CirceVersion           = "0.10.1"
val DoobieVersion          = "0.6.0"
val EnumeratumVersion      = "1.5.13"
val EnumeratumCirceVersion = "1.5.17"
val Http4sVersion          = "0.19.0"
val PureConfigVersion      = "0.9.2"
val LogbackVersion         = "1.2.3"

libraryDependencies ++= Seq(
  "org.typelevel"         %% "cats-core"            % CatsVersion,
  "io.circe"              %% "circe-generic"        % CirceVersion,
  "io.circe"              %% "circe-literal"        % CirceVersion,
  "io.circe"              %% "circe-generic-extras" % CirceVersion,
  "io.circe"              %% "circe-parser"         % CirceVersion,
  "io.circe"              %% "circe-java8"          % CirceVersion,
  "org.tpolecat"          %% "doobie-core"          % DoobieVersion,
  "org.tpolecat"          %% "doobie-postgres"      % DoobieVersion,
  "org.tpolecat"          %% "doobie-scalatest"     % DoobieVersion,
  "org.tpolecat"          %% "doobie-hikari"        % DoobieVersion,
  "com.beachape"          %% "enumeratum"           % EnumeratumVersion,
  "com.beachape"          %% "enumeratum-circe"     % EnumeratumCirceVersion,
  "org.http4s"            %% "http4s-blaze-server"  % Http4sVersion,
  "org.http4s"            %% "http4s-circe"         % Http4sVersion,
  "org.http4s"            %% "http4s-dsl"           % Http4sVersion,
  "com.github.pureconfig" %% "pureconfig"           % PureConfigVersion,
  "ch.qos.logback"        %  "logback-classic"      % LogbackVersion,
)

scalacOptions ++= Seq(
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
)