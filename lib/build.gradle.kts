import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version "2.1.21"
  `maven-publish`
}

group = "org.veupathdb.lib"
version = "1.2.0"

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

  withJavadocJar()
  withSourcesJar()
}

dependencies {
  implementation("commons-fileupload:commons-fileupload:1.5")

  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.8")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.8")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.8")
  implementation("org.glassfish.hk2:hk2-api:3.0.3")

  implementation("com.fasterxml.jackson.core:jackson-core:2.19.0")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.19.0")
  implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0")

  api("org.slf4j:slf4j-api:2.0.17")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url = uri("https://maven.pkg.github.com/VEuPathDB/lib-jersey-multipart-jackson-pojo")
      credentials {
        username = project.findProperty("github.username") as String? ?: System.getenv("GH_USERNAME")
        password = project.findProperty("github.token") as String? ?: System.getenv("GH_TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])
      pom {
        name.set("JaxRS Multipart for Jackson POJOs")
        description.set("Support for parsing Jackson POJOs from multipart/form-data request bodies.")
        url.set("https://github.com/VEuPathDB/lib-jersey-multipart-jackson-pojo")
        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("epharper@upenn.edu")
            url.set("https://github.com/foxcapades")
            organization.set("VEuPathDB")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/VEuPathDB/lib-jersey-multipart-jackson-pojo.git")
          developerConnection.set("scm:git:ssh://github.com/VEuPathDB/lib-jersey-multipart-jackson-pojo.git")
          url.set("https://github.com/VEuPathDB/lib-jersey-multipart-jackson-pojo")
        }
      }
    }
  }
}
