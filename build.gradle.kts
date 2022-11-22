plugins {
    java
    signing
    `maven-publish`
    id("org.checkerframework").version("0.6.19")
}

group = "me.moros"
version = "3.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.zaxxer", "HikariCP", "5.0.1")
}

tasks {
    withType<Sign>().configureEach {
        onlyIf { !isSnapshot() }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    named<Copy>("processResources") {
        from("LICENSE") {
            rename { "${project.name.toUpperCase()}_${it}"}
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            name.set(project.name)
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
        val snapshotUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        val releaseUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        repositories {
            maven {
                credentials { username = user; password = pass }
                url = if (isSnapshot()) snapshotUrl else releaseUrl
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
