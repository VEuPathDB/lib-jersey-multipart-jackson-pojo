plugins {
  kotlin("jvm") version "1.7.0"
  `maven-publish`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8

  withJavadocJar()
  withSourcesJar()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  implementation("commons-fileupload:commons-fileupload:1.4")

  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.4")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.4")
  implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.4")
  implementation("org.glassfish.jersey.media:jersey-media-multipart:3.0.4")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.4")
  implementation("org.glassfish.hk2:hk2-api:3.0.3")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])
      pom {
        name.set("S3 Workspaces")
        description.set("Workspaces backed by an S3 object store.")
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
