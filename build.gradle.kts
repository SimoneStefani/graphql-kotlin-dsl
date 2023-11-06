plugins {
    kotlin("jvm") version "1.9.20"
    `maven`
}

group = "com.github.simonestefani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
}

tasks.test {
    useJUnitPlatform()
}
