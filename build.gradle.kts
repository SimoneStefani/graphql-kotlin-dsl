plugins {
    kotlin("jvm") version "1.9.24"
    `maven`
}

group = "com.github.simonestefani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.27.0")
}

tasks.test {
    useJUnitPlatform()
}
