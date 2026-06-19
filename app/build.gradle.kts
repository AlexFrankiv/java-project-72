plugins {
    id("java")
    application
    id("org.sonarqube") version "7.1.0.6387"
    checkstyle
    jacoco
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
sonar {
    properties {
        property("sonar.projectKey", "AlexFrankiv_java-project-78")
        property("sonar.organization", "alexfrankiv")
    }
}
checkstyle {
    toolVersion = "10.12.4"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}