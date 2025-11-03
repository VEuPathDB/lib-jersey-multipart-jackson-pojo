import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version "2.2.21"
  id("application")
  id("java")
}

group = "org.example"
version = "1.1.1"

repositories {
  mavenCentral()
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_21
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

application {
  mainClass.set("derp.Main")
}

dependencies {
  implementation(project(":multipart-jackson-pojo"))

  implementation("commons-fileupload:commons-fileupload:1.6.0")

  implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.2")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.1.11")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.1.11")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.1.11")
  implementation("org.glassfish.hk2:hk2-api:3.1.1")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.20.0")

  testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}