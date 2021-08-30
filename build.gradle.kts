plugins {
    kotlin("jvm") version "1.5.30"
    `maven`
}

group = "com.github.simonestefani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")
}

tasks.test {
    useJUnitPlatform()
}
