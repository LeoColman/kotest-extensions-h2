/*
 * Copyright 2020 Leonardo Colman Lopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either press or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.lang.System.getenv

plugins {
  kotlin("jvm") version libs.versions.kotlin.get()
  id("org.jetbrains.dokka") version libs.versions.kotlin.get()
  alias(libs.plugins.detekt)
  `maven-publish`
  signing
}

group = "br.com.colman"
version = getenv("RELEASE_VERSION") ?: "local"

dependencies {
  // H2
  api(libs.h2)

  // Kotest
  api(libs.kotest.framework.api)
  testImplementation(libs.bundles.kotest)
}

detekt {
  buildUponDefaultConfig = true
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
  useJUnitPlatform()
}

kotlin {
  explicitApi()
  jvmToolchain(8)
}

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")
  from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.registering(Jar::class) {
  dependsOn("dokkaHtml")
  archiveClassifier.set("javadoc")
  from("$buildDir/dokka")
}


publishing {
  repositories {

    maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
      credentials {
        username = getenv("OSSRH_USERNAME")
        password = getenv("OSSRH_PASSWORD")
      }
    }
  }

  publications {

    register("mavenJava", MavenPublication::class) {
      from(components["java"])
      artifact(sourcesJar.get())
      artifact(javadocJar.get())

      pom {
        name.set("kotest-extensions-h2")
        description.set("Integration for H2 Database with Kotest")
        url.set("https://www.github.com/LeoColman/kotest-extensions-h2")


        scm {
          connection.set("scm:git:http://www.github.com/LeoColman/kotest-extensions-h2")
          developerConnection.set("scm:git:http://github.com/LeoColman/kotest-extensions-h2")
          url.set("https://www.github.com/LeoColman/kotest-extensions-h2")
        }

        licenses {
          license {
            name.set("The Apache 2.0 License")
            url.set("https://opensource.org/licenses/Apache-2.0")
          }
        }

        developers {
          developer {
            id.set("LeoColman")
            name.set("Leonardo Colman Lopes")
            email.set("dev@leonardo.colman.com.br")
          }
        }
      }
    }
  }
}

val signingKey: String? by project
val signingPassword: String? by project

signing {
  useGpgCmd()
  if (signingKey != null && signingPassword != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
  }

  sign(publishing.publications["mavenJava"])
}
