plugins {
    application
    kotlin("jvm") version "1.7.22"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "com.vtence.asteroids"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "17.0.1"
    modules("javafx.controls")
}

application {
    mainClass.set("Asteroids")
}