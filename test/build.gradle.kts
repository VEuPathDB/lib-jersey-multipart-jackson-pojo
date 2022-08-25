plugins {
  kotlin("jvm") version "1.7.0"
  id("application")
  id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

application {
  mainClass.set("derp.Main")
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  implementation(project(":multipart-jackson-pojo"))

  implementation("commons-fileupload:commons-fileupload:1.4")

  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.6")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.6")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.6")
  implementation("org.glassfish.hk2:hk2-api:3.0.3")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}