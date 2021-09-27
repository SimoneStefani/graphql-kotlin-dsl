plugins {
    kotlin("jvm") version "1.5.31"
    `maven`
}

group = "com.github.simonestefani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

tasks.test {
    useJUnitPlatform()
}
