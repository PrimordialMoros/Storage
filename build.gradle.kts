plugins {
    java
    signing
    `maven-publish`
}

group = "me.moros"
version = "2.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.checkerframework", "checker-qual","3.15.0")
    compileOnly("com.zaxxer", "HikariCP", "4.0.3")
}

tasks {
    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    withType<Sign>().configureEach {
        onlyIf { !isSnapshot() }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            name.set(project.name.toLowerCase())
            description.set("A utility library to easily build and wrap HikariDataSources")
            url.set("https://github.com/PrimordialMoros/Storage")
            licenses {
                license {
                    name.set("The GNU Affero General Public License, Version 3.0")
                    url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                }
            }
            developers {
                developer {
                    id.set("moros")
                    name.set("Moros")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/PrimordialMoros/Storage.git")
                developerConnection.set("scm:git:ssh://git@github.com/PrimordialMoros/Storage.git")
                url.set("https://github.com/PrimordialMoros/Storage")
            }
        }
    }
    if (project.hasProperty("ossrhUsername") && project.hasProperty("ossrhPassword")) {
        val user = project.property("ossrhUsername") as String?
        val pass = project.property("ossrhPassword") as String?
        val repoUrl = if (isSnapshot()) uri("https://oss.sonatype.org/content/repositories/snapshots/") else uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        repositories {
            maven {
                credentials { username = user; password = pass }
                url = repoUrl
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
