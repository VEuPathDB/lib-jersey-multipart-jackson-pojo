import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin)
  id("application")
  id("java")
}

group = "org.example"
version = "1.1.1"

repositories {
  mavenCentral()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
      password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
    }
  }
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

  implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.3")

  implementation(libs.commons.uploads)
  implementation(libs.jackson)
  implementation(libs.bundles.jersey)

  testImplementation(platform(libs.test.junit.bom))
  testImplementation(libs.test.junit.api)
  testRuntimeOnly(libs.test.junit.engine)
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}