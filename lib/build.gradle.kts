import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin)
  `maven-publish`
}

group = "org.veupathdb.lib"
version = "2.0.0"

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

  withJavadocJar()
  withSourcesJar()
}

dependencies {
  api(libs.logging)

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
