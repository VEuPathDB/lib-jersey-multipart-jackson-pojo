import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version "2.2.20"
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

  implementation("commons-fileupload:commons-fileupload:1.5")

  implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.2")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.6")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.6")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.6")
  implementation("org.glassfish.hk2:hk2-api:3.1.1")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.20.0")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.4")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.4")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}