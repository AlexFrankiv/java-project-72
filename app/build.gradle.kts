plugins {
    application
    id("org.sonarqube") version "7.3.1.8318"
    //checkstyle
    jacoco
    id("gg.jte.gradle") version "3.1.9"
}
jte {
    generate()
    sourceDirectory.set(file("src/main/resources/templates").toPath())
    targetDirectory.set(file("build/generated-sources/jte").toPath())
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
application {
    mainClass = "hexlet.code.App"
}
sonar {
    properties {
        property("sonar.projectKey", "AlexFrankiv_java-project-72")
        property("sonar.organization", "alexfrankiv")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.javalin:javalin:6.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("io.javalin:javalin-rendering:6.1.3")

    implementation("gg.jte:jte:3.1.12")

    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("com.h2database:h2:2.2.220")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
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
sourceSets {
    main {
        java {
            srcDir("build/generated-sources/jte")
        }
    }
}