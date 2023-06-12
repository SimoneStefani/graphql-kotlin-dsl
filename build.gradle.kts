plugins {
    kotlin("jvm") version "1.8.22"
    `maven`
}

group = "com.github.simonestefani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.26.1")
}

tasks.test {
    useJUnitPlatform()
}
